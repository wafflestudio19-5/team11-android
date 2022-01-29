package com.example.toyproject.ui.main.tableFragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.toyproject.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ScheduleSettingBottomSheet : BottomSheetDialogFragment() {

    private lateinit var bridge : ScheduleSettingInterface

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_table_schedule_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scheduleId = arguments!!.getInt("id")
        val title= arguments!!.getString("title")

        view.findViewById<LinearLayout>(R.id.schedule_setting_change_name).setOnClickListener {
            // 시간표 이름 설정
        }

        view.findViewById<LinearLayout>(R.id.schedule_setting_change_scope).setOnClickListener {
            // 시간표 공개 범위 설정
        }

        view.findViewById<LinearLayout>(R.id.schedule_setting_theme).setOnClickListener {
            // 시간표 테마 설정
        }

        view.findViewById<LinearLayout>(R.id.schedule_setting_widget).setOnClickListener {
            // 시간표 위젯 설정
        }

        view.findViewById<LinearLayout>(R.id.schedule_setting_screenshot).setOnClickListener {
            // 시간표 스크린샷
        }

        view.findViewById<LinearLayout>(R.id.schedule_setting_url).setOnClickListener {
            // 시간표 공유
        }

        view.findViewById<LinearLayout>(R.id.schedule_setting_kakao).setOnClickListener {
            // 카카오톡 공유
        }

        view.findViewById<LinearLayout>(R.id.schedule_setting_delete).setOnClickListener {
            val dialog = AlertDialog.Builder(activity)
                .setTitle("삭제")
                .setMessage("'$title' 시간표를 삭제하시겠습니까?")
                .setNegativeButton("취소") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton("삭제") { _, _ ->
                    bridge.delete(scheduleId)
                }
                .create()
            dialog.show()
            dismiss()
        }
    }

    interface ScheduleSettingInterface {
        fun delete(scheduleId : Int)
    }

    fun setInterface(bridge : ScheduleSettingInterface) {
        this.bridge = bridge
    }



}
