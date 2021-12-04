package com.eslam.csp1401_test

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class MainViewModel(val app:Application): AndroidViewModel(app) {



    var eventList:MutableLiveData<List<EventItem?>> = MutableLiveData()

    fun getEvents(accessToken:String?)
    {
        viewModelScope.launch {
            try {
                if (accessToken != null)
                {
                    val map:MutableMap<String,String> = mutableMapOf()
                    map.put("Authorization","Bearer $accessToken")
                    val result = GraphClient.retrofitService.getEvents("Bearer $accessToken")

                        eventList.value = result.value!!

                }

            }catch (e:Exception)
            {

                Log.e(null, "getEvents: ${e.message} ")
            }

        }
    }


}