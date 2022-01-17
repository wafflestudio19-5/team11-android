package com.example.toyproject.ui.main.tableFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.TableService
import com.example.toyproject.network.dto.table.Schedule
import com.example.toyproject.network.dto.table.Semester
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class TableListViewModel @Inject constructor(
    private val service : TableService,
    private val retrofit: Retrofit
) : ViewModel() {

    private val _semesterList = MutableLiveData<MutableList<Semester>>()
    val semesterList : LiveData<MutableList<Semester>> = _semesterList


    fun loadScheduleList() {
        // 로딩 하면 일단 리스트 비워주기
        // val semesterListTemp = mutableListOf<Semester>()

        val s1 = listOf(Schedule(10, "수업1", 2022, 1, true),
            Schedule(11, "수업2", 2022, 1, false),
            Schedule(12, "수업3", 2022, 1, false),
            Schedule(7, "수업4", 2022, 1, false),
            Schedule(1, "수업5", 2022, 1, false),
            Schedule(10, "수업6", 2022, 1, false))
        val s2 = listOf(Schedule(14, "수업7", 2021, 1, false),
            Schedule(15, "수업8", 2021, 1, false),
            Schedule(10, "수업9", 2022, 1, true),
            Schedule(11, "수업10", 2022, 1, false),
            Schedule(12, "수업11", 2022, 1, false),
            Schedule(7, "수업12", 2022, 1, false))
        val s3 = listOf(Schedule(17, "수업13", 2021, 2, true),
            Schedule(18, "수업14", 2021, 2, false),
            Schedule(19, "수업5", 2021, 2, false),
            Schedule(10, "수업16", 2022, 1, false),
            Schedule(11, "수업17", 2022, 1, false),
            Schedule(12, "수업18", 2022, 1, false),
            Schedule(7, "수업19", 2022, 1, false),
            Schedule(1, "수업20", 2022, 1, false),
            Schedule(10, "수업21", 2022, 1, false),
        )

        val ss = listOf(Semester(2022, 1, s1), Semester(2021, 1, s2), Semester(2021, 2, s3))
        val semesterListTemp = ss.toMutableList()

        viewModelScope.launch {
            /*
            val hashmap : HashMap<String, MutableList<Schedule>> = hashMapOf()

            val list : List<Schedule> = service.getScheduleList().result
            val iterator = list.iterator()
            while(iterator.hasNext()) {

                val schedule = iterator.next()
                // 학기 종류 추출
                val semesterString = schedule.year.toString()+schedule.season.toString()

                if(hashmap.containsKey(semesterString)) {
                    hashmap[semesterString]!!.add(schedule)
                }
                else {
                    hashmap[semesterString] = mutableListOf(schedule)
                }
            }
            for(key in hashmap.keys) {
                val year = hashmap[key]!![0].year
                val season = hashmap[key]!![0].season
                val semester = Semester(year, season, hashmap[key]!!)
                semesterListTemp.add(semester)
            }

             */
            _semesterList.value = semesterListTemp
        }
    }
}