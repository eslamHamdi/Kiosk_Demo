package com.eslam.csp1401_test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {


    var accessToken:String? = null


    var eventList:MutableLiveData<List<EventModel>> = MutableLiveData()

    fun getEvents()
    {
        viewModelScope.launch {
            try {
                if (accessToken != null)
                {
                    val result = GraphClient.retrofitService.getEvents(accessToken!!)
                    eventList.value = result
                }

            }catch (e:Exception)
            {

            }

        }
    }
}