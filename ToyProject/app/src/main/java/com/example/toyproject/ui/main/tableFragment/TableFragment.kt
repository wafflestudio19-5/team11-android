package com.example.toyproject.ui.main.tableFragment

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.toyproject.R
import com.example.toyproject.databinding.FragmentTableBinding
import dagger.hilt.android.AndroidEntryPoint
import android.widget.*
import kotlin.NullPointerException
import kotlin.collections.ArrayList

@AndroidEntryPoint
class TableFragment : Fragment() {

    private lateinit var binding: FragmentTableBinding

    // 흩어져 있는 여러 다른 셀을 같은 강의로 묶기 위한 해시맵  HashMap<고유번호, MutableList<셀 뷰들>>
    private val lectureHashMap : HashMap<String, MutableList<TableCellView>> = hashMapOf()
    // 시간표 셀들 중복 체크 및 동적 크기 조절을 위한 해시맵  HashMap<Pair<row, col>, title>
    private val occupyTable : HashMap<Pair<Int, Int>, String> = hashMapOf()

    // Cell 의 View 와 Object 를 매칭시켜 주는 해시맵
    private val myCells : HashMap<TableCellView, Cell> = hashMapOf()

    var colWidth : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 열 너비 기종마다 일정하게 조정 (수정 필요)
        val display = activity!!.windowManager.defaultDisplay
        val stageWidth = display.width
        colWidth = stageWidth/6 + stageWidth/54


        // 시간표에 가로 테두리 칠하기
        addBorder(findFastestTime(), findLatestTime())
        // 시간표 세로 길이 동적 조정
        adjustTableHeight(findFastestTime(), findLatestTime())


