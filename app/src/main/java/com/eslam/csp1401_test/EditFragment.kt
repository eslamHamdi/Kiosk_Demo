package com.eslam.csp1401_test

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.eslam.csp1401_test.database.EventEntity
import com.eslam.csp1401_test.databinding.FragmentEditBinding
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.*

private const val BASE_URL = " https://graph.microsoft.com/v1.0/me/"
class EditFragment : Fragment(), TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    lateinit var binding:FragmentEditBinding
    private var startTime:String =""
    private var startDate:String =""
    private var endTime:String =""
    private var endDate:String =""
    var dateTimeStatus = false


    val viewModel:MainViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val event:EventEntity = arguments?.get("event") as EventEntity
        val token:String = arguments?.get("token") as String



        binding.startEdit.setOnClickListener {
            dateTimeStatus = true
            openDatePicker()
        }
        binding.editTime.setOnClickListener {
            dateTimeStatus = true
            openTimePicker()
        }
        binding.endEdit.setOnClickListener {
            dateTimeStatus = false
            openDatePicker()
        }
        binding.timeEnd.setOnClickListener {
            dateTimeStatus = false
            openTimePicker()
        }

        binding.updateTime.setOnClickListener {

            var start: Start? = null
            var end: End? = null
            if (startDate.isEmpty()) {

                startDate = event.startDateTime?.substringBefore("T")!!
            }

            if (endDate.isEmpty())
            {
                endDate = event.endDateTime?.substringBefore("T")!!
            }

            Log.e(null, "onViewCreated: $startTime  $endTime", )
            if (startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this.requireContext(),"Please Enter valid Timing",Toast.LENGTH_LONG).show()
            }else {
                start = Start(dateTime = "${startDate}T$startTime",timeZone = "Asia/Dubai")

                end = End(dateTime = "${endDate}T$endTime", timeZone = "Asia/Dubai" )

                //val eventItem = EventItem(start = start, end = end)

                val dateTime = DateTime(start,end)



//               val startBody =
//                   Gson().toJson(eventItem.start).toRequestBody(okhttp3.MultipartBody.FORM)
//
//                val endBody = Gson().toJson(eventItem.end).toRequestBody(okhttp3.MultipartBody.FORM)


                lifecycleScope.launch {

                    viewModel.editEvent(token,dateTime,BASE_URL,event.id)
                }
            }


        }


    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        val hour = timeConvert(hourOfDay)
        val min = timeConvert(minute)
        val sec = timeConvert(second)

        if (dateTimeStatus)
        {
            startTime = "$hour:$min:$sec.0000000"



        }else {

            endTime = "$hour:$min:$sec.0000000"


        }
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {

        val editedMonth = monthOfYear +1
        val month = timeConvert(editedMonth)
        val day = timeConvert(dayOfMonth)

        if (dateTimeStatus)
        {
            startDate = "$year-$month-$day"

        }else
        {
            endDate = "$year-$month-$day"

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






    fun timeConvert(time:Int):String
    {
        if (time < 10 )
        {
            return "0${time}"
        }

        return time.toString()
    }



}

data class DateTime(

    //@field:SerializedName("start")
    val start :Start,
   // @field:SerializedName("end")
    val end:End)