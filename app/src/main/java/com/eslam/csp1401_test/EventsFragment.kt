package com.eslam.csp1401_test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.eslam.csp1401_test.databinding.FragmentEventsBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


private const val BASE_URL = " https://graph.microsoft.com/v1.0/me/"

class EventsFragment : Fragment() {

    lateinit var binding :FragmentEventsBinding

    val viewModel:MainViewModel by activityViewModels()

    val authHelper:AuthenticationHelper = AuthenticationHelper

    val args:EventsFragmentArgs by navArgs()

    lateinit var adapter: EventsAdapter

    private var nextLink:String? = null
    private var deltaLink:String? = null
    private var updatedToken:String? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventsBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EventsAdapter()
        binding.recycler.adapter = adapter
        val token = args.userName
       // viewModel.getEvents(token,BASE_URL)
        val start = getISO8601StringForStartDate()
        val end = getISO8601StringForEndDate()
        getUpdatedEvents(args.userName!!,start!!,end!!)



        //requireActivity().actionBar?.title = args.userName
        Log.d(null, "onViewCreated: $token")


        viewModel.eventList.observe(viewLifecycleOwner){
            if (it != null)
            {
                Log.e(null, "onViewCreated: $it")
                adapter.submitList(it)

            }

        }

        authHelper.tokenState.observe(viewLifecycleOwner){
            updatedToken = it
        }


       // getUpdatedEvents(args.userName!!,start!!,end!!)

        viewModel.eventUpdates.observe(viewLifecycleOwner){
            Log.e("updatedLIst", "onViewCreated: $it ", )
            adapter.submitList(it)
        }

        viewModel.deltaLink.observe(viewLifecycleOwner){

            deltaLink = it

            Log.e("DeltaLink", "onViewCreated: $it ", )
        }

        viewModel.updatesOnly.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }


    private fun getUpdatedEvents(token:String, start:String,end:String) {
        viewModel.getUpdates(token,start,end, BASE_URL)
    }

    fun getISO8601StringForStartDate(): String? {
        val now = Date()
        return getISO8601StringForDate(now)
    }

    fun getISO8601StringForEndDate(): String? {
        val today = Date()

        val calendar = Calendar.getInstance()
        calendar.time = today

        calendar.add(Calendar.MONTH, 1)
        calendar[Calendar.DAY_OF_MONTH] = 1
        calendar.add(Calendar.DATE, -1)

        val lastDayOfMonth = calendar.time

        return getISO8601StringForDate(lastDayOfMonth)
    }

    private fun getISO8601StringForDate(date: Date): String? {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(date)
    }

    //using delta end point + alarm Manager
    fun getUpdatedRange()
    {
        deltaLink?.let { updatedToken?.let { it1 -> viewModel.getUpdatesUsingTheStateTokens(it1, it) } }

    }

}


