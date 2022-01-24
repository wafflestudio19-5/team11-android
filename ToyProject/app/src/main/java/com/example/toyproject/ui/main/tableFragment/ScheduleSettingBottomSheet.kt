package com.example.toyproject.ui.main.tableFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.example.toyproject.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ScheduleSettingBottomSheet : BottomSheetDialogFragment() {

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
            // 시간표 삭제
        }
    }



}
