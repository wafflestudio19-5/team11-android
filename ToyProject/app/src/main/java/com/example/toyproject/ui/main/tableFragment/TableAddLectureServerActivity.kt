package com.example.toyproject.ui.main.tableFragment

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityTableAddLectureServerBinding
import com.example.toyproject.network.dto.table.Lecture
import com.example.toyproject.ui.main.homeFragment.BrowseActivity
import com.example.toyproject.ui.review.LectureInfoActivity
import com.google.common.base.Joiner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.RuntimeException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min

@AndroidEntryPoint
class TableAddLectureServerActivity : AppCompatActivity() {

    lateinit var binding: ActivityTableAddLectureServerBinding
    private val viewModel : TableAddLectureServerViewModel by viewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val lectureHashMap: HashMap<Int, MutableList<TableCellView>> = hashMapOf()
    private val shadowHashMap: HashMap<TableAddCustomLectureView, TableCellView> = hashMapOf()

    // 시간표를 차지하고 있는 수업의 정보 (중복 체크용)
    private val occupyTable: HashMap<Pair<Int, Int>, Cell> = hashMapOf()
    private val shadowOccupyTable: HashMap<Pair<Int, Int>, Int> = hashMapOf()

    // 미리보기 시간표의 셀들
    private val cells = mutableListOf<Cell>()

    private var colWidth: Int = 0

    // 검색어 필터 폴더 경로 저장
    private var majorPath : ArrayList<String> = arrayListOf("전체")

    // 서버에서 불러온 강의들 recyclerView
    private lateinit var lectureListAdapter : TableAddLectureServerAdapter
    private lateinit var lectureListLayoutManager: LinearLayoutManager
    private var page = 0

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

