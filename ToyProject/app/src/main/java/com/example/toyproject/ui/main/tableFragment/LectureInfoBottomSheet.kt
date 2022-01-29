package com.example.toyproject.ui.main.tableFragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.R
import com.example.toyproject.ui.main.homeFragment.BrowseActivity
import com.example.toyproject.ui.review.LectureInfoActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.common.base.Joiner

class LectureInfoBottomSheet : BottomSheetDialogFragment() {

    private lateinit var deleteInterface : DeleteCellInterface


    private val resultListener =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            // "RESULT_OK" : 메모 적용
            if(it.resultCode == AppCompatActivity.RESULT_OK) {
                deleteInterface.memo(it.data!!.getStringExtra("memo").toString())
            }
            dismiss()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_table_lecture_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 이 프래그먼트의 구성 뷰들
        val titleView = view.findViewById<TextView>(R.id.cell_info_title)
        val instructorView = view.findViewById<TextView>(R.id.cell_info_instructor)
        val timeView = view.findViewById<TextView>(R.id.cell_info_time)
        val locationView = view.findViewById<TextView>(R.id.cell_info_location)

        // TableFragment 에서 전달받은 cellInfo 얻기
        val cell : Cell = requireArguments().getParcelable("cellInfo")!!
        val friends : ArrayList<Cell> = requireArguments().getParcelableArrayList("friends")!!

        // 뷰 내용 채우기
        titleView.text = cell.title
        instructorView.text = cell.instructor

        val allTime = arrayListOf<String>()
        val allLocation = arrayListOf<String>()
        friends.withIndex().forEach { piece ->
            try {
                allTime.add(makeTimeViewString(
                    piece.value.start, piece.value.span, piece.value.col))

                if(!allLocation.contains(piece.value.location.toString())) allLocation.add(piece.value.location.toString())
            } catch (n : NullPointerException) {}
        }
        timeView.text = Joiner.on(", ").join(allTime)
        locationView.text = Joiner.on(", ").join(allLocation)

        // 내용 비어 있으면 텅 빈칸으로 남기지 말고 View 를 GONE 으로.
        if(cell.instructor == null || cell.instructor.isEmpty()) instructorView.visibility = View.GONE
        if(cell.location==null || cell.location.isEmpty()) locationView.visibility = View.GONE

        // 커스텀 강의이면 강의평, 강의계획서, 약칭 없애기
        if(cell.lecture_id==-1) {
            view.findViewById<LinearLayout>(R.id.cell_info_rating).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.cell_info_syllabus).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.cell_info_nick).visibility = View.GONE
        } // 서버에 있는 강의면 수업 정보 수정 없애기
        else {
            view.findViewById<LinearLayout>(R.id.cell_info_edit).visibility = View.GONE
        }


        view.findViewById<LinearLayout>(R.id.cell_info_rating).setOnClickListener {
            val intent = Intent(activity, LectureInfoActivity::class.java)
            intent.putExtra("id", cell.subject_professor)
            startActivity(intent)
            dismiss()
        }

        view.findViewById<LinearLayout>(R.id.cell_info_syllabus).setOnClickListener {
            val intent = Intent(activity, BrowseActivity::class.java)
            intent.putExtra("url", cell.url)
            intent.putExtra("title", "강의계획서 | 서울대학교 수강신청 시스템")
            startActivity(intent)
            dismiss()
        }

        view.findViewById<LinearLayout>(R.id.cell_info_memo).setOnClickListener {
            // 메모 추가 액티비티 실행
            val intent = Intent(activity, TableAddLectureMemoActivity::class.java)
            intent.putExtra("memo", cell.memo)
            resultListener.launch(intent)
            dismiss()
        }

        view.findViewById<LinearLayout>(R.id.cell_info_nick).setOnClickListener {
            // 약칭 지정 다이얼로그 열기
            val mBuilder = AlertDialog.Builder(activity)
                .setTitle("약칭 지정")
                .setView(R.layout.dialog_lecture_set_nickname)
                .setPositiveButton("확인", null)
                .setNegativeButton("취소", null)
                .setNeutralButton("초기화", null)
            val dialog = mBuilder.create()
            dialog.setOnShowListener(object : DialogInterface.OnShowListener {
                override fun onShow(p0: DialogInterface?) {
                    val positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    positive.setOnClickListener(object : View.OnClickListener {
                        // 확인 눌렀을 때
                        override fun onClick(p0: View?) {
                            val newNick = dialog.findViewById<EditText>(R.id.lecture_nickname_edit_text).text.toString()
                            deleteInterface.nick(newNick, memo=null)
                            dialog.dismiss()
                        }
                    })

                    val negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                    negative.setOnClickListener(object : View.OnClickListener {
                        // 취소 눌렀을 때
                        override fun onClick(p0: View?) {
                            dialog.dismiss()
                        }
                    })

                    val neutral =  dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
                    neutral.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(p0: View?) {
                            // 초기화 눌렀을 때
                            deleteInterface.nick("", null)
                            dialog.dismiss()
                        }
                    })
                }
            })
            dialog.show()
            dismiss()
        }

        view.findViewById<LinearLayout>(R.id.cell_info_edit).setOnClickListener {
            // 수정하는 액티비티 열게 하기
            deleteInterface.edit()
            dismiss()
        }

        view.findViewById<LinearLayout>(R.id.cell_info_delete).setOnClickListener {
            // 삭제 다이얼로그 열기
            val title = titleView.text.toString()
            val mBuilder = AlertDialog.Builder(activity)
                .setTitle("삭제")
                .setMessage("'${title}' 수업을 삭제하시겠습니까?")
                .setNegativeButton("취소") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton("삭제") { _, _ ->
                    deleteInterface.delete()
                }
            val dialog = mBuilder.create()
            dialog.show()
            dialog.findViewById<TextView>(android.R.id.message).textSize = 15f
            dismiss()
        }
    }

    interface DeleteCellInterface {
        fun delete()
        fun edit()
        fun nick(nickname : String?, memo : String?="")
        fun memo(memo : String="")
    }
    fun accessCell(deleteInterface : DeleteCellInterface) {
        this.deleteInterface = deleteInterface
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