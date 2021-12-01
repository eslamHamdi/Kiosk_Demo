package com.eslam.csp1401_test

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GraphService {

    @GET("calendar/events")
    suspend fun getEvents(
        @Header("Authorization") token: String,
        @Header("Prefer") type: String = "outlook.body-content-type = \"text\"",
        @Query("select") propertyList:List<String> = listOf("attendees","body","bodyPreview",
            "location","end","id","start","subject","calendar")
    ):List<EventModel>
}