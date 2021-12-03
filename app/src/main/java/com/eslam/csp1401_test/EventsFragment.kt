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


class EventsFragment : Fragment() {

    lateinit var binding :FragmentEventsBinding

    val viewModel:MainViewModel by activityViewModels()

    //lateinit var list:List<EventModel>

    val args:EventsFragmentArgs by navArgs()

    lateinit var adapter: EventsAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventsBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EventsAdapter(listOf())
        binding.recycler.adapter = adapter
        val token = args.userName
        viewModel.getEvents(token)

        //requireActivity().actionBar?.title = args.userName
        Log.d(null, "onViewCreated: $token", )


        viewModel.eventList.observe(viewLifecycleOwner){
            if (it != null)
            {
                //adapter= EventsAdapter(it)

            }

        }
    }

    }


