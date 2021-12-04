package com.eslam.csp1401_test

import retrofit2.http.*

interface GraphService {

    @GET("events")
    suspend fun getEvents(
        @Header("Authorization") token:String,
        //@Header("Prefer:outlook.body-content-type") type: String = "text",
       // @Query("select") propertyList:List<String> = listOf("attendees","body","bodyPreview",
          //  "location","end","id","start","subject","calendar")
    ): Events
}