package com.eslam.csp1401_test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.eslam.csp1401_test.databinding.EventItemBinding

class EventsAdapter(val list:List<EventModel>):RecyclerView.Adapter<EventsAdapter.ViewHolder>() {

    lateinit var binding: EventItemBinding


    inner class ViewHolder(binding:EventItemBinding):RecyclerView.ViewHolder(binding.root)
    {
        fun bind(event:EventModel)
        {
            binding.start.text = event.start?.dateTime
            binding.end.text = event.end?.dateTime
            binding.preview.text = event.bodyPreview

            notifyDataSetChanged()
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),R.layout.event_item,parent,false)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      val item = list[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return list.size
    }

}