package com.eslam.csp1401_test

import retrofit2.http.Header
import retrofit2.http.Query

interface GraphService {

    suspend fun getEvents(
        @Header("Authorization") token: String,
        @Header("Prefer") type: String = "outlook.body-content-type = \"text\"",
        //@Query("select")
    )
}