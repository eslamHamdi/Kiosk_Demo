package com.eslam.csp1401_test.database

import android.R
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.eslam.csp1401_test.*
import com.eslam.csp1401_test.databinding.FragmentCreateEventBinding
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*


class CreateEventFragment : Fragment(), TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener {


    lateinit var binding: FragmentCreateEventBinding

    lateinit var eventItem :EventItem

    private var attendeeList:ArrayList<AttendeesItem> = arrayListOf()

    private var attendeesEmailList:ArrayList<String> = arrayListOf()

    lateinit var arrayAdapter:ArrayAdapter<String>

    private var startTime:String =""
    private var startDate:String =""
    private var endTime:String =""
    private var endDate:String =""

   val args: CreateEventFragmentArgs by navArgs()

    private lateinit var token:String

    var dateTimeStatus = false

    val viewModel:MainViewModel by activityViewModels()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = FragmentCreateEventBinding.inflate(inflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startDate.showSoftInputOnFocus = false
        binding.startTime.showSoftInputOnFocus = false

        binding.endDate.showSoftInputOnFocus = false
        binding.endTime.showSoftInputOnFocus = false

        token = args.accessToken!!

        arrayAdapter = ArrayAdapter(
            this.requireContext(),
            R.layout.simple_list_item_1,
            attendeesEmailList
        )

        binding.attendees.adapter = arrayAdapter


        binding.startDate.setOnClickListener {
            dateTimeStatus = true
            openDatePicker()
        }

        binding.startTime.setOnClickListener {

            dateTimeStatus = true
            openTimePicker()
        }

        binding.endDate.setOnClickListener {
            dateTimeStatus = false
            openDatePicker()
        }

        binding.endTime.setOnClickListener {
            openTimePicker()
        }

        binding.attendeeSave.setOnClickListener {
            saveAttendees()
        }

        binding.eventSave.setOnClickListener {
            saveEvent()
        }


    }

    private fun saveAttendees() {

        if (binding.attendeeEmail.text.isNullOrBlank() || binding.attendeeName.text.isNullOrBlank() || binding.attendeeType.text.isNullOrBlank())
        {
            Toast.makeText(this.requireContext(),"Please Enter All Attendee Info ",Toast.LENGTH_LONG).show()
        }else
        {
            val emailAddress:EmailAddress = EmailAddress(address = binding.attendeeEmail.toString(), name = binding.attendeeName.toString())
            val attendeesItem = AttendeesItem(emailAddress = emailAddress, type = binding.attendeeType.toString())

            val attendeeEmail = binding.attendeeEmail.text.toString()

            attendeesEmailList.add(attendeeEmail)
            //arrayAdapter.addAll(attendeesEmailList)
            arrayAdapter.notifyDataSetChanged()

            attendeeList.add(attendeesItem)
            binding.attendeeEmail.text?.clear()
            binding.attendeeName.text?.clear()
            binding.attendeeType.text?.clear()
        }

    }


    //2021-12-08T08:00:00.0000000

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {

        if (dateTimeStatus)
        {
            startTime = "$hourOfDay:$minute:$second:0000000"

            val editable = Editable.Factory().newEditable(startTime)

            binding.startTime.text = editable

        }else {

            endTime = "$hourOfDay:$minute:$second:0000000"

            val editable = Editable.Factory().newEditable(endTime)

            binding.endTime.text = editable


        }



    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        if (dateTimeStatus)
        {
            startDate = "$year-$monthOfYear-$dayOfMonth"
            val editable = Editable.Factory().newEditable(startDate)
            binding.startDate.text = editable
        }else
        {
            endDate = "$year-$monthOfYear-$dayOfMonth"
            val editable = Editable.Factory().newEditable(endDate)
            binding.endDate.text = editable

        }

    }


    fun openDatePicker()
    {
        val now: Calendar = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
            this,
            now.get(Calendar.YEAR),  // Initial year selection
            now.get(Calendar.MONTH),  // Initial month selection
            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        )
// If you're calling this from a support Fragment
// If you're calling this from a support Fragment
        dpd.show(parentFragmentManager!!, "Datepickerdialog")
    }

    fun openTimePicker()
    {
        val now: Calendar = Calendar.getInstance()

        val tpd = TimePickerDialog.newInstance(this,now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),now.get(Calendar.SECOND),true)

        tpd.show(parentFragmentManager,"TimePicker")

    }


       fun saveEvent()
       {
           val start = Start(dateTime = "${startDate}'T'$startTime",timeZone = "Asia/Dubai")
           val end = End(dateTime = "$endDate'T'$endTime", timeZone = "Asia/Dubai" )
           eventItem = EventItem(subject = binding.eventTitle.toString(),attendees = attendeeList,
           start = start, end = end)
               if (token != null)
               {
                   viewModel.saveEvent(" https://graph.microsoft.com/v1.0/me/",token,eventItem)

                   findNavController().navigate(CreateEventFragmentDirections.actionCreateEventFragmentToEventsFragment(token))
               }

       }


}