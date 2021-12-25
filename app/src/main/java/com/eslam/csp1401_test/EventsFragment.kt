package com.eslam.csp1401_test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.eslam.csp1401_test.database.EventEntity
import com.eslam.csp1401_test.databinding.FragmentEventsBinding
import kotlinx.coroutines.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


private const val BASE_URL = " https://graph.microsoft.com/v1.0/me/"

class EventsFragment : Fragment(),EventsAdapter.EventUpdate {

    lateinit var binding :FragmentEventsBinding

    val viewModel:MainViewModel by activityViewModels()

    val authHelper:AuthenticationHelper = AuthenticationHelper

    val args:EventsFragmentArgs by navArgs()
    var token:String? =null

    lateinit var adapter: EventsAdapter
    private var errorMessage = ""

    private var nextLink:String? = null
    private var deltaLink:String? = null
    //private var updatedToken:String? = null



    private var repeatingJob:Job = Job()

    //private var updatedList:MutableList<EventItem?> = mutableListOf()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(null, "onCreateView: called ")
        binding = FragmentEventsBinding.inflate(inflater)
        val start = getISO8601StringForStartDate()
        val end = getISO8601StringForEndDate()
        getUpdatedEvents(args.userName!!,start!!,end!!)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        adapter = EventsAdapter()
        binding.recycler.adapter = adapter
        token = args.userName
        adapter.eventUpdate = this






    Log.d(null, "onViewCreated: $token")



        authHelper.tokenState.observe(viewLifecycleOwner){
            token = it
            Log.e("updatedToken", "onViewCreated: $it ", )
        }

        authHelper.exceptionState.observe(viewLifecycleOwner){

            if (it != null) {
                Toast.makeText(this.requireContext(),it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }


        viewModel.eventUpdates.observe(viewLifecycleOwner){ it ->
            Log.e("updatedLIst", "onViewCreated: $it ", )


           adapter.submitList(it)
        }

        viewModel.deltaLink.observe(viewLifecycleOwner){

            deltaLink = it

            Log.e("DeltaLink", "onViewCreated: $it ", )
        }

        viewModel.nextLink.observe(viewLifecycleOwner){
            if (it != null)
            {
                viewModel.getUpdatesUsingTheStateTokens(token!!,it!!, BASE_URL)
            }

        }
        viewModel.networkError.observe(viewLifecycleOwner) {
            if (it != null) {

                errorMessage = it
            }
        }

        binding.addEvent.setOnClickListener {

            findNavController().navigate(EventsFragmentDirections.actionEventsFragmentToCreateEventFragment(token))
        }

    }


    private fun getUpdatedEvents(token:String, start:String,end:String) {
        Log.d("Conflict2", "getUpdatedEvents: ")
        if (nextLink == null && deltaLink == null)
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



    private fun startRepeatingJob(timeInterval: Long): Job {

        return CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                // add your task here
                if (deltaLink != null)
                {
                    delay(timeInterval)
                    Log.d("Conflict1", "startRepeatingJob: ")
                    if (errorMessage != "HTTP 401 Unauthorized")
                    {
                        viewModel.getUpdatesUsingTheStateTokens(token!!,deltaLink!!, BASE_URL)
                    }else{
                        Log.e("firingSilentToken", "updatingAccessToken: ", )
                        authHelper.acquireTokenSilently(this@EventsFragment.requireActivity())
                    }


                }

            }
        }
    }

    //calling for updates every 1 min
    override fun onResume() {
        super.onResume()

        repeatingJob = startRepeatingJob(60000L)
        Log.e("repeatingUpdates", "onResume: called", )
        updatingAccessToken(1200000L)
        resettingDataBase(43200000L)
    }

    override fun onPause() {
        super.onPause()
        repeatingJob.cancel()
    }


    //updating Access Token every 45min
    private fun updatingAccessToken(timeInterval: Long) {

        val activity = this.requireActivity()
        viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                // add your task here
                delay(timeInterval)
                    Log.e("firingSilentToken", "updatingAccessToken: ", )
                    authHelper.acquireTokenSilently(activity)


            }
        }
    }


    //resetting after 12hrs
    private fun resettingDataBase(timeInterval: Long) {

        viewLifecycleOwner.lifecycleScope.launch {
            while (isActive) {
                // add your task here
                delay(timeInterval)
                Log.e(null, "resettingDataBase: entered", )
                val start = getISO8601StringForStartDate()
                val end = getISO8601StringForEndDate()
                viewModel.clearDataBase()
                viewModel.getUpdates(token!!,start!!,end!!, BASE_URL)

            }
        }
    }

    override fun onSelect(item: EventEntity) {

        val bundle = Bundle()

        if (token != null)
        {
            bundle.putString("token",token!!)
            bundle.putParcelable("event",item)

            findNavController().navigate(R.id.action_eventsFragment_to_editFragment,bundle)
        }

    }
}


