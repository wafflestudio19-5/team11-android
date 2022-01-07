package com.example.toyproject.ui.univsearch

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityUnivSearchBinding
import com.example.toyproject.network.dto.GetUniversity
import com.example.toyproject.network.dto.University
import com.example.toyproject.ui.signup.SignupActivity
import com.example.toyproject.ui.signup.SocialSignupActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class UnivSearchActivity  : AppCompatActivity() {
    private lateinit var binding: ActivityUnivSearchBinding
    private val viewModel : UnivSearchViewModel by viewModels()

    lateinit var email : String
    lateinit var access_token : String
    lateinit var socialType : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.getStringExtra("email") != null) {
            email = intent.getStringExtra("email").toString()
        }
        if(intent.getStringExtra("access_token") != null) {
            access_token = intent.getStringExtra("access_token").toString()
        }
        if(intent.getStringExtra("socialType") != null) {
            socialType = intent.getStringExtra("socialType").toString()
        }

        binding = ActivityUnivSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel
        // viewModel 에서 list 받아와서 adapter 에 넣는 부분
        val univList : List<University> = viewModel.loadUnivList().university_list
        val nameList = mutableListOf<String>()
        for ((idx, name) in univList) {
            nameList.add(idx-1, name)
        }
        val autoCompleteTextView = findViewById<AutoCompleteTextView>(binding.autoCompleteTextView.id)
        val adapter = ArrayAdapter<String>(this@UnivSearchActivity, android.R.layout.simple_dropdown_item_1line, nameList)
        autoCompleteTextView.setAdapter(adapter)

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

        // signup 이 완료되지 않았으면 뒤로가기 버튼으로 다시 이 화면으로 돌아올 수 있고, signup 이 끝날 때 이 액티비티를 종료
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == RESULT_OK) {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        binding.button.setOnClickListener {
            when {
                binding.spinnerYear.selectedItem == "연도 선택(학번)" -> {
                    Toast.makeText(this, "연도를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                }
                binding.autoCompleteTextView.text.toString() !in nameList -> {
                    Toast.makeText(this, "학교를 올바르게 입력해 주세요.", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // 소셜 로그인일때는 다음 버튼을 누르면 SocialSignupActivity 로 이동
                    if(intent.getStringExtra("mode")=="social") {
                        val intent = Intent(this, SocialSignupActivity::class.java)
                        intent.putExtra("admission_year", binding.spinnerYear.selectedItem.toString().toInt())
                        intent.putExtra("university", binding.autoCompleteTextView.text.toString())
                        intent.putExtra("email", email)
                        intent.putExtra("access_token", access_token)
                        intent.putExtra("socialType", socialType)
                        resultListener.launch(intent)
                    }
                    // 일반 로그인일때는 다음 버튼을 누르면 SignupActivity 로 이동
                    else {
                        val intent = Intent(this, SignupActivity::class.java)
                        intent.putExtra("admission_year", binding.spinnerYear.selectedItem.toString().toInt())
                        intent.putExtra("university", binding.autoCompleteTextView.text.toString())
                        resultListener.launch(intent)
                    }

                }
            }

        }
    }

}