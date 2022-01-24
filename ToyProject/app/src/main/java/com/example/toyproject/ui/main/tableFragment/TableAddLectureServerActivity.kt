package com.example.toyproject.ui.main.tableFragment

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityTableAddLectureServerBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min

@AndroidEntryPoint
class TableAddLectureServerActivity : AppCompatActivity() {

    lateinit var binding: ActivityTableAddLectureServerBinding

    // TODO : 나중에 key 를 통신에서 받은 ID 로 바꿀 것
    private val lectureHashMap: HashMap<Int, MutableList<TableCellView>> = hashMapOf()
    private val shadowHashMap: HashMap<TableAddCustomLectureView, TableCellView> = hashMapOf()

    // 시간표를 차지하고 있는 수업의 정보 (중복 체크용)
    private val occupyTable: HashMap<Pair<Int, Int>, Cell> = hashMapOf()
    private val shadowOccupyTable: HashMap<Pair<Int, Int>, Int> = hashMapOf()

    // 미리보기 시간표의 셀들
    private val cells = mutableListOf<Cell>()

    private var colWidth: Int = 0

    private var majorPath : ArrayList<String> = arrayListOf("전체")

    // "RESULT_OK" : AddDefaultLectureActivity 에서 시간표 정보 가져온 것 적용
    private val resultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                cells.clear()
                lectureHashMap.clear()
                shadowHashMap.clear()
                occupyTable.clear()
                for (row in 1..96) for (col in 2..10 step (2)) shadowOccupyTable[Pair(row, col)] = 0

