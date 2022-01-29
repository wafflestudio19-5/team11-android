package com.example.toyproject.ui.main.noteFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.NoteService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoteMakeViewModel @Inject constructor(
    private val service: NoteService
): ViewModel() {


    fun createMessage(messageRoomId: Int, text: String){
        viewModelScope.launch {
            try{
                service.sendMessage(messageRoomId, text)
            } catch(e: Exception){
                Timber.e(e)
            }
        }
    }

    fun createStartCommentMessage(id: Int, text: String){
        viewModelScope.launch {
            try{
                service.sendStartCommentMessage(id, text)
            } catch(e: Exception){
                Timber.e(e)
            }
        }
    }

    fun createStartArticleMessage(id: Int, text: String){
        viewModelScope.launch {
            try{
                service.sendStartArticleMessage(id, text)
            } catch(e: Exception){
                Timber.e(e)
            }
        }
    }
}