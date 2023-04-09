package com.francle.hello.feature.pair.data.api

import com.francle.hello.feature.pair.data.response.PairResponse
import retrofit2.http.GET

interface PairApi {
    @GET("/api/pair")
    suspend fun getPairUser(): PairResponse?
}
