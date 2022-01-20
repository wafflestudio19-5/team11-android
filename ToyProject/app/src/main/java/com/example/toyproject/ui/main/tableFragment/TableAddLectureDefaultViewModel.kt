package com.example.toyproject.ui.main.tableFragment

import android.view.View
import androidx.lifecycle.ViewModel
import com.example.toyproject.network.TableService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class TableAddLectureDefaultViewModel@Inject constructor(


    private val service : TableService,
    private val retrofit: Retrofit
)   : ViewModel(){



}