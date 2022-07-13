package com.doxx.writein.api

import com.doxx.writein.models.UserRequest
import com.doxx.writein.models.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("/users/signup")
    suspend fun signup(@Body userRequest: UserRequest):Response<UserResponse>

    @POST("/users/signin")
    suspend fun signin(@Body userRequest: UserRequest):Response<UserResponse>
}