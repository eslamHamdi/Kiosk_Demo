package com.eslam.csp1401_test

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import androidx.room.Room
import com.eslam.csp1401_test.database.EventDataBase
import com.eslam.csp1401_test.database.EventEntity
import com.eslam.csp1401_test.database.toEntity
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.RequestBody


class MainViewModel(val app:Application): AndroidViewModel(app) {



    var eventList:MutableLiveData<List<EventItem?>> = MutableLiveData()


    var updatesOnly:MutableLiveData<List<EventEntity?>> = MutableLiveData()

    var networkError:MutableLiveData<String?> = MutableLiveData()

    var deltaLink:MutableLiveData<String?> = MutableLiveData()
    var nextLink:MutableLiveData<String?> = MutableLiveData()
    private val dataBase = Room.databaseBuilder(app,EventDataBase::class.java,"EventsDataBase").fallbackToDestructiveMigration().build()

    val dao = dataBase.getEventsDao()

    var eventUpdates: LiveData<List<EventEntity?>> = dao.getAllEvents().map {
        it.filter { entity->
            entity.removedReason == null
        }
    }.asLiveData(viewModelScope.coroutineContext)


//    fun getEvents(accessToken:String?,url:String)
//    {
////        val List:Array<String> = arrayOf("attendees","body","bodyPreview",
////            "location","end","id","start","subject")
//
//        val list = "attendees,body,bodyPreview,location,end,id,start,subject"
//
//        viewModelScope.launch {
//            try {
//                if (accessToken != null)
//                {
//                    val map:MutableMap<String,String> = mutableMapOf()
//                    map["Authorization"] = "Bearer $accessToken"
//                    val result = GraphClient.getService(url).getEvents("Bearer $accessToken")
//
//
//
//                        eventList.value = result.value!!
//
//                }
//
//            }catch (e:Exception)
//            {
//
//                Log.e(null, "getEvents: ${e.message} ")
//            }
//
//        }
//    }

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
                    Log.d("NetworkEvents", "getUpdates: $events ")
                    val deletedEvents:ArrayList<EventItem> = arrayListOf()
                    events.forEach {
                        if (it?.removed?.reason != null)
                        {
                            deletedEvents.add(it)
                        }
                    }
                    deletedEvents.forEach {
                        dao.deleteEvent(it.id!!)
                    }

                    events.filter {
                        it?.removed?.reason == null
                    }
                   val eventEntities = events.map {
                        it.toEntity()
                    }

                    eventEntities.filter {
                        it.removedReason == null
                    }

                    dao.insertEvents(eventEntities)
                    networkError.value = ""

                }


            }catch (e:Exception)
            {
                networkError.value = e.message
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

                    Log.d("NetworkUpdates", "getUpdatesUsingTheStateTokens: ${result.events} ", )
                    val deletedEvents:ArrayList<EventItem> = arrayListOf()
                    updatedEventsOnly.forEach {
                        if (it?.removed?.reason != null)
                        {
                            deletedEvents.add(it)
                        }
                    }
                    deletedEvents.forEach {
                        dao.deleteEvent(it.id!!)
                    }
                    updatedEventsOnly.filter {
                        it?.removed?.reason == null
                    }
                    val eventEntities = updatedEventsOnly.map {
                        it.toEntity()
                    }
                    eventEntities.filter {
                        it.removedReason == null
                    }
                    dao.insertEvents(eventEntities)
                    networkError.value = ""

                }



            }catch (e:Exception)
            {
                networkError.value = e.message
                Log.e("updateUsingStateToken", "getUpdatesUsingTheStateTokens:  ${e.message} ", )
            }
        }
    }


    fun clearDataBase()
    {
        viewModelScope.launch(Dispatchers.IO) {

            dao.wipeEvents()
            deltaLink.value = null
        }
    }

    fun saveEvent(baseUrl: String,token: String,eventItem: EventItem) {
        viewModelScope.launch(Dispatchers.IO) {

            try {
                GraphClient.getService(baseUrl).createEvent(token,eventItem)
            }catch (e:Exception)
            {
                Log.e("EventCreation", "saveEvent: ${e.message} ", )
            }


        }
    }


    suspend fun editEvent(token: String,dateTime: DateTime,baseUrl:String,eventId:String)
    {
        try {

            val editEventResponse =GraphClient.getService(baseUrl).updateEvent(token,eventId,dateTime)

            if (editEventResponse.isSuccessful && editEventResponse.code() == 200)
            {
                val edited = editEventResponse.body()
                Log.e(null, "editEvent: $edited", )
                dao.insertEvents(listOf(edited.toEntity()))
            }else if (editEventResponse.code() == 400)
            {
                Log.e(null, "editEvent: ${editEventResponse.message()} ", )
            }

        }catch (e:Exception)
        {
            Log.e(null, "editEvent: ${e.message}", )
        }

    }

    init {
       viewModelScope.launch(Dispatchers.IO) {

          dao.wipeEvents()
       }

    }



}