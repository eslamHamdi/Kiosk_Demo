package com.eslam.csp1401_test

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class MainViewModel(val app:Application): AndroidViewModel(app) {



    var eventList:MutableLiveData<List<EventItem?>> = MutableLiveData()

    var eventUpdates:MutableLiveData<MutableList<EventItem?>> = MutableLiveData()
    var updatesOnly:MutableLiveData<MutableList<EventItem?>> = MutableLiveData()

    var deltaLink:MutableLiveData<String?> = MutableLiveData()
    var nextLink:MutableLiveData<String?> = MutableLiveData()

    fun getEvents(accessToken:String?,url:String)
    {
//        val List:Array<String> = arrayOf("attendees","body","bodyPreview",
//            "location","end","id","start","subject")

        val list = "attendees,body,bodyPreview,location,end,id,start,subject"

        viewModelScope.launch {
            try {
                if (accessToken != null)
                {
                    val map:MutableMap<String,String> = mutableMapOf()
                    map["Authorization"] = "Bearer $accessToken"
                    val result = GraphClient.getService(url).getEvents("Bearer $accessToken")

                        eventList.value = result.value!!

                }

            }catch (e:Exception)
            {

                Log.e(null, "getEvents: ${e.message} ")
            }

        }
    }

    fun getUpdates(token:String,startDate:String,endDate:String,url:String)
    {

        viewModelScope.launch {

            try {

                val result = GraphClient.getService(url).getUpdatedEvents("Bearer $token",startDate,endDate)

               deltaLink.value = result.odataDeltaLink
                nextLink.value = result.odataNextLink
                Log.e("nextLink", "getUpdates: ${result.odataNextLink} ", )
                val events = result.events

                if (events != null)
                {
                    eventUpdates.value = events!!
                }


            }catch (e:Exception)
            {
                Log.e("deltaTest", "getUpdates: ${e.message} ", )
            }
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun getUpdatesUsingTheStateTokens(token:String, stateLink:String, baseUrl:String)
    {
        viewModelScope.launch {
            try {

                val result = GraphClient.getService(baseUrl).getUpdatesUsingStateToken(token,stateLink)
                val updatedEventsOnly = result.events
                deltaLink.value = result.odataDeltaLink
                nextLink.value = result.odataNextLink
                if (updatedEventsOnly != null && updatedEventsOnly.isNotEmpty())
                {
                    Log.e("TAG", "getUpdatesUsingTheStateTokens: ${result.events} ", )
                    updatesOnly.value = updatedEventsOnly!!
                }



            }catch (e:Exception)
            {
                Log.e("updateUsingStateToken", "getUpdatesUsingTheStateTokens:  ${e.message} ", )
            }
        }
    }


}