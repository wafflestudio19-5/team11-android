package com.example.toyproject.ui.main.tableFragment


import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityTableAddLectureBinding
import dagger.hilt.android.AndroidEntryPoint
import android.app.TimePickerDialog.OnTimeSetListener
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.*
import android.widget.ArrayAdapter
import androidx.core.view.children
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min




@AndroidEntryPoint
class TableAddLectureDefaultActivity : AppCompatActivity() {



    private lateinit var binding : ActivityTableAddLectureBinding
    private val viewModel : TableAddLectureDefaultViewModel by viewModels()

    // TODO : 나중에 key 를 Lecture 로 바꿀 것
    private val lectureHashMap : HashMap<String, MutableList<TableCellView>> = hashMapOf()
    private val shadowHashMap : HashMap<TableAddCustomLectureView, TableCellView> = hashMapOf()

    // 시간표를 차지하고 있는 수업의 정보 (중복 체크용)
    // TODO : 시간표에서 셀 삭제하는 거 만들면 여기서도 지워줘야함
    private val occupyTable : HashMap<Pair<Int, Int>, String> = hashMapOf()
    private val shadowOccupyTable : HashMap<Pair<Int, Int>, Int> = hashMapOf()

    private val cells = mutableListOf<Cell>()

    var colWidth : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 미리보기 시간표의 열 너비 기종마다 일정하게 조정 (수정 필요)
        val display = windowManager.defaultDisplay
        val stageWidth = display.width
        colWidth = stageWidth/6 + stageWidth/54


