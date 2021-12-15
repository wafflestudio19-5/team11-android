package com.example.toyproject

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityUnivCertifyBinding

class UnivCertifyActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUnivCertifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUnivCertifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val years = resources.getStringArray(R.array.years)
        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, years)
        binding.spinnerYear.adapter = spinnerAdapter

        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                //아이템이 클릭 되면 맨 위부터 position 0번부터 순서대로 동작하게 됩니다.
                when (position) {
                    0 -> {

                    }
                    1 -> {

                    }
                    //...
                    else -> {

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

}