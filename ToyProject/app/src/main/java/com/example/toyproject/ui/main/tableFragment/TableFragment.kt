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


@AndroidEntryPoint
class TableFragment : Fragment() {

    private lateinit var binding: FragmentTableBinding

    var temp = 0

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

        // "RESULT_OK" :
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == AppCompatActivity.RESULT_OK) {

                }
            }

        makeCell("프로그래밍언어", "#A5D92F", 3, 6, MON)
        makeCell("프로그래밍언어", "#A5D92F", 3, 6, WED)

        makeCell("알고리즘", "#2BC167", 9, 6, TUE)
        makeCell("알고리즘", "#2BC167", 9, 6, THU)

        makeCell("시스템프로그래밍", "#4E49BF", 21, 6, TUE)
        makeCell("시스템프로그래밍", "#4E49BF", 21, 6, THU)
        makeCell("시스템프로그래밍", "#4E49BF", 33, 6, WED)


        // Grid 의 각 item 클릭 이벤트
        val grid = view.findViewById(R.id.table_now) as  androidx.gridlayout.widget.GridLayout
        val childCount = grid.childCount
        for (i in 0 until childCount) {
            val container = grid.getChildAt(i) as View
            container.setOnClickListener {
                Toast.makeText(context, it.id.toString(), Toast.LENGTH_SHORT).show()
            }
        }


        // 시간표 없을 때만 보이는, 새 시간표 만들기 버튼
        binding.fragmentTableMakeButton.setOnClickListener {
            // TODO : 지금 년도, 학기 구해서 Schedule 객체 목록에 추가하기
            binding.fragmentTableDefault.visibility = GONE
        }

        // 우상단 시간표 목록 버튼
        binding.tableButtonList.setOnClickListener {
            val intent = Intent(activity, TableListActivity::class.java)
            resultListener.launch(intent)
        }
        binding.tableButtonSetting.setOnClickListener {

        }
        binding.tableButtonAddClass.setOnClickListener {

        }
    }

    fun makeCell(title : String, color : String, start : Int, span: Int, col : Int) : TextView {
        val item = TextView(context)
        item.text = title
        item.setTypeface(item.typeface, Typeface.BOLD);
        item.setTextColor(Color.parseColor("#FFFFFF"))
        item.setBackgroundColor(Color.parseColor(color))
        item.width = resources.getDimension(R.dimen.table_col_width).toInt()
        item.height = (resources.getDimension(R.dimen.table_row_width)*span).toInt()
        item.gravity = Gravity.TOP
        item.id = title.hashCode()

        val colspan :  androidx.gridlayout.widget.GridLayout.Spec =  androidx.gridlayout.widget.GridLayout.spec(col,1)
        val rowSpan : androidx.gridlayout.widget.GridLayout.Spec = androidx.gridlayout.widget.GridLayout.spec(start, span)

        val param = androidx.gridlayout.widget.GridLayout.LayoutParams(rowSpan, colspan)
        param.setGravity(Gravity.FILL)

        binding.tableNow.addView(item, param)
        return item
    }
    
    fun findLatestTime() : Int{
        return 6
    }
    private companion object {
        const val MON = 2
        const val TUE = 3
        const val WED = 4
        const val THU = 5
        const val FRI = 6
    }
}

