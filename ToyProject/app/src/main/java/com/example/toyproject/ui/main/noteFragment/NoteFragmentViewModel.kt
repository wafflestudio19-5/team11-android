package com.example.toyproject.ui.main.noteFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.NoteService
import com.example.toyproject.network.dto.Message
import com.example.toyproject.network.dto.MessageRoom
import com.example.toyproject.network.dto.MessageRoomResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteFragmentViewModel @Inject constructor(
    private val service : NoteService
) : ViewModel(){

    private val _messageRoomList = MutableLiveData<MutableList<MessageRoom>>()
    val messageRoomList : LiveData<MutableList<MessageRoom>> = _messageRoomList

    private val _hasNewMessage = MutableLiveData<Boolean>()
    val hasNewMessage : LiveData<Boolean> = _hasNewMessage

    fun getMessageRoomList(){
        viewModelScope.launch {
            val response : MessageRoomResponse = service.getMessageRoom()
            _messageRoomList.value = response.message_rooms
        }
    }

}