package com.eslam.csp1401_test

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class MainViewModel: ViewModel() {


    //var accessToken:String? = null


    var eventList:MutableLiveData<List<EventModel>> = MutableLiveData()
   // private var mClient: GraphServiceClient<*>? = null



    fun getEvents(accessToken:String?)
    {
        viewModelScope.launch {
            try {
                if (accessToken != null)
                {
                    val result = GraphClient.retrofitService.getEvents("Bearer $accessToken!!")

                        eventList.value = result

                }

            }catch (e:Exception)
            {

                Log.e(null, "getEvents: ${e.message} ")
            }

        }
    }
}