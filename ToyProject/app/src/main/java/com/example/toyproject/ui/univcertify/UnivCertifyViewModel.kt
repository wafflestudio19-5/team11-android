package com.example.toyproject.ui.univcertify

import androidx.lifecycle.ViewModel
import com.example.toyproject.network.Service
import com.example.toyproject.network.dto.University
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class UnivCertifyViewModel @Inject constructor(
    private val service: Service
) : ViewModel() {

    fun loadUnivList() : List<University> = runBlocking {
        service.getUniversityList()
    }
}