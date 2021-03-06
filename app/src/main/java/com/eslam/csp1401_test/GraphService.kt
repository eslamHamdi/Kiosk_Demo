package com.eslam.csp1401_test

import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body


interface GraphService {


    @GET("events")
    suspend fun getEvents(
        @Header("Authorization") token:String,
        //@HeaderMap bodytype: MutableMap<String,String>,
       // @Query("Prefer: outlook.timezone") timeZone:String

    ): Events


    @GET("calendarView/delta")
    @Headers("Content-Type: application/json","Prefer: outlook.timezone=\"Asia/Dubai\"")
    suspend fun getUpdatedEvents(@Header("Authorization") token:String,@Query("startDateTime") startDate:String,
                                 @Query("endDateTime") endDate:String):EventsChanges

    @GET
    @Headers("Content-Type: application/json")
    suspend fun getUpdatesUsingStateToken(@Header("Authorization")token:String,@Url stateLink:String ):EventsChanges

    @POST("calendar/events")
    @Headers("Content-Type: application/json")
    suspend fun createEvent(@Header("Authorization")token:String,@Body eventItem: EventItem):EventItem

    @PATCH("events/{id}")
    @Headers("Content-Type: application/json")
    suspend fun updateEvent(@Header("Authorization")token:String, @Path("id") eventId:String, @Body dateTime: DateTime ):Response<EventItem>
}

//@Part("start") start:RequestBody, @Part("end") end:RequestBody