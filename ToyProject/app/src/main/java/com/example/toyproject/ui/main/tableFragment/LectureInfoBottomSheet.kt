package com.example.toyproject.ui.main.tableFragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.example.toyproject.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.w3c.dom.Text

class LectureInfoBottomSheet : BottomSheetDialogFragment() {

    private lateinit var deleteInterface : DeleteCellInterface

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
        val cell : Cell = arguments!!.getParcelable("cellInfo")!!

        // 뷰 내용 채우기
        titleView.text = cell.title
        instructorView.text = cell.instructor

        timeView.text = arguments!!.getString("time")
        locationView.text = cell.location

        // 내용 비어 있으면 텅 빈칸으로 남기지 말고 View 를 GONE 으로.
        if(cell.instructor.isEmpty()) instructorView.visibility = View.GONE
        if(cell.location.isEmpty()) locationView.visibility = View.GONE

        view.findViewById<LinearLayout>(R.id.cell_info_rating).setOnClickListener {
//            TODO : 여기서 강의평 LectureInfoActivity 로 이동 (필요한 정보 putExtra 로 넣어서 챙겨가기)
//            val intent = Intent(activity, LectureInfoActivity::class.java)
//            startActivity(intent)
        }

        view.findViewById<LinearLayout>(R.id.cell_info_syllabus).setOnClickListener {
            // TODO : 강의계획서로 이동 , webView 사용할것
        }

        view.findViewById<LinearLayout>(R.id.cell_info_memo).setOnClickListener {
            // TODO : 메모 추가하는 액티비티 열기
        }

        view.findViewById<LinearLayout>(R.id.cell_info_nick).setOnClickListener {
            // dismiss()
            // TODO : 약칭 지정하는 다이얼로그 열기
            val intent = Intent()
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
    }
    fun deleteCell(deleteInterface : DeleteCellInterface) {
        this.deleteInterface = deleteInterface
    }

}