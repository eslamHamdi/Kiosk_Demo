package com.eslam.csp1401_test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.eslam.csp1401_test.databinding.FragmentEventsBinding


class EventsFragment : Fragment() {

    lateinit var binding :FragmentEventsBinding

    val viewModel:MainViewModel by activityViewModels()

    lateinit var list:List<EventModel>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEventsBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getEvents()

        viewModel.eventList.observe(viewLifecycleOwner){
            if (it != null)
            {
                val adapter= EventsAdapter(it)
                binding.recycler.adapter = adapter
            }

        }
    }

    }