        // 전환 이펙트 : 실행할 때 아래에서 올라오도록
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_nothing)

        binding = ActivityTableAddLectureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 디폴트 강의 정보 추가하기
        val titles = intent.getStringArrayListExtra("titles")
        val colors = intent.getStringArrayListExtra("colors")
        val starts = intent.getIntegerArrayListExtra("starts")
        val spans = intent.getIntegerArrayListExtra("spans")
        val cols = intent.getIntegerArrayListExtra("cols")
        for(i in 0 until titles!!.size) {
            makeCell(titles!![i], colors!![i], starts!![i], spans!![i], cols!![i])
            for(row in starts[i].toInt() until starts[i].toInt()+spans[i].toInt()) {
                occupyTable[Pair(row, cols[i])] = titles[i]
            }
        }
        
        // 그림자 점유 테이블 초기화
        for(row in 1..96) for(col in 2..10 step(2)) shadowOccupyTable[Pair(row, col)] = 0
        // 시간표에 가로 테두리 칠하기
        addBorder(findFastestTime(), findLatestTime())
        // 시간표 세로 길이 동적 조정
        adjustTableHeight(findFastestTime(), findLatestTime())


        // 완료 버튼
        binding.addLectureButton.setOnClickListener {
            // 수업명 비어 있으면 체크
            if(binding.addLectureTitle.text.isEmpty()) {
                Toast.makeText(this, "수업명을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            // 교수 정보는 체크 안함
            // 시간 입력 안했으면 체크
            else if(binding.addLectureScrollBottom.childCount==5) {
                Toast.makeText(this, "시간 정보를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            // 정상이면 중복 확인하고, 시간표애 추가
            else {
                // 섀도우 간 중복 확인
                if(checkShadowDuplicate()) {
                    Toast.makeText(this, "입력한 시간 중 겹치는 시간이 있습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 강의 정보 추출
                val title = binding.addLectureTitle.text.toString()
                val professor : String = binding.addLectureInstructor.text.toString()
                val locations : MutableList<String> = mutableListOf()
                // 일단 color 는 랜덤으로
                val colorCode = randomColor()


                val cells = mutableListOf<Cell>()

                val timeLocationLayout = binding.addLectureScrollBottom.children.iterator()
                // 각 시간대별로 셀 정보 추출 및 중복확인
                while(timeLocationLayout.hasNext()) {
                    val curr = timeLocationLayout.next()
                    if(curr is TableAddCustomLectureView) {
                        // 요일 정보 구해서 col 넘버로 변환
                        val dayTextView = curr.findViewById<TextView>(R.id.make_custom_lecture_day_text)
                        val col = dayStringToColInt(dayTextView.text.toString())

                        // 시작 시간 정보 구해서 start row 넘버로 변환
                        val startTimeTextView = curr.findViewById<TextView>(R.id.make_custom_lecture_start_time_text)
                        val start = timeStringToRowInt(startTimeTextView.text.toString())

                        // 시작 ~ 끝 길이 구해서 span 넘버로 전화
                        val endTimeTextView = curr.findViewById<TextView>(R.id.make_custom_lecture_end_time_text)
                        val end = timeStringToRowInt(endTimeTextView.text.toString())
                        val span = end - start

                        // 기존 시간표와 중복 확인
                        val isDuplicate : String? = checkDuplicate(start, end, col)
                        if(isDuplicate != null) {
                            Toast.makeText(this, "'$isDuplicate'수업과 시간이 겹칩니다.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        // 중복 확인했으면 후보 리스트에 추가                     // TODO : 통신 이후 ID 추가
                        cells.add(Cell(title, colorCode, start, span, col, title.hashCode(), null))
                    }
                    else {
                        continue
                    }
                }
                // 중복 확인 다 완료하고, 실제 시간표에 뷰 삽입
                val cellIterator = cells.iterator()
                while(cellIterator.hasNext()) {
                    val oneCell = cellIterator.next()
                    // TODO 통신해서 id 받아오기
                    makeCell(oneCell.title, oneCell.color, oneCell.start, oneCell.span, oneCell.col)
                    // 중복 테이블에 셀 정보 추가
                    for(row in oneCell.start until (oneCell.start+oneCell.span)) occupyTable[Pair(row, oneCell.col)] = title
                }

                // editText 초기화, 시간정보 뷰 삭제 -> TODO 통신 추가하면 통신 이후에 할 일
                binding.addLectureTitle.text.clear()
                binding.addLectureInstructor.text.clear()
                val childCount = binding.addLectureScrollBottom.childCount
                for(i in 0 until childCount-5) {
                    val toDelete = binding.addLectureScrollBottom.getChildAt(4)
                    // 각 삭제될 시간정보 뷰에 대응하는 그림자도 삭제
                    binding.tableNow.removeView(shadowHashMap[toDelete])        // TODO 바꿨음
                    shadowHashMap.remove(toDelete)
                    binding.addLectureScrollBottom.removeViewAt(4)
                }
                // 그림자 점유 테이블 초기화
                for(row in 1..96) for(col in 2..10 step(2)) shadowOccupyTable[Pair(row, col)] = 0
                shadowHashMap.clear()
            }
        }

        // 시간 및 장소 추가 눌렀을 때 ScrollView 안의 LinearLayout 에 새 View 추가하기
        binding.addLectureTimeLocation.setOnClickListener {
            // 추가할 View 객체
            val newCustomLecture= TableAddCustomLectureView(this)
            // 이 객체의 구성 요소들
            val daySelect = newCustomLecture.findViewById<LinearLayout>(R.id.make_custom_lecture_day)
            val startTimeSelect = newCustomLecture.findViewById<LinearLayout>(R.id.make_custom_lecture_start_time)
            val startTimeSelectText = newCustomLecture.findViewById<TextView>(R.id.make_custom_lecture_start_time_text)
            val endTimeSelect = newCustomLecture.findViewById<LinearLayout>(R.id.make_custom_lecture_end_time)
            val endTimeSelectText = newCustomLecture.findViewById<TextView>(R.id.make_custom_lecture_end_time_text)
            val deleteButton = newCustomLecture.findViewById<ImageView>(R.id.make_custom_lecture_delete_button)

            // 요일 선택
            daySelect.setOnClickListener {
                val builderSingle = AlertDialog.Builder(this)
                val arrayAdapter = ArrayAdapter(
                    this, android.R.layout.select_dialog_item,
                    resources.getStringArray(R.array.days))

                builderSingle.setAdapter(arrayAdapter) { _, which ->
                    val col = dayStringToColInt(daySelect.findViewById<TextView>(R.id.make_custom_lecture_day_text).text.toString())
                    val start = timeStringToRowInt(startTimeSelectText.text.toString())
                    val end = timeStringToRowInt(endTimeSelectText.text.toString())

                    // 확인 누르면 기존 그림자 삭제
                    val a = shadowHashMap.remove(newCustomLecture)
                    binding.tableNow.removeView(a)
                    removeShadowOccupy(start, end, col)

                    // 선택된 요일 표시 텍스트 바꾸기
                    val selectedDay = arrayAdapter.getItem(which)
                    it.findViewById<TextView>(R.id.make_custom_lecture_day_text).text = selectedDay.toString()
                    // 그림자 새로 생성
                    val colNew = dayStringToColInt(daySelect.findViewById<TextView>(R.id.make_custom_lecture_day_text).text.toString())
                    val shadow = makeShadowCell(start, end-start, colNew)
                    // 새로 생성된 섀도우로 스크롤 포커스 가게 하기
                    binding.addLecturePreview.post {
                        binding.addLecturePreview.scrollTo(0, shadow.top - 15)
                    }
                    shadowHashMap[newCustomLecture] = shadow
                    shadow.bringToFront()
                }
                builderSingle.show()
            }

            // 시작 시간 선택
            startTimeSelect.setOnClickListener {
                val listener =
                    OnTimeSetListener { _, hourOfDay, minute ->
                        val col = dayStringToColInt(daySelect.findViewById<TextView>(R.id.make_custom_lecture_day_text).text.toString())
                        val startBefore = timeStringToRowInt(startTimeSelectText.text.toString())
                        val endBefore = timeStringToRowInt(endTimeSelectText.text.toString())

                        // 확인 누르면 기존 그림자 삭제
                        binding.tableNow.removeView(shadowHashMap[newCustomLecture])
                        removeShadowOccupy(startBefore, endBefore, col)

                        var timeString = buildTimeString(hourOfDay, minute)
                        // 시작 시간이 23시 45분이면 끝을 지정할 수 없으므로 23시 30분으로 강제 조정 (실제 에타 반영)
                        if(hourOfDay==23 && minute == 45) {
                           timeString =  buildTimeString(23, 30)
                        }
                        // 시작 시간 TextView 표기되는 값 변경
                        startTimeSelectText.text = timeString

                        // 바뀐 시작 시간이 표기된 끝나는 시간보다 나중이면, 끝나는 시간을 1시간 뒤로 바꿔주기 (실제 에타 반영)
                        val end = endTimeSelectText.text.toString()
                        if(end <= timeString) {
                            // 그런데 시작 시간이 23시면 +1 할 수 없으니, 끝 시간을 23시 45분으로 조정 (실제 에타 반영)
                            if(hourOfDay==23) endTimeSelectText.text = buildTimeString(23, 45)
                            // 보통 경우는 그냥 1시간 뒤.
                            else endTimeSelectText.text = buildTimeString(hourOfDay+1, minute)
                        }
                        // 그림자 새로 생성
                        val startNew = timeStringToRowInt(startTimeSelectText.text.toString())
                        val endNew = timeStringToRowInt(endTimeSelectText.text.toString())
                        val shadow = makeShadowCell(startNew, endNew-startNew, col)
                        // 새로 생성된 섀도우로 스크롤 포커스 가게 하기
                        binding.addLecturePreview.post {
                            binding.addLecturePreview.scrollTo(0, shadow.top - 15)
                        }
                        shadowHashMap[newCustomLecture] = shadow
                        shadow.bringToFront()
                    }
                val hourMin = startTimeSelectText.text.split(":")
                // 설정 창 디폴트 값은 기존 값으로. (10시인 상태에서 클릭했으면 스피너도 10시부터 돌게)
                val dialog = CustomTimePickerDialog(
                    this@TableAddLectureDefaultActivity,  listener,
                    hourMin[0].toInt(), hourMin[1].toInt(), true)
                dialog.show()
            }

            // 끝나는 시간 스피너
            endTimeSelect.setOnClickListener {
                val listener =
                    OnTimeSetListener { _, hourOfDay, minute ->
                        val col = dayStringToColInt(daySelect.findViewById<TextView>(R.id.make_custom_lecture_day_text).text.toString())
                        val startBefore = timeStringToRowInt(startTimeSelectText.text.toString())
                        val endBefore = timeStringToRowInt(endTimeSelectText.text.toString())

                        // 확인 누르면 기존 그림자 삭제
                        binding.tableNow.removeView(shadowHashMap[newCustomLecture])
                        removeShadowOccupy(startBefore, endBefore, col)

                        var timeString  = buildTimeString(hourOfDay, minute)

                        // 끝 시간이 00시 00분이면 시작 시간을 지정할 수 없으므로 23:30 ~ 23:45 로 변경 (에타 사양)
                        if(hourOfDay==0 && minute==0) {
                            startTimeSelectText.text = buildTimeString(23, 30)
                            timeString = buildTimeString(23, 45)
                        }
                        endTimeSelectText.text = timeString

                        // 끝 시간이 시작보다 이전일 경우 -> 시작 시간을 (끝 시간 - 1시간)으로.
                        if(timeString<=startTimeSelectText.text.toString()) {
                            // 그런데 끝 시간이 0시면 시작 시간을 00시 00분로.
                            if(hourOfDay==0) {
                                startTimeSelectText.text = buildTimeString(0, 0)
                            }
                            else {
                                startTimeSelectText.text = buildTimeString(hourOfDay-1, minute)
                            }
                        }
                        // 그림자 새로 생성
                        val startNew = timeStringToRowInt(startTimeSelectText.text.toString())
                        val endNew = timeStringToRowInt(endTimeSelectText.text.toString())
                        val shadow = makeShadowCell(startNew, endNew-startNew, col)
                        // 새로 생성된 섀도우로 스크롤 포커스 가게 하기
                        binding.addLecturePreview.post {
                            binding.addLecturePreview.scrollTo(0, shadow.top - 15)
                        }
                        shadowHashMap[newCustomLecture] = shadow
                        shadow.bringToFront()
                    }
                val hourMin = endTimeSelectText.text.split(":")
                // 설정 창 디폴트 값은 기존 값으로. (10시인 상태에서 클릭했으면 스피너도 10시부터 돌게)
                val dialog = CustomTimePickerDialog(
                    this@TableAddLectureDefaultActivity,  listener,
                    hourMin[0].toInt(), hourMin[1].toInt(), true)
                dialog.show()
            }

            // 삭제 버튼 누르면 scrollView 에서 지워지기
            deleteButton.setOnClickListener {
                val mBuilder = AlertDialog.Builder(this)
                    .setMessage("삭제하시겠습니까?")
                    .setNegativeButton("취소") { dialogInterface, _ ->
                        dialogInterface.dismiss()
                    }
                    .setPositiveButton("확인") { _, _ ->
                        val col = dayStringToColInt(daySelect.findViewById<TextView>(R.id.make_custom_lecture_day_text).text.toString())
                        val start = timeStringToRowInt(startTimeSelectText.text.toString())
                        val end = timeStringToRowInt(endTimeSelectText.text.toString())

                        // 확인 누르면 해당 시간의 그림자 삭제, 해당 뷰도 삭제
                        removeShadowOccupy(start, end, col)
                        binding.tableNow.removeView(shadowHashMap[newCustomLecture])
                        shadowHashMap.remove(newCustomLecture)

                        // 그림자 없어지면 시간표 height 도 재조정
                        adjustTableHeight(min(findFastestShadow(), findFastestTime()),
                                          max(findLatestShadow(), findLatestTime()))
                        binding.addLectureScrollBottom.removeView(newCustomLecture)
                    }
                val dialog = mBuilder.create()
                dialog.findViewById<TextView>(android.R.id.message)?.textSize = 13f
                dialog.show()
            }

            // 이렇게 완성한 객체를 scrollView 에 추가
            binding.addLectureScrollBottom.addView(newCustomLecture, binding.addLectureScrollBottom.childCount-1)
            // 해당 시간의 그림자도 미리보기 시간표에 추가
            val col = dayStringToColInt(daySelect.findViewById<TextView>(R.id.make_custom_lecture_day_text).text.toString())
            val start = timeStringToRowInt(startTimeSelectText.text.toString())
            val end = timeStringToRowInt(endTimeSelectText.text.toString())
            val shadow = makeShadowCell(start, end-start, col)
            // 새로 생성된 섀도우로 스크롤 포커스 가게 하기
            binding.addLecturePreview.post {
                binding.addLecturePreview.scrollTo(0, shadow.top - 15)
            }
            shadowHashMap[newCustomLecture] = shadow
            shadow.bringToFront()
        }

        // X 버튼
        binding.tableAddLectureCloseButton.setOnClickListener {
            val titlesFinal : ArrayList<String?> = arrayListOf()
            val colorsFinal : ArrayList<String?> = arrayListOf()
            val startsFinal : ArrayList<Int> = arrayListOf()
            val spansFinal : ArrayList<Int> = arrayListOf()
            val colsFinal : ArrayList<Int> = arrayListOf()

            val iter = cells.iterator()
            while(iter.hasNext()) {
                val cell = iter.next()
                titlesFinal.add(cell.title)
                colorsFinal.add(cell.color)
                startsFinal.add(cell.start)
                spansFinal.add(cell.span)
                colsFinal.add(cell.col)
            }
            setResult(RESULT_OK, intent.putStringArrayListExtra("titles", titlesFinal))
            setResult(RESULT_OK, intent.putStringArrayListExtra("colors", colorsFinal))
            setResult(RESULT_OK, intent.putIntegerArrayListExtra("starts", startsFinal))
            setResult(RESULT_OK, intent.putIntegerArrayListExtra("spans", spansFinal))
            setResult(RESULT_OK, intent.putIntegerArrayListExtra("cols", colsFinal))
            finish()
            // 끝낼 땐 아래로 내려가기
            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)


    }

    override fun onBackPressed() {
        val titles : ArrayList<String?> = arrayListOf()
        val colors : ArrayList<String?> = arrayListOf()
        val starts : ArrayList<Int> = arrayListOf()
        val spans : ArrayList<Int> = arrayListOf()
        val cols : ArrayList<Int> = arrayListOf()

        val iter = cells.iterator()
        while(iter.hasNext()) {
            val cell = iter.next()
            titles.add(cell.title)
            colors.add(cell.color)
            starts.add(cell.start)
            spans.add(cell.span)
            cols.add(cell.col)
        }
        setResult(RESULT_OK, intent.putStringArrayListExtra("titles", titles))
        setResult(RESULT_OK, intent.putStringArrayListExtra("colors", colors))
        setResult(RESULT_OK, intent.putIntegerArrayListExtra("starts", starts))
        setResult(RESULT_OK, intent.putIntegerArrayListExtra("spans", spans))
        setResult(RESULT_OK, intent.putIntegerArrayListExtra("cols", cols))
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
    private fun makeCell(title : String, color : String, start : Int, span: Int, col : Int) : TableCellView {
        val item = TableCellView(this)
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

        cells.add(Cell(title, color, start, span, col, item.info.toInt(), null))
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
        adjustTableHeight(min(findFastestShadow(), findFastestTime()),
            max(findLatestShadow(), findLatestTime()))

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

                val colspan :  androidx.gridlayout.widget.GridLayout.Spec =  androidx.gridlayout.widget.GridLayout.spec(col,1)
                val rowSpan : androidx.gridlayout.widget.GridLayout.Spec = androidx.gridlayout.widget.GridLayout.spec(time, 1)
                val param = androidx.gridlayout.widget.GridLayout.LayoutParams(rowSpan, colspan)
                param.setGravity(Gravity.FILL)

                binding.tableNow.addView(item, param)
            }
        }
        // 테두리에 강의가 가려져서 나뉘어 보이지 않도록
//        for(c in 0 until binding.tableNow.childCount) {
//            val view = binding.tableNow.getChildAt(c)
//            if(view is TableCellView) binding.tableNow.getChildAt(c).bringToFront()
//        }
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
    private fun checkDuplicate(start : Int, end : Int, col : Int) : String? {
        for(row in start until end) {
            if(occupyTable.containsKey(Pair(row, col))) {
                return  occupyTable[Pair(row, col)].toString()
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
        if(col==2) return "월"
        if(col==4) return "화"
        if(col==6) return "수"
        if(col==8) return "목"
        else return "금"
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

data class Cell (
    val title : String,
    val color : String,
    val start : Int,
    val span : Int,
    val col : Int,
    val custom_id : Int,
    val lecture_id : Int?
)


