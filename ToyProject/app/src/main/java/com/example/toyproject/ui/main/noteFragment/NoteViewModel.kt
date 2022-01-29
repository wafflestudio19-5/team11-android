package com.example.toyproject.ui.main.noteFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.NoteService
import com.example.toyproject.network.dto.Message
import com.example.toyproject.network.dto.MessageResponse
import com.example.toyproject.network.dto.MessageRoomResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val service: NoteService
): ViewModel()  {

    private val _messageList = MutableLiveData<MutableList<Message>>()
    val messageList: LiveData<MutableList<Message>> = _messageList

    private val _talker = MutableLiveData<String>()
    val talker: LiveData<String> = _talker

    fun getMessageList(messageRoomId: Int){
        viewModelScope.launch {
            try{
                val response: MessageResponse = service.getMessage(messageRoomId)

                _messageList.value = response.messages
                _talker.value = response.user_nickname
            } catch(e: Exception){
                Timber.e(e)
            }
        }
    }


}