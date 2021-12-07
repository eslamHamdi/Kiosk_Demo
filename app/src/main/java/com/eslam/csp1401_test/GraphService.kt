package com.eslam.csp1401_test

import retrofit2.http.*




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

    @GET()
    @Headers("Content-Type: application/json")
    suspend fun getUpdatesUsingStateToken(@Header("Authorization")token:String,@Url stateLink:String ):EventsChanges
}