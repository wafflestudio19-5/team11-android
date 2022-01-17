package com.example.toyproject.ui.main.tableFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.toyproject.databinding.FragmentTableBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TableFragment : Fragment() {

    private lateinit var binding: FragmentTableBinding

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

        // 우상단 시간표 목룍 버튼
        binding.tableButtonList.setOnClickListener {
            val intent = Intent(activity, TableListActivity::class.java)
            resultListener.launch(intent)
        }
        binding.tableButtonSetting.setOnClickListener {

        }
        binding.tableButtonAddClass.setOnClickListener {

        }
    }

}