package com.doxx.writein.api

import com.doxx.writein.models.NoteRequest
import com.doxx.writein.models.NoteResponse
import retrofit2.Response
import retrofit2.http.*

interface NotesAPI {
    @GET("/note")
    suspend fun getNotes():Response<List<NoteResponse>>
    @POST("/note")
    suspend fun createNote(@Body noteRequest: NoteRequest):Response<NoteResponse>
    @PUT("/note/{noteId}")
    suspend fun updateNote(@Path("noteId") noteId:String,@Body noteRequest: NoteRequest) :Response<NoteResponse>
    @DELETE("/note/{noteId}")
    suspend fun deleteNote(@Path ("noteId")noteId: String):Response<NoteResponse>

}