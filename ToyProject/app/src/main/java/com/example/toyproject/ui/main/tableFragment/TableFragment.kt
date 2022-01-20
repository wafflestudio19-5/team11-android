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
import java.util.ArrayList
import kotlin.NullPointerException





@AndroidEntryPoint
class TableFragment : Fragment() {

    private lateinit var binding: FragmentTableBinding

    private val lectureHashMap : HashMap<String, MutableList<TableCellView>> = hashMapOf()
    // TODO : 시간표에서 셀 삭제하는 거 만들면 여기서도 지워줘야함
    private val occupyTable : HashMap<Pair<Int, Int>, String> = hashMapOf()

    private val myCells : MutableList<Cell> = mutableListOf()

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
                    val titles = it.data?.getStringArrayListExtra("titles")
                    val colors = it.data?.getStringArrayListExtra("colors")
                    val starts = it.data?.getIntegerArrayListExtra("starts")
                    val spans = it.data?.getIntegerArrayListExtra("spans")
                    val cols = it.data?.getIntegerArrayListExtra("cols")

                    titles!!.indices.forEach { i ->
                        makeCell(titles[i], colors!![i], starts!![i], spans!![i], cols!![i])        // TODO : 임시로 해시코드임
                        myCells.add(Cell(titles[i], colors[i], starts[i], spans[i], cols[i], titles[i].hashCode(), null))
                        for(row in starts[i].toInt() until starts[i].toInt()+spans[i].toInt()) {
                            occupyTable[Pair(row, cols[i])] = titles[i]
                        }
                    }
                    adjustTableHeight(findFastestTime(), findLatestTime())
                }
            }

        // TODO : 강의마다 고유번호 서버에서 ID 받아와서 HASHMAP 의 KEY로 쓸 것
        // TODO : makeCell 함수의 인자를 lecture 로 바꿔버리기.
        // Grid 의 각 item 을 같은 수업 단위로 묶어서 해시맵에 넣고, 클릭 이벤트 설정
        val grid = view.findViewById(R.id.table_now) as androidx.gridlayout.widget.GridLayout
        val childCount = grid.childCount
        for (i in 0 until childCount) {
            if(grid.getChildAt(i) is TableCellView) {
                val container : TableCellView = grid.getChildAt(i) as TableCellView

                if(lectureHashMap.containsKey<Any>(container.info)){
                    lectureHashMap.get<Any, MutableList<TableCellView>>(container.info)?.add(container)
                }
                else {
                    lectureHashMap[container.info.toString()] = mutableListOf(container)
                }
                container.setOnClickListener {
                    try {
                        val iter =lectureHashMap.get<Any, MutableList<TableCellView>>(container.info)?.iterator()
                        while(iter!!.hasNext()) {
                            val cell = iter.next()
                            // binding.tableNow.removeView(cell)
                            Toast.makeText(activity, cell.info, Toast.LENGTH_SHORT).show()
                        }
                    } catch (n : NullPointerException) {

                    }

                }
            }

        }


        // 시간표 없을 때만 보이는, 새 시간표 만들기 창
        binding.fragmentTableMakeButton.setOnClickListener {
            // TODO : 지금 년도, 학기 구해서 Schedule 객체 목록에 추가하기
            binding.fragmentTableDefault.visibility = GONE
        }

        // 강의 추가 버튼
        binding.tableButtonAddClass.setOnClickListener {
            // if(계절학기) {
            val intent = Intent(activity, TableAddLectureDefaultActivity::class.java)

            val titles : ArrayList<String?> = arrayListOf()
            val colors : ArrayList<String?> = arrayListOf()
            val starts : ArrayList<Int> = arrayListOf()
            val spans : ArrayList<Int> = arrayListOf()
            val cols : ArrayList<Int> = arrayListOf()

            val iter = myCells.iterator()
            while(iter.hasNext()) {
                val cell = iter.next()
                titles.add(cell.title)
                colors.add(cell.color)
                starts.add(cell.start)
                spans.add(cell.span)
                cols.add(cell.col)
            }

            intent.putStringArrayListExtra("titles", titles)
            intent.putStringArrayListExtra("colors", colors)
            intent.putIntegerArrayListExtra("starts", starts)
            intent.putIntegerArrayListExtra("spans", spans)
            intent.putIntegerArrayListExtra("cols", cols)
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
    private fun makeCell(title : String, color : String, start : Int, span: Int, col : Int) : TableCellView {
        val item = TableCellView(activity)
        item.text = title
        item.setTypeface(item.typeface, Typeface.BOLD)
        item.setTextColor(Color.parseColor("#FFFFFF"))
        item.setBackgroundColor(Color.parseColor(color))
        // item.width = resources.getDimension(R.dimen.table_col_width).toInt()
        item.width = colWidth
        item.height = (resources.getDimension(R.dimen.table_row_width)*span).toInt()
        item.gravity = Gravity.TOP

        val colSpan :  androidx.gridlayout.widget.GridLayout.Spec =  androidx.gridlayout.widget.GridLayout.spec(col,1)
        val rowSpan : androidx.gridlayout.widget.GridLayout.Spec = androidx.gridlayout.widget.GridLayout.spec(start, span)

        val param = androidx.gridlayout.widget.GridLayout.LayoutParams(rowSpan, colSpan)
        param.setGravity(Gravity.FILL)


        binding.tableNow.addView(item, param)

        // 새로 추가되는 강의는 hashmap 에도 추가
        // TODO : 강의마다 고유번호 서버에서 ID 받아와서 HASHMAP 의 KEY 로 쓸 것
        item.info = title.hashCode().toString()
        if(lectureHashMap.containsKey(item.info)){
            lectureHashMap[item.info]?.add(item)
        }
        else {
            lectureHashMap[item.info] = mutableListOf(item)
        }
        return item
    }

    // 시간표 가로 테두리 추가하는 함수
    private fun addBorder(startTime : Int, endTime : Int) {
        for(time in startTime until endTime) {
            if(time%4!=0) continue
            for(col in 2..10 step(2)) {
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
            if(view is TableCellView) binding.tableNow.getChildAt(c).bringToFront()
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
    private companion object {
        const val MON = 2
        const val TUE = 4
        const val WED = 6
        const val THU = 8
        const val FRI = 10
    }
}

