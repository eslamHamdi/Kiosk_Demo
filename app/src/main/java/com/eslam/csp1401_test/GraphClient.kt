package com.eslam.csp1401_test

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.logging.HttpLoggingInterceptor




object GraphClient {


    private fun retrofit(url: String): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            // .client(clientBuilder())
            .baseUrl(url)
            .build()
    }


    private fun clientBuilder(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
// set your desired log level
// set your desired log level
        logging.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient().newBuilder()
// add your other interceptors …

// add logging as last interceptor
// add your other interceptors …

// add logging as last interceptor
        httpClient.addInterceptor(logging)
        return httpClient.build()
    }


//    val retrofitService: GraphService by lazy {
//        retrofit().create(GraphService::class.java)
//    }

    fun getService(url: String)= retrofit(url).create(GraphService::class.java)




}