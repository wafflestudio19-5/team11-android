package com.example.toyproject.ui.univsearch

import androidx.lifecycle.ViewModel
import com.example.toyproject.network.Service
import com.example.toyproject.network.dto.GetUniversity
import com.example.toyproject.network.dto.University
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


@HiltViewModel
class UnivSearchViewModel @Inject constructor(
    private val service: Service
) : ViewModel() {

    fun loadUnivList() : GetUniversity = runBlocking {
        service.getUniversityList()
    }
}