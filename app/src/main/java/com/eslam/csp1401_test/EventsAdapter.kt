package com.eslam.csp1401_test

import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eslam.csp1401_test.databinding.EventItemBinding

class EventsAdapter(var list:List<EventItem?>):RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    lateinit var binding: EventItemBinding


    inner class ViewHolder(binding:EventItemBinding):RecyclerView.ViewHolder(binding.root)
    {
        fun bind(event:EventItem)
        {
            binding.start.text = event.start?.dateTime.toString()
            Log.e(null, "bind: ${event.start?.dateTime.toString()} ", )
            binding.end.text = event.end?.dateTime.toString()
             val br:StringBuffer= StringBuffer();
            for ( desc:AttendeesItem? in event.attendees!!) {

                if (desc != null) {
                    br.append("\n Email: ${desc.emailAddress?.address} \n Status: ${desc.status?.response} \n")
                }

        }
            binding.attend.text = br


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.event_item,parent,false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val item = list[position]
        if (item != null) {
            holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun addList(list:List<EventItem?>)
    {
        this.list = list
        notifyDataSetChanged()

    }
}