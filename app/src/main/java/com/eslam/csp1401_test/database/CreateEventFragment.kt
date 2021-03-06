package com.eslam.csp1401_test.database

import android.R
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.*
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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class CreateEventFragment : Fragment(),TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener
     {


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
        setHasOptionsMenu(true)
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
            dateTimeStatus = false
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
            val address = binding.attendeeEmail.text.toString()
            val name = binding.attendeeName.text.toString()
            val type = binding.attendeeType.text.toString()

            val emailAddress:EmailAddress = EmailAddress(address =address , name = name)
            val attendeesItem = AttendeesItem(emailAddress = emailAddress, type = type)

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

        val hour = timeConvert(hourOfDay)
        val min = timeConvert(minute)
        val sec = timeConvert(second)

        if (dateTimeStatus)
        {
            startTime = "$hour:$min:$sec.0000000"

            val editable = Editable.Factory().newEditable(startTime)

            binding.startTime.text = editable

        }else {

            endTime = "$hour:$min:$sec.0000000"

            val editable = Editable.Factory().newEditable(endTime)

            binding.endTime.text = editable


        }



    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        val editedMonth = monthOfYear +1
        val month = timeConvert(editedMonth)
        val day = timeConvert(dayOfMonth)

        if (dateTimeStatus)
        {
            startDate = "$year-$month-$day"
            val editable = Editable.Factory().newEditable(startDate)
            binding.startDate.text = editable
        }else
        {
            endDate = "$year-$month-$day"
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
          val tbd = TimePickerDialog.newInstance(this,now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),true)

        tbd.show(parentFragmentManager!!, "Timepickerdialog")

    }
   // 2021-12-08T08:00:00.0000000  ${startDate}T$startTime  ${endDate}T$endTime

       fun saveEvent()
       {

           val start = Start(dateTime = "${startDate}T$startTime",timeZone = "Asia/Dubai")
           val end = End(dateTime = "${endDate}T$endTime", timeZone = "Asia/Dubai" )
           val subject = binding.eventTitle.text.toString()
           eventItem = EventItem(subject =subject ,attendees = attendeeList,
           start = start, end = end)
               if (token != null)
               {
                   Log.e(null, "saveEvent:$eventItem ", )
                   viewModel.saveEvent(" https://graph.microsoft.com/v1.0/me/",token,eventItem)

                   findNavController().navigate(CreateEventFragmentDirections.actionCreateEventFragmentToEventsFragment(token))
               }

       }

    private fun getISO8601StringForDate(date: Date): String? {
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(date)
    }

    fun timeConvert(time:Int):String
    {
        if (time < 10 )
        {
            return "0${time}"
        }

        return time.toString()
    }

         override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
             super.onCreateOptionsMenu(menu, inflater)
             menu.clear()
         }


}