    // 필터 적용하기
    private val filterResultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                // 전공/영역 받아온 정보 적용
                val queryMajor : String? = it.data?.getStringExtra("major")
                if(queryMajor != null) {
                    // 선택 항목 정보, 뷰 설정
                    binding.filterMajorText.text = queryMajor
                    binding.filterMajorText.setTextColor(ContextCompat.getColor(this, R.color.Primary))
                    binding.filterMajorText.setTypeface(null, Typeface.BOLD)
                    binding.filterMajorClear.visibility = View.VISIBLE
                    // 선택된 항목 폴더 경로 정보
                    majorPath.clear()
                    majorPath.addAll(it.data!!.getStringArrayListExtra("path")!!)
                }
                // 학년, 구분, 학점 필터 적용
                applyFilterWithCheckBox()
                // 검색어 필터 적용
                try {
                    binding.filterQueryText.text = sharedPreferences.getString("filter_query_text", null)!!
                    binding.filterQueryText.setTextColor(ContextCompat.getColor(this, R.color.Primary))
                    val mBuilder = StringBuilder()
                    mBuilder.append(sharedPreferences.getString("filter_query_field", null))
                    mBuilder.append(": ")
                    binding.filterQueryField.text = mBuilder.toString()
                    binding.filterQueryClear.visibility = View.VISIBLE
                } catch(n : NullPointerException) {
                    binding.filterQueryText.text = "없음"
                    binding.filterQueryField.text = "검색어: "
                }
                // 새로 검색
                collectFilterCondition(0, 20)
            }
        }

    private fun applyFilterWithCheckBox() {
        // 학년 필터 적용
        val yearInfoArray = mutableListOf<String>()
        sharedPreferences.getString("filter_year", null)!!.split("-").forEachIndexed { idx, string ->
            if (string.toBoolean()) {
                if (idx == 4) yearInfoArray.add("기타")
                else yearInfoArray.add((idx + 1).toString())
            }
        }
        if(yearInfoArray.size==5) {
            binding.filterYearText.text = "전체"
            binding.filterYearText.setTextColor(ContextCompat.getColor(this@TableAddLectureServerActivity, R.color.color_filter_text_default))
            binding.filterYearClear.visibility = View.GONE
        }
        else {
            binding.filterYearText.setTextColor(ContextCompat.getColor(this@TableAddLectureServerActivity, R.color.Primary))
            binding.filterYearText.setTypeface(null, Typeface.BOLD)
            binding.filterYearClear.visibility = View.VISIBLE
            if(yearInfoArray.size<=2) {
                binding.filterYearText.text = Joiner.on(", ").join(yearInfoArray)
            }
            else if(yearInfoArray.size>2) {
                val etc = yearInfoArray.size - 2
                val sBuilder = StringBuilder()
                sBuilder.append(Joiner.on(", ").join(yearInfoArray.subList(0, 2)))
                sBuilder.append(" 외 ${etc}개")
                binding.filterYearText.text = sBuilder.toString()
            }
        }
        // 구분 필터 적용
        val typeArray = arrayOf("전선", "교양", "일선", "논문", "전필", "교직")
        val typeInfoArray = mutableListOf<String>()
        sharedPreferences.getString("filter_type", null)!!.split("-").forEachIndexed { idx, str ->
            if(str.toBoolean()) {
                typeInfoArray.add(typeArray[idx])
            }
        }
        if(typeInfoArray.size==6) {
            binding.filterTypeText.text = "전체"
            binding.filterTypeText.setTextColor(ContextCompat.getColor(this@TableAddLectureServerActivity, R.color.color_filter_text_default))
            binding.filterTypeClear.visibility = View.GONE
        }
        else {
            binding.filterTypeText.setTextColor(ContextCompat.getColor(this@TableAddLectureServerActivity, R.color.Primary))
            binding.filterTypeText.setTypeface(null, Typeface.BOLD)
            binding.filterTypeClear.visibility = View.VISIBLE
            if(typeInfoArray.size<=2) {
                binding.filterTypeText.text = Joiner.on(", ").join(typeInfoArray)
            }
            else if(typeInfoArray.size>2) {
                val etc = typeInfoArray.size - 2
                val sBuilder = StringBuilder()
                sBuilder.append(Joiner.on(", ").join(typeInfoArray.subList(0, 2)))
                sBuilder.append(" 외 ${etc}개")
                binding.filterTypeText.text = sBuilder.toString()
            }
        }
        // 학점 필터 적용
        val creditArray = arrayOf("0", "0,5", "1", "1.5", "2", "2.5", "3", "3.5", "4~")
        val creditInfoArray = mutableListOf<String>()
        sharedPreferences.getString("filter_credit", null)!!.split("-").forEachIndexed { idx, str ->
            if(str.toBoolean()) {
                creditInfoArray.add(creditArray[idx])
            }
        }
        if(creditInfoArray.size==creditArray.size) {
            binding.filterCreditText.text = "전체"
            binding.filterCreditText.setTextColor(ContextCompat.getColor(this@TableAddLectureServerActivity, R.color.color_filter_text_default))
            binding.filterCreditClear.visibility = View.GONE
        }
        else {
            binding.filterCreditText.setTextColor(ContextCompat.getColor(this@TableAddLectureServerActivity, R.color.Primary))
            binding.filterCreditText.setTypeface(null, Typeface.BOLD)
            binding.filterCreditClear.visibility = View.VISIBLE
            if(creditInfoArray.size<=2) {
                binding.filterCreditText.text = Joiner.on(", ").join(creditInfoArray)
            }
            else if(creditInfoArray.size>2) {
                val etc = creditInfoArray.size - 2
                val sBuilder = StringBuilder()
                sBuilder.append(Joiner.on(", ").join(creditInfoArray.subList(0, 2)))
                sBuilder.append(" 외 ${etc}개")
                binding.filterCreditText.text = sBuilder.toString()
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
        if(!sharedPreferences.contains("favorite_major")) {
            sharedPreferences.edit {
                this.putStringSet("favorite_major", setOf())
            }
        }
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
            collectFilterCondition(0, 20)
        }
        // 2. 검색어 필터
        if(sharedPreferences.contains("filter_query_text")) {
            binding.filterQueryText.text = sharedPreferences.getString("filter_query_text", null)
            binding.filterQueryText.setTextColor(ContextCompat.getColor(this, R.color.Primary))
            val mBuilder = StringBuilder()
            mBuilder.append(sharedPreferences.getString("filter_query_field", null))
            mBuilder.append(": ")
            binding.filterQueryField.text = mBuilder.toString()
            binding.filterQueryClear.visibility = View.VISIBLE
        }
        binding.filterQuery.setOnClickListener {
            val intent = Intent(this, TableAddFilterQueryActivity::class.java)
            filterResultListener.launch(intent)
        }
        binding.filterQueryClear.setOnClickListener {
            binding.filterQueryText.text = "없음"
            binding.filterQueryText.setTextColor(ContextCompat.getColor(this, R.color.color_filter_text_default))
            binding.filterQueryField.text = "검색어: "
            binding.filterQueryClear.visibility = View.GONE
            sharedPreferences.edit {
                this.remove("filter_query_text")
                this.remove("filter_query_field")
            }
            collectFilterCondition(0, 20)
        }

        // 3. 정렬 필터
        var queryChecked = sharedPreferences.getInt("filter_sort_index", 0)
        val choices = arrayOf("기본", "과목코드", "과목명", "별점 높은 순", "별점 낮은 순", "담은인원 많은순",
            "담은인원 적은순", "경쟁률 높은순", "경쟁률 낮은순")
        binding.filterSortText.text = choices[queryChecked]
        if(choices[queryChecked]=="기본") binding.filterSortText.setTextColor(ContextCompat.getColor(this, R.color.color_filter_text_default))
        else {
            binding.filterSortText.setTextColor(ContextCompat.getColor(this, R.color.Primary))
            binding.filterSortClear.visibility = View.VISIBLE
        }
        binding.filterSort.setOnClickListener {
            // TODO : 빨간색 하기
            queryChecked = sharedPreferences.getInt("filter_sort_index", 0)
            val mBuilder = AlertDialog.Builder(this)
                .setTitle("정렬")
                .setSingleChoiceItems(choices, queryChecked, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        if(p1 > 2) Toast.makeText(this@TableAddLectureServerActivity, "해당 필터는 미구현입니다", Toast.LENGTH_SHORT).show()
                        binding.filterSortText.text = choices[p1]
                        binding.filterSortText.setTextColor(ContextCompat.getColor(this@TableAddLectureServerActivity, R.color.Primary))
                        binding.filterSortText.setTypeface(null, Typeface.BOLD)
                        binding.filterSortClear.visibility = View.VISIBLE
                        if(choices[p1]=="기본") {
                            binding.filterSortText.setTextColor(ContextCompat.getColor(this@TableAddLectureServerActivity, R.color.color_filter_text_default))
                            binding.filterSortClear.visibility = View.GONE
                        }
                        sharedPreferences.edit {
                            this.putInt("filter_sort_index", p1)
                        }
                        p0!!.dismiss()
                        collectFilterCondition(0, 20)
                    }
                })
            val dialog = mBuilder.create()
            dialog.show()
        }
        binding.filterSortClear.setOnClickListener {
            binding.filterSortText.setTextColor(ContextCompat.getColor(this@TableAddLectureServerActivity, R.color.color_filter_text_default))
            binding.filterSortClear.visibility = View.GONE
            binding.filterSortText.text = "기본"
            sharedPreferences.edit { this.remove("filter_sort_index") }
            collectFilterCondition(0, 20)
        }
        // 5. 학년, 구분, 학점 필터
        if(!sharedPreferences.contains("filter_year")) {
            sharedPreferences.edit {
                this.putString("filter_year", "true-true-true-true-true") }
        }
        if(!sharedPreferences.contains("filter_type")) {
            sharedPreferences.edit {
                this.putString("filter_type", "true-true-true-true-true-true") }
        }
        if(!sharedPreferences.contains("filter_credit")) {
            sharedPreferences.edit {
                this.putString("filter_credit", "true-true-true-true-true-true-true-true-true") }
        }
        binding.filterYear.setOnClickListener {
            val intent = Intent(this, TableAddFilterCheckboxActivity::class.java)
            intent.putExtra("mode", "filter_year")
            filterResultListener.launch(intent)
        }
        binding.filterType.setOnClickListener {
            val intent = Intent(this, TableAddFilterCheckboxActivity::class.java)
            intent.putExtra("mode", "filter_type")
            filterResultListener.launch(intent)
        }
        binding.filterCredit.setOnClickListener {
            val intent = Intent(this, TableAddFilterCheckboxActivity::class.java)
            intent.putExtra("mode", "filter_credit")
            filterResultListener.launch(intent)
        }
        binding.filterYearClear.setOnClickListener {
            binding.filterYearText.text = "전체"
            binding.filterYearClear.visibility = View.GONE
            sharedPreferences.edit {
                this.putString("filter_year", "true-true-true-true-true")
            }
            binding.filterYearText.setTextColor(ContextCompat.getColor(this, R.color.color_filter_text_default))
            collectFilterCondition(0, 20)
        }
        binding.filterTypeClear.setOnClickListener {
            binding.filterTypeText.text = "전체"
            binding.filterTypeClear.visibility = View.GONE
            sharedPreferences.edit {
                this.putString("filter_type", "true-true-true-true-true-true")
            }
            binding.filterTypeText.setTextColor(ContextCompat.getColor(this, R.color.color_filter_text_default))
            collectFilterCondition(0, 20)
        }
        binding.filterCreditClear.setOnClickListener {
            binding.filterCreditText.text = "전체"
            binding.filterCreditClear.visibility = View.GONE
            sharedPreferences.edit {
                this.putString("filter_credit", "true-true-true-true-true-true-true-true-true")
            }
            binding.filterCreditText.setTextColor(ContextCompat.getColor(this, R.color.color_filter_text_default))
            collectFilterCondition(0, 20)
        }

        // 액티비티 시작할 때 필터 뷰 적용
        applyFilterWithCheckBox()


        // recyclerView 부분
        lectureListAdapter = TableAddLectureServerAdapter(this)
        lectureListLayoutManager= LinearLayoutManager(this)
        binding.addServerLectureRecyclerView.apply {
            adapter = lectureListAdapter
            layoutManager = lectureListLayoutManager
        }
        binding.addServerLectureRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                if(!binding.addServerLectureRecyclerView.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount){
                    collectFilterCondition(20*page, 20)
                }
            }
        })

        // 필터 값 모아서 검색
        collectFilterCondition(0, 20)

        // 강의 아이템들 클릭 이벤트 (노랗게 바꾸고 버튼 3개 등장)
        val shadows = mutableListOf<TableCellView>()
        lectureListAdapter.setItemClickListener(object : TableAddLectureServerAdapter.OnLectureClickListener {
            override fun removeShadow() {
                // 있던 그림자 싹 제거
                shadows.forEach { shadow ->
                    binding.tableNow.removeView(shadow)
                    for (row in 1..96) for (col in 2..10 step (2)) shadowOccupyTable[Pair(row, col)] = 0
                    // 동적 시간표 길이 적용
                    adjustTableHeight(findFastestTime(), findLatestTime())
                    addBorder(findFastestTime(), findLatestTime())
                }
                shadows.clear()
            }
            override fun onItemClick(v: View, data: Lecture, position: Int) {
                // 시간 정보로 그림자 추가
                val times = parseServerTimeInput(data.time)
                times?.forEachIndexed { idx, time ->
                    val newShadow = makeShadowCell(time.second.first, time.second.second, time.first)
                    shadows.add(newShadow)
                    for (row in 1..96) for (col in 2..10 step (2)) shadowOccupyTable[Pair(row, col)] = 0
                    if(idx==times.size-1) {
                        // 새로 생성된 섀도우로 스크롤 포커스 가게 하기
                        binding.addServerLecturePreview.post {
                            binding.addServerLecturePreview.scrollTo(0, newShadow.top - 15)
                        }
                    }
                    newShadow.bringToFront()
                }
            }
            // 시간표에 추가 버튼
            override fun addItem(parent : View, v: View, lecture: Lecture, position: Int) {
                val moreView = parent.findViewById<LinearLayout>(R.id.server_lecture_item_more_layout)
                moreView.visibility = View.GONE
                parent.setBackgroundColor(ContextCompat.getColor(this@TableAddLectureServerActivity, R.color.Background))

                // 시간대마다 셀 추가
                val times = parseServerTimeInput(lecture.time)
                val locations = if(lecture.location!=null) lecture.location.split("/")
                                else null
                val tempCells = mutableListOf<Cell>()

                val colorCode = randomColor()
                times?.forEachIndexed { idx, time ->
                    // 기존 시간표와 중복 확인
                    val isDuplicate : String? = checkDuplicate(time.second.first,
                        time.second.first+time.second.second, time.first, -1)
                    if(isDuplicate != null) {
                        Toast.makeText(this@TableAddLectureServerActivity, "'$isDuplicate'수업과 시간이 겹칩니다.", Toast.LENGTH_SHORT).show()
                        // 있던 그림자 싹 제거
                        shadows.forEach { shadow ->
                            binding.tableNow.removeView(shadow)
                            for (row in 1..96) for (col in 2..10 step (2)) shadowOccupyTable[Pair(row, col)] = 0
                            // 동적 시간표 길이 적용
                            adjustTableHeight(findFastestTime(), findLatestTime())
                            addBorder(findFastestTime(), findLatestTime())
                        }
                        shadows.clear()
                        return
                    }
                    var location = ""
                    if(locations!=null && idx<locations.size) location = locations[idx]
                    val newItem = Cell(
                        lecture.subject_name, colorCode, time.second.first,
                        time.second.second, time.first, lecture.subject_name.hashCode(), lecture.id,
                        lecture.professor, location, memo = "")
                    tempCells.add(newItem)
                }
                var flag = true
                CoroutineScope(Dispatchers.Main).launch {
                    viewModel.addLectureById(lecture.id)
                    viewModel.addServerLectureFlow.collect {
                        if(it==null) {
                            Toast.makeText(this@TableAddLectureServerActivity, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
                        }
                        else if(flag) {
                            if(it.time_location==null) Toast.makeText(this@TableAddLectureServerActivity,
                                "시간 정보가 없는 강의입니다.", Toast.LENGTH_SHORT).show()

                            val customId = it.id
                            val memo = it.memo
                            val subjectProfessor = it.subject_professor
                            val url = it.url
                            tempCells.forEach { newItem ->
                                newItem.custom_id = customId
                                newItem.memo = memo
                                newItem.url = url
                                newItem.subject_professor = subjectProfessor
                                makeCell(newItem)
                            }
                            flag = false
                            // 동적 시간표 길이 적용
                            adjustTableHeight(findFastestTime(), findLatestTime())
                            addBorder(findFastestTime(), findLatestTime())

                            // 하이라이트 됐던 서버 강의 뷰 원상복구
                            moreView.visibility = View.GONE
                            parent.setBackgroundColor(ContextCompat.getColor(this@TableAddLectureServerActivity, R.color.Background))
                        }
                    }
                }
            }
            override fun showReview(id: Int) {
                val intent = Intent(this@TableAddLectureServerActivity, LectureInfoActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            }

            override fun openSyllabus(url: String) {
                val intent = Intent(this@TableAddLectureServerActivity, BrowseActivity::class.java)
                intent.putExtra("url", url)
                intent.putExtra("title", "강의계획서 | 서울대학교 수강신청 시스템")
                startActivity(intent)
            }
        })

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

    private fun collectFilterCondition(offset : Int, limit : Int = 20) {
        if(offset==0) page=0


        val major = if(binding.filterMajorText.text=="전체") null
        else {
            try {
                "${majorPath[2]} ${majorPath[3]}"
            } catch (e : RuntimeException) {
                "${majorPath[2]} ${binding.filterMajorText.text}"
            }
        }

        val subjectName = if(binding.filterQueryField.text=="과목명: ") binding.filterQueryText.text.toString()
        else null

        val professor = if(binding.filterQueryField.text=="교수명: ") {
            binding.filterQueryText.text.toString()
        }
        else null

        val location = if(binding.filterQueryField.text=="장소:") binding.filterQueryText.text.toString()
        else null

        val subjectCode= if(binding.filterQueryField.text=="과목코드: ") binding.filterQueryText.text.toString()
        else null

        val yearQuery = mutableListOf<String>()
        sharedPreferences.getString("filter_year", null)!!.split("-").forEachIndexed { idx, string ->
            if (string.toBoolean()) {
                if (idx == 4) yearQuery.add("5")
                else yearQuery.add((idx + 1).toString())
            }}
        val year = if(binding.filterYearText.text=="전체") null
        else Joiner.on(" ").join(yearQuery).toString()


        val typeArray = arrayOf("전선", "교양", "일선", "논문", "전필", "교직")
        val typeQuery = mutableListOf<String>()
        sharedPreferences.getString("filter_type", null)!!.split("-").forEachIndexed { idx, str ->
            if(str.toBoolean()) {
                typeQuery.add(typeArray[idx]) } }

        val category = if(binding.filterTypeText.text=="전체") null
        else Joiner.on(" ").join(typeQuery).toString()


        val creditArray = arrayOf("0", "100", "1", "100", "2", "200", "3", "300", "4")
        val creditQuery = mutableListOf<String>()
        sharedPreferences.getString("filter_credit", null)!!.split("-").forEachIndexed { idx, str ->
            if(str.toBoolean()) {
                creditQuery.add(creditArray[idx])
            } }

        val credit = if(binding.filterCreditText.text=="전체") null
        else Joiner.on(" ").join(creditQuery).toString()

        val sort : String? = when (binding.filterSortText.text) {
            "전체" -> null
            "과목코드" -> "subject_code"
            "과목명" -> "subject_name"
            else -> null
        }


        // 서버에서 강의 목록 불러오기
        var flag = true
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.loadServerLecture(offset=offset, limit=limit, subject_name =subjectName, professor = professor,
            subject_code = subjectCode, location = location, department = major, grade=year,
            credit=credit, category = category, sort=sort)
            viewModel.serverLectureGetFlow.collect {
                if(it==null) {
                    Toast.makeText(this@TableAddLectureServerActivity, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
                }
                else {
                    if(flag) {
                        if(page==0) {
                            lectureListAdapter.highlighted = -1
                            lectureListAdapter.clearLectures()
                            lectureListAdapter.setLectures(it.results.toMutableList())
                            lectureListAdapter.notifyItemRemoved((page+1)*20)
                        }
                        else lectureListAdapter.addMoreLectures(it.results.toMutableList())
                        page ++
                        flag = false
                    }
                }
            }
        }
    }

    // 시간표에 셀 추가하는 함수
    private fun makeCell(cellObject : Cell) : TableCellView {
        val item = TableCellView(this)
        item.title.text = cellObject.title
        item.title.setTypeface(null, Typeface.BOLD)
        item.title.setTextColor(Color.parseColor("#FFFFFF"))
        item.setBackgroundColor(Color.parseColor(cellObject.color))
        item.location.text = cellObject.location
        // item.width = resources.getDimension(R.dimen.table_col_width).toInt()

//        item.width = colWidth
//        item.height = (resources.getDimension(R.dimen.table_row_width)*cellObject.span).toInt()
//        item.gravity = Gravity.TOP
        val layoutParam = item.topLayout.layoutParams
        layoutParam.width = colWidth
        layoutParam.height = (resources.getDimension(R.dimen.table_row_width)*cellObject.span).toInt()
        item.topLayout.layoutParams = layoutParam


        val colSpan :  androidx.gridlayout.widget.GridLayout.Spec =  androidx.gridlayout.widget.GridLayout.spec(cellObject.col,1)
        val rowSpan : androidx.gridlayout.widget.GridLayout.Spec = androidx.gridlayout.widget.GridLayout.spec(cellObject.start, cellObject.span)

        val param = androidx.gridlayout.widget.GridLayout.LayoutParams(rowSpan, colSpan)
        param.setGravity(Gravity.FILL)

        binding.tableNow.addView(item, param)

        // 새로 추가되는 강의는 hashmap 에도 추가
        item.info = cellObject.custom_id
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
//        item.width = colWidth
//        item.height = (resources.getDimension(R.dimen.table_row_width)*span).toInt()
        val layoutParam = item.topLayout.layoutParams
        layoutParam.width = colWidth
        layoutParam.height = (resources.getDimension(R.dimen.table_row_width)*span).toInt()
        item.topLayout.layoutParams = layoutParam

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

    // 시간표 가로 테두리 추가하는 함수
    private fun addBorder(startTime : Int, endTime : Int) {
        binding.tableNow.children.forEach { item ->
            if(item is TableBorderView) binding.tableNow.removeView(item)
        }
        for(time in startTime until endTime) {
            if(time%4!=0) continue
            for(col in 2..10 step(2)) {
                if (occupyTable.containsKey(Pair(time, col))) continue  // 기존 시간표에 있는 사이에는 border 치지 말기
                if(shadowOccupyTable[Pair(time, col)]!!>0) continue  // 역시, 그림자도 border 로 쪼개지지 않도록
                val item = TableBorderView(this)
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
    private fun dayStringToColInt(day : String) : Int{
        return when(day) {
            "월요일" -> MON
            "월" -> MON
            "화요일" -> TUE
            "화" -> TUE
            "수요일" -> WED
            "수" -> WED
            "목요일" -> THU
            "목" -> THU
            else -> FRI
        }
    }
    private fun timeStringToRowInt(time : String) : Int {
        val hour = time.split(":")[0].toInt()
        val min = time.split(":")[1].toInt()

        // 자정이 row = 1 이고, 15분 마다 row +1 이니 이렇게 변환
        return hour*4 + min/15 + 1
    }
    private fun parseServerTimeInput(input : String?) : MutableList<Pair<Int, Pair<Int, Int>>>? {
        if(input==null) return null
        if(input.isEmpty()) return mutableListOf()
        val processedTimes = mutableListOf<Pair<Int, Pair<Int, Int>>>()

        val times = input.split("/")
        times.forEach { time ->
            val day: String = time.substring(0, 1)
            val timeStrings = time.substring(2, time.length - 1).split("~")


            val col = dayStringToColInt(day)
            val startRow = timeStringToRowInt(timeStrings[0])
            val endRow = timeStringToRowInt(timeStrings[1])
            processedTimes.add(Pair(col, Pair(startRow, endRow-startRow)))
        }
        return processedTimes
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
