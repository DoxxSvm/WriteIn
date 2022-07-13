package com.doxx.writein.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.doxx.writein.api.NotesAPI
import com.doxx.writein.models.NoteRequest
import com.doxx.writein.models.NoteResponse
import com.doxx.writein.utils.NetworkResult
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class NoteRepository @Inject constructor(private val notesAPI: NotesAPI) {
    private val _noteLiveData = MutableLiveData<NetworkResult<List<NoteResponse>>>()
    val noteLiveData: LiveData<NetworkResult<List<NoteResponse>>>
        get() = _noteLiveData
    private val _statusLiveData = MutableLiveData<NetworkResult<String>>()
    val statusLiveData: LiveData<NetworkResult<String>>
        get() = _statusLiveData

    suspend fun getNotes() {
        _noteLiveData.postValue(NetworkResult.Loading())
        val response = notesAPI.getNotes()
        if (response.isSuccessful && response.body() != null) {
            _noteLiveData.postValue(NetworkResult.Success(response.body()!!))
        } else if (response.errorBody() != null) {
            val errorObj = JSONObject(response.errorBody()!!.charStream().readText())
            _noteLiveData.postValue(NetworkResult.Error(errorObj.getString("message")))
        } else _noteLiveData.postValue(NetworkResult.Error("Something went wrong"))
    }

    suspend fun createNote(noteRequest: NoteRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = notesAPI.createNote(noteRequest)
        handleResponse(response,"Note created")

    }
    suspend fun updateNote(noteId:String,noteRequest: NoteRequest) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = notesAPI.updateNote(noteId, noteRequest)
        handleResponse(response,"Note updated")
    }
    suspend fun deleteNote(noteId:String) {
        _statusLiveData.postValue(NetworkResult.Loading())
        val response = notesAPI.deleteNote(noteId)
        handleResponse(response,"Note deleted")
    }


    private fun handleResponse(response: Response<NoteResponse>, message: String) {
        if(response.isSuccessful && response.body() != null){
            _statusLiveData.postValue(NetworkResult.Success(message))
        }
        else{
            _statusLiveData.postValue(NetworkResult.Error("Something went wrong"))
        }
    }


}