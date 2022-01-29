package com.example.toyproject.ui.main.notifyFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.NotifyService
import com.example.toyproject.network.dto.Notification
import com.example.toyproject.network.dto.NotificationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotifyViewModel @Inject constructor(
    private val service: NotifyService
): ViewModel() {

    private val _notificationList = MutableLiveData<MutableList<Notification>>()
    val notificationList: LiveData<MutableList<Notification>> = _notificationList

    fun getNotificationList(){
        viewModelScope.launch{
            val response : NotificationResponse = service.getNotification()
            _notificationList.value = response.results
        }
    }

    fun readNotification(notification_id: Int){
        viewModelScope.launch {
            service.readNotification(notification_id)
        }
    }


}