        // "RESULT_OK" : AddLectureActivity 에서 시간표 정보 가져온 것 적용
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == AppCompatActivity.RESULT_OK) {
                    val cellInfo : ArrayList<Cell> = it.data!!.getParcelableArrayListExtra("cellInfo")!!
                    // 불러온 정보로 셀 추가
                    cellInfo.forEach { i ->
                        makeCell(i)
                    }
                    // 동적 시간표 길이 적용
                    adjustTableHeight(findFastestTime(), findLatestTime())
                    addBorder(findFastestTime(), findLatestTime())

                    // 각 셀 뷰에 대해 클릭 리스너 설정
                    for(cell in myCells.keys) {
                        cell.setOnClickListener {
                            // 화면 아래에 뜨는 팝업창
                            val bottomSheet = LectureInfoBottomSheet()
                            // 전달할 강의 정보들 parcelize 해서 포장
                            val args = Bundle()
                            args.putParcelable("cellInfo", myCells[cell])

                            // 같은 강의의 다른 셀 시간도 모두 합쳐서 전달
                            val allTime = StringBuilder()
                            lectureHashMap[cell.info]!!.withIndex().forEach { piece ->
                                try {
                                    allTime.append(makeTimeViewString(
                                        myCells[piece.value]!!.start, myCells[piece.value]!!.span, myCells[piece.value]!!.col))
                                    if(piece.index<lectureHashMap[cell.info]!!.size-1) allTime.append(", ")
                                } catch (n : NullPointerException) {}
                            }
                            args.putString("time", allTime.toString())
                            bottomSheet.arguments = args
                            // BottomSheet 에서 [삭제]를 누르면 작동하는 인터페이스 함수
                            bottomSheet.deleteCell(object : LectureInfoBottomSheet.DeleteCellInterface {
                                override fun delete() {
                                    // 해당 셀과 동일한 강의 싹 골라서 view 에서 삭제
                                    lectureHashMap[cell.info]!!.forEach { piece ->
                                        val pieceObject = myCells[piece]
                                        binding.tableNow.removeView(piece)
                                        try {
                                            removeFromOccupyTable(pieceObject!!.start, pieceObject.span, pieceObject.col)
                                        } catch ( n : NullPointerException) {}

                                        myCells.remove(piece)
                                    }
                                    adjustTableHeight(findFastestTime(), findLatestTime())
                                }
                            })
                            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
                        }

                    }
                }
            }
        // TODO : 통신 해서 default 시간표 셀 정보 받을 때 object 도 같이 생성해서 clickListener 만들어야 함
        // TODO : 이때도 parcelable 쓸 것




        // 시간표 없을 때만 보이는, 새 시간표 만들기 창
        binding.fragmentTableMakeButton.setOnClickListener {
            // TODO : 지금 년도, 학기 구해서 Schedule 객체 목록에 추가하기
            binding.fragmentTableDefault.visibility = GONE // TODO : 임시
        }

        // 강의 추가 버튼
        binding.tableButtonAddClass.setOnClickListener {
            // if(계절학기) {
            val intent = Intent(activity, TableAddLectureDefaultActivity::class.java)
            intent.putParcelableArrayListExtra("cellInfo", ArrayList(myCells.values))
            resultListener.launch(intent)
        }
        // 우상단 시간표 목록 버튼
        binding.tableButtonList.setOnClickListener {
            val intent = Intent(activity, TableListActivity::class.java)
            resultListener.launch(intent)
        }
        binding.tableButtonSetting.setOnClickListener {

        }
    }

    // 시간표에 셀 추가하는 함수
    private fun makeCell(cellObject : Cell) : TableCellView {
        val item = TableCellView(activity)
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
        item.info = cellObject.title.hashCode().toString()
        if(lectureHashMap.containsKey(item.info)){
            lectureHashMap[item.info]?.add(item)
        }
        else {
            lectureHashMap[item.info] = mutableListOf(item)
        }

        for(row in cellObject.start until cellObject.start+cellObject.span) {
            occupyTable[Pair(row, cellObject.col)] = cellObject.title
        }

        myCells[item] = cellObject
        return item
    }

    private fun removeFromOccupyTable(start : Int, span : Int, col : Int) {
        for(row in start until start+span) {
            occupyTable.remove(Pair(row, col))
        }
    }
    // 시간표 가로 테두리 추가하는 함수
    private fun addBorder(startTime : Int, endTime : Int) {
        for(time in startTime until endTime) {
            if(time%4!=0) continue
            for(col in 2..10 step(2)) {
                if (occupyTable.containsKey(Pair(time, col))) continue  // 기존 시간표에 있는 사이에는 border 치지 말기
                val item = TextView(activity)
                item.setBackgroundResource(R.drawable.table_cell_stroke_bottom)
                item.width = colWidth
                item.height = 1

                val colspan :  androidx.gridlayout.widget.GridLayout.Spec =  androidx.gridlayout.widget.GridLayout.spec(col,1)
                val rowSpan : androidx.gridlayout.widget.GridLayout.Spec = androidx.gridlayout.widget.GridLayout.spec(time, 1)
                val param = androidx.gridlayout.widget.GridLayout.LayoutParams(rowSpan, colspan)
                param.setGravity(Gravity.FILL)

                binding.tableNow.addView(item, param)
            }
        }
        // 테두리에 강의가 가려져서 나뉘어 보이지 않도록
        for(c in 0 until binding.tableNow.childCount) {
            val view = binding.tableNow.getChildAt(c)
            if(view is TableCellView) view.bringToFront()
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
            times[t][0].visibility = GONE
            times[t][1].visibility = GONE
            times[t][2].visibility = GONE
            times[t][3].visibility = GONE
            times[t][4].visibility = GONE
        }
        for(t in startRow .. endRow) {
            times[t][0].visibility = VISIBLE
            times[t][1].visibility = VISIBLE
            times[t][2].visibility = VISIBLE
            times[t][3].visibility = VISIBLE
            times[t][4].visibility = VISIBLE
        }
        for(t in endRow+1 until 24) {
            times[t][0].visibility = GONE
            times[t][1].visibility = GONE
            times[t][2].visibility = GONE
            times[t][3].visibility = GONE
            times[t][4].visibility = GONE
        }
        addBorder(startTime, endTime)
    }
    private fun makeTimeViewString(start : Int, span : Int, col : Int) : String {
        val end = start + span
        val day = when(col) {
            2 -> "월"
            4 -> "화"
            6 -> "수"
            8 -> "목"
            else -> "금"
        }
        val sBuilder = StringBuilder()
        val startHour = (start-1)/4
        val startMin = (start-1)%4 * 15
        val endHour = (end-1)/4
        val endMin = (end-1)%4 * 15

        sBuilder.append("$day ")
        if(startHour<10) sBuilder.append("0$startHour:")
        else sBuilder.append("$startHour:")
        if(startMin<10) sBuilder.append("0$startMin-")
        else sBuilder.append("$startMin-")
        if(endHour<10) sBuilder.append("0$endHour:")
        else sBuilder.append("$endHour:")
        if(endMin<10) sBuilder.append("0$endMin")
        else sBuilder.append("$endMin")

        return sBuilder.toString()
    }
}