                val cellInfo: ArrayList<Cell> = it.data!!.getParcelableArrayListExtra("cellInfo")!!
                // 불러온 정보로 셀 추가
                cellInfo.withIndex().forEach { i ->
                    makeCell(i.value)
                }
                // 동적 시간표 길이 적용
                adjustTableHeight(findFastestTime(), findLatestTime())
                addBorder(findFastestTime(), findLatestTime())
            }
        }

    private val filterResultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                val queryMajor : String? = it.data?.getStringExtra("major")
                if(queryMajor != null) {
                    binding.filterMajorText.text = queryMajor
                    binding.filterMajorText.setTextColor(resources.getColor(R.color.Primary))
                    binding.filterMajorText.setTypeface(null, Typeface.BOLD)
                    binding.filterMajorClear.visibility = View.VISIBLE
                    val path = it.data!!.getStringArrayListExtra("path")!!
                    majorPath.clear()
                    path.forEach { item ->
                        majorPath.add(item)
                    }
                    // TODO : 바로 통신 진행
                }
            }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 미리보기 시간표의 열 너비 기종마다 일정하게 조정 (수정 필요)
        val display = windowManager.defaultDisplay
        val stageWidth = display.width
        colWidth = stageWidth/6 + stageWidth/54

        // 전환 이펙트 : 실행할 때 아래에서 올라오도록
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_nothing)

        binding = ActivityTableAddLectureServerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cellInfo : ArrayList<Cell> = intent.getParcelableArrayListExtra("cellInfo")!!
        // 기존 강의 정보 추가하기
        cellInfo.forEach { i ->
            makeCell(i)
        }

        // 그림자 점유 테이블 초기화
        for(row in 1..96) for(col in 2..10 step(2)) shadowOccupyTable[Pair(row, col)] = 0
        // 시간표에 가로 테두리 칠하기
        addBorder(findFastestTime(), findLatestTime())
        // 시간표 세로 길이 동적 조정
        adjustTableHeight(findFastestTime(), findLatestTime())

        // 직접 추가 버튼
        binding.addDefaultLectureButton.setOnClickListener {
            val intent = Intent(this, TableAddLectureDefaultActivity::class.java)
            intent.putParcelableArrayListExtra("cellInfo", ArrayList(cells))
            resultListener.launch(intent)
        }

        // Filter 부분
        // 1. 전공/영역 필터
        binding.filterMajor.setOnClickListener {
            val intent = Intent(this, TableAddFilterMajorActivity::class.java)
            intent.putExtra("before", binding.filterMajorText.text.toString())
            intent.putStringArrayListExtra("path", majorPath)
            filterResultListener.launch(intent)
        }
        binding.filterMajorClear.setOnClickListener {
            binding.filterMajorText.text = "전체"
            binding.filterMajorClear.visibility = View.GONE
            majorPath = arrayListOf("전체")
            binding.filterMajorText.setTextColor(ContextCompat.getColor(this, R.color.color_filter_text_default))
        }

        // X 버튼
        binding.tableAddServerLectureCloseButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putParcelableArrayListExtra("cellInfo", ArrayList(cells))
        setResult(RESULT_OK, resultIntent)
        finish()
        // 뒤로 버튼 누르면 아래로 내려가기
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
    }

    private fun buildTimeString(hour : Int, min : Int) : String {
        val hourString = if(hour<10) "0$hour"
        else hour.toString()
        val minuteString = if(min<10) "0$min"
        else min.toString()

        val builder = StringBuilder()
        builder.append(hourString)
        builder.append(":")
        builder.append(minuteString)

        return builder.toString()
    }

    // 시간표에 셀 추가하는 함수
    private fun makeCell(cellObject : Cell) : TableCellView {
        val item = TableCellView(this)
        item.text = cellObject.title
        item.setTypeface(item.typeface, Typeface.BOLD)
        item.setTextColor(Color.parseColor("#FFFFFF"))
        item.setBackgroundColor(Color.parseColor(cellObject.color))
        // item.width = resources.getDimension(R.dimen.table_col_width).toInt()
        item.width = colWidth
        item.height = (resources.getDimension(R.dimen.table_row_width)*cellObject.span).toInt()
        item.gravity = Gravity.TOP

        val colSpan :  androidx.gridlayout.widget.GridLayout.Spec =  androidx.gridlayout.widget.GridLayout.spec(cellObject.col,1)
        val rowSpan : androidx.gridlayout.widget.GridLayout.Spec = androidx.gridlayout.widget.GridLayout.spec(cellObject.start, cellObject.span)

        val param = androidx.gridlayout.widget.GridLayout.LayoutParams(rowSpan, colSpan)
        param.setGravity(Gravity.FILL)

        binding.tableNow.addView(item, param)

        // 새로 추가되는 강의는 hashmap 에도 추가
        // TODO : 강의마다 고유번호 서버에서 ID 받아와서 HASHMAP 의 KEY 로 쓸 것
        item.info = cellObject.title.hashCode()
        if(lectureHashMap.containsKey(item.info)){
            lectureHashMap[item.info]?.add(item)
        }
        else {
            lectureHashMap[item.info] = mutableListOf(item)
        }

        for(row in cellObject.start until cellObject.start+cellObject.span) {
            occupyTable[Pair(row, cellObject.col)] = cellObject
        }

        cells.add(cellObject)
        return item
    }

    // 미리보기 시간표에 그림자 셀 추가하기
    private fun makeShadowCell(start : Int, span: Int, col : Int) : TableCellView {
        val item =  TableCellView(this)

        item.setBackgroundColor(Color.parseColor("#803C3C3C"))
        item.width = colWidth
        item.height = (resources.getDimension(R.dimen.table_row_width)*span).toInt()

        val colSpan :  androidx.gridlayout.widget.GridLayout.Spec =  androidx.gridlayout.widget.GridLayout.spec(col,1)
        val rowSpan : androidx.gridlayout.widget.GridLayout.Spec = androidx.gridlayout.widget.GridLayout.spec(start, span)

        val param = androidx.gridlayout.widget.GridLayout.LayoutParams(rowSpan, colSpan)
        param.setGravity(Gravity.FILL)

        // 그림자 점유 테이블도 갱신
        for(row in start until start+span) {
            val temp = shadowOccupyTable[Pair(row, col)]
            if (temp != null) {
                shadowOccupyTable[Pair(row, col)] = temp + 1
            }
            else {
                shadowOccupyTable[Pair(row, col)] = 1
            }
        }

        // 시간표 height 조정 (그림자가 범위 밖에 있을 수도 있으니)
        adjustTableHeight(
            min(findFastestShadow(), findFastestTime()),
            max(findLatestShadow(), findLatestTime())
        )

        addBorder(findFastestShadow(), findLatestShadow())

        // 미리보기 시간표에 그림자 셀 추가
        binding.tableNow.addView(item, param)
        return item
    }
    // 그림자 점유 표에서 특정 영역 삭제하기
    private fun removeShadowOccupy(startRow : Int, endRow : Int, col : Int) {
        for(row in startRow until endRow) {
            val temp = shadowOccupyTable[Pair(row, col)]
            if (temp != null) {
                shadowOccupyTable[Pair(row, col)] = temp - 1
            }
        }
    }

    // 시간표 가로 테두리 추가하는 함수
    private fun addBorder(startTime : Int, endTime : Int) {
        for(time in startTime until endTime) {
            if(time%4!=0) continue
            for(col in 2..10 step(2)) {
                if (occupyTable.containsKey(Pair(time, col))) continue  // 기존 시간표에 있는 사이에는 border 치지 말기
                if(shadowOccupyTable[Pair(time, col)]!!>0) continue  // 역시, 그림자도 border 로 쪼개지지 않도록
                val item = TextView(this)
                item.setBackgroundResource(R.drawable.table_cell_stroke_bottom)
                item.width = colWidth
                item.height = 1

                val colSpan :  androidx.gridlayout.widget.GridLayout.Spec =  androidx.gridlayout.widget.GridLayout.spec(col,1)
                val rowSpan : androidx.gridlayout.widget.GridLayout.Spec = androidx.gridlayout.widget.GridLayout.spec(time, 1)
                val param = androidx.gridlayout.widget.GridLayout.LayoutParams(rowSpan, colSpan)
                param.setGravity(Gravity.FILL)

                binding.tableNow.addView(item, param)
            }
        }
    }

    // 시간표 상 수업 중 가장 나중 시간 row 번호
    private fun findLatestTime() : Int{
        var maxVal = 61
        for(key in occupyTable.keys) {
            if(key.first > maxVal) maxVal = key.first
        }
        return maxVal
    }
    // 시간표 상 수업 중 가장 빠른 시간 row 번호
    private fun findFastestTime() : Int{
        var min = 37
        for(key in occupyTable.keys) {
            if(key.first < min) min = key.first
        }
        return min
    }
    // 시간표 상 그림자 중 가장 빠른 시간 row 번호
    private fun findFastestShadow() : Int {
        for(row in 1..37) {
            for(col in 2..10 step(2)) {
                if(shadowOccupyTable[Pair(row, col)]!! > 0) {
                    return row
                }
            }
        }
        return 37
    }
    // 시간표 상 그림자 중 가장 늦은 시간 row 번호
    private fun findLatestShadow() : Int{
        for(row in 96 downTo 64) {
            for(col in 2..10 step(2)) {
                if(shadowOccupyTable[Pair(row, col)]!! >  0) {
                    return row
                }
            }
        }
        return 64
    }

    // 7시부터 21시까지 있으면, startTime = 7*4=28, endTime = 21*4=84
    private fun adjustTableHeight(startTime : Int, endTime : Int) {
        val time0 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_0_1),
            binding.tableNow.findViewById(R.id.table_grid_0_2),
            binding.tableNow.findViewById(R.id.table_grid_0_3),
            binding.tableNow.findViewById(R.id.table_grid_0_4),
            binding.tableNow.findViewById(R.id.table_grid_0))

        val time1 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_1_1),
            binding.tableNow.findViewById(R.id.table_grid_1_2),
            binding.tableNow.findViewById(R.id.table_grid_1_3),
            binding.tableNow.findViewById(R.id.table_grid_1_4),
            binding.tableNow.findViewById(R.id.table_grid_1))

        val time2 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_2_1),
            binding.tableNow.findViewById(R.id.table_grid_2_2),
            binding.tableNow.findViewById(R.id.table_grid_2_3),
            binding.tableNow.findViewById(R.id.table_grid_2_4),
            binding.tableNow.findViewById(R.id.table_grid_2))

        val time3 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_3_1),
            binding.tableNow.findViewById(R.id.table_grid_3_2),
            binding.tableNow.findViewById(R.id.table_grid_3_3),
            binding.tableNow.findViewById(R.id.table_grid_3_4),
            binding.tableNow.findViewById(R.id.table_grid_3))

        val time4 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_4_1),
            binding.tableNow.findViewById(R.id.table_grid_4_2),
            binding.tableNow.findViewById(R.id.table_grid_4_3),
            binding.tableNow.findViewById(R.id.table_grid_4_4),
            binding.tableNow.findViewById(R.id.table_grid_4))

        val time5 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_5_1),
            binding.tableNow.findViewById(R.id.table_grid_5_2),
            binding.tableNow.findViewById(R.id.table_grid_5_3),
            binding.tableNow.findViewById(R.id.table_grid_5_4),
            binding.tableNow.findViewById(R.id.table_grid_5))

        val time6 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_6_1),
            binding.tableNow.findViewById(R.id.table_grid_6_2),
            binding.tableNow.findViewById(R.id.table_grid_6_3),
            binding.tableNow.findViewById(R.id.table_grid_6_4),
            binding.tableNow.findViewById(R.id.table_grid_6))

        val time7 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_7_1),
            binding.tableNow.findViewById(R.id.table_grid_7_2),
            binding.tableNow.findViewById(R.id.table_grid_7_3),
            binding.tableNow.findViewById(R.id.table_grid_7_4),
            binding.tableNow.findViewById(R.id.table_grid_7))

        val time8 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_8_1),
            binding.tableNow.findViewById(R.id.table_grid_8_2),
            binding.tableNow.findViewById(R.id.table_grid_8_3),
            binding.tableNow.findViewById(R.id.table_grid_8_4),
            binding.tableNow.findViewById(R.id.table_grid_8))

        val time9 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_9_1),
            binding.tableNow.findViewById(R.id.table_grid_9_2),
            binding.tableNow.findViewById(R.id.table_grid_9_3),
            binding.tableNow.findViewById(R.id.table_grid_9_4),
            binding.tableNow.findViewById(R.id.table_grid_9))

        val time10 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_10_1),
            binding.tableNow.findViewById(R.id.table_grid_10_2),
            binding.tableNow.findViewById(R.id.table_grid_10_3),
            binding.tableNow.findViewById(R.id.table_grid_10_4),
            binding.tableNow.findViewById(R.id.table_grid_10))

        val time11 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_11_1),
            binding.tableNow.findViewById(R.id.table_grid_11_2),
            binding.tableNow.findViewById(R.id.table_grid_11_3),
            binding.tableNow.findViewById(R.id.table_grid_11_4),
            binding.tableNow.findViewById(R.id.table_grid_11))

        val time12 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_12_1),
            binding.tableNow.findViewById(R.id.table_grid_12_2),
            binding.tableNow.findViewById(R.id.table_grid_12_3),
            binding.tableNow.findViewById(R.id.table_grid_12_4),
            binding.tableNow.findViewById(R.id.table_grid_12))

        val time13 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_13_1),
            binding.tableNow.findViewById(R.id.table_grid_13_2),
            binding.tableNow.findViewById(R.id.table_grid_13_3),
            binding.tableNow.findViewById(R.id.table_grid_13_4),
            binding.tableNow.findViewById(R.id.table_grid_13))

        val time14 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_14_1),
            binding.tableNow.findViewById(R.id.table_grid_14_2),
            binding.tableNow.findViewById(R.id.table_grid_14_3),
            binding.tableNow.findViewById(R.id.table_grid_14_4),
            binding.tableNow.findViewById(R.id.table_grid_14))

        val time15 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_15_1),
            binding.tableNow.findViewById(R.id.table_grid_15_2),
            binding.tableNow.findViewById(R.id.table_grid_15_3),
            binding.tableNow.findViewById(R.id.table_grid_15_4),
            binding.tableNow.findViewById(R.id.table_grid_15))

        val time16 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_16_1),
            binding.tableNow.findViewById(R.id.table_grid_16_2),
            binding.tableNow.findViewById(R.id.table_grid_16_3),
            binding.tableNow.findViewById(R.id.table_grid_16_4),
            binding.tableNow.findViewById(R.id.table_grid_16))

        val time17 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_17_1),
            binding.tableNow.findViewById(R.id.table_grid_17_2),
            binding.tableNow.findViewById(R.id.table_grid_17_3),
            binding.tableNow.findViewById(R.id.table_grid_17_4),
            binding.tableNow.findViewById(R.id.table_grid_17))

        val time18 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_18_1),
            binding.tableNow.findViewById(R.id.table_grid_18_2),
            binding.tableNow.findViewById(R.id.table_grid_18_3),
            binding.tableNow.findViewById(R.id.table_grid_18_4),
            binding.tableNow.findViewById(R.id.table_grid_18))

        val time19 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_19_1),
            binding.tableNow.findViewById(R.id.table_grid_19_2),
            binding.tableNow.findViewById(R.id.table_grid_19_3),
            binding.tableNow.findViewById(R.id.table_grid_19_4),
            binding.tableNow.findViewById(R.id.table_grid_19))

        val time20 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_20_1),
            binding.tableNow.findViewById(R.id.table_grid_20_2),
            binding.tableNow.findViewById(R.id.table_grid_20_3),
            binding.tableNow.findViewById(R.id.table_grid_20_4),
            binding.tableNow.findViewById(R.id.table_grid_20))

        val time21 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_21_1),
            binding.tableNow.findViewById(R.id.table_grid_21_2),
            binding.tableNow.findViewById(R.id.table_grid_21_3),
            binding.tableNow.findViewById(R.id.table_grid_21_4),
            binding.tableNow.findViewById(R.id.table_grid_21))

        val time22 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_22_1),
            binding.tableNow.findViewById(R.id.table_grid_22_2),
            binding.tableNow.findViewById(R.id.table_grid_22_3),
            binding.tableNow.findViewById(R.id.table_grid_22_4),
            binding.tableNow.findViewById(R.id.table_grid_22))

        val time23 : List<View> = listOf(binding.tableNow.findViewById(R.id.table_grid_23_1),
            binding.tableNow.findViewById(R.id.table_grid_23_2),
            binding.tableNow.findViewById(R.id.table_grid_23_3),
            binding.tableNow.findViewById(R.id.table_grid_23_4),
            binding.tableNow.findViewById(R.id.table_grid_23))

        val times : List<List<View>> = listOf(time0, time1, time2, time3, time4, time5, time6, time7, time8, time9, time10, time11, time12, time13, time14, time15, time16, time17, time18, time19, time20, time21, time22, time23)
        val startRow : Int = (startTime-1)/4
        val endRow : Int = (endTime-1)/4


        for(t in 0 until startRow) {
            times[t][0].visibility = View.GONE
            times[t][1].visibility = View.GONE
            times[t][2].visibility = View.GONE
            times[t][3].visibility = View.GONE
            times[t][4].visibility = View.GONE
        }
        for(t in startRow .. endRow) {
            times[t][0].visibility = View.VISIBLE
            times[t][1].visibility = View.VISIBLE
            times[t][2].visibility = View.VISIBLE
            times[t][3].visibility = View.VISIBLE
            times[t][4].visibility = View.VISIBLE
        }
        for(t in endRow+1 until 24) {
            times[t][0].visibility = View.GONE
            times[t][1].visibility = View.GONE
            times[t][2].visibility = View.GONE
            times[t][3].visibility = View.GONE
            times[t][4].visibility = View.GONE
        }
        addBorder(startTime, endTime)
    }
    // 시간표에 중복되는 강의가 있는지 체크
    private fun checkDuplicate(start : Int, end : Int, col : Int, exception : Int) : String? {
        for(row in start until end) {
            if(occupyTable.containsKey(Pair(row, col))) {
                if(occupyTable[Pair(row, col)]!!.custom_id==exception) continue
                return  occupyTable[Pair(row, col)]!!.title
            }
        }
        return null
    }
    // 그림자 간 중복이 있는지 체크
    private fun checkShadowDuplicate() : Boolean {
        for(values in shadowOccupyTable.values) if(values > 1) return true
        return false
    }
    private fun dayStringToColInt(day : String) : Int{
        return when(day) {
            "월요일" -> MON
            "화요일" -> TUE
            "수요일" -> WED
            "목요일" -> THU
            else -> FRI
        }
    }
    private fun timeStringToRowInt(time : String) : Int {
        val hour = time.split(":")[0].toInt()
        val min = time.split(":")[1].toInt()

        // 자정이 row = 1 이고, 15분 마다 row +1 이니 이렇게 변환
        return hour*4 + min/15 + 1
    }
    private fun buildCommunicateTimeString(starts : MutableList<Int>, ends : MutableList<Int>, cols : MutableList<Int>)  : String{
        val stringBuilder = StringBuilder()
        for(i in 0 until starts.size) {
            val start = starts[i]
            val end = ends[i]
            val col = cols[i]
            stringBuilder.append(colIntToDay(col))
            stringBuilder.append("(")
            stringBuilder.append(buildTimeString( (start-1)/4, (start-1)%4*15))
            stringBuilder.append("~")
            stringBuilder.append(buildTimeString( (end-1)/4, (end-1)%4*15))
            stringBuilder.append(")")
            if(i != starts.size-1) stringBuilder.append("/")
        }
        return stringBuilder.toString()
    }
    private fun colIntToDay(col : Int) : String {
        return when(col) {
            2 -> "월"
            4 -> "화"
            6 -> "수"
            8 -> "목"
            else -> "금"
        }
    }

    private fun randomColor(): String {
        val random = Random()
        val nextInt: Int = random.nextInt(0xD7CFD1 + 1)
        return String.format("#%06x", nextInt)
    }

    private companion object {
        const val MON = 2
        const val TUE = 4
        const val WED = 6
        const val THU = 8
        const val FRI = 10
    }
}
