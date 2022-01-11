package com.example.toyproject.ui.board

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.databinding.ActivityBoardSearchBinding
import com.example.toyproject.network.dto.Board
import com.example.toyproject.ui.main.GeneralRecyclerViewAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BoardSearchActivity: AppCompatActivity() {

    private lateinit var binding: ActivityBoardSearchBinding
    private val viewModel: BoardSearchViewModel by viewModels()

    private lateinit var boardSearchAdapter: BoardSearchAdapter
    private lateinit var boardSearchLayoutManager: LinearLayoutManager

    private var searchKeyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardSearchAdapter = BoardSearchAdapter()
        boardSearchLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewCareerBoard.apply{
            adapter = boardSearchAdapter
            layoutManager = boardSearchLayoutManager
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        viewModel.noBoard.observe(this@BoardSearchActivity,{
            if(!it){
                binding.recyclerViewCareerBoard.visibility = View.VISIBLE
                binding.linearLayout.visibility = View.GONE
            } else{
                binding.recyclerViewCareerBoard.visibility = View.GONE
                binding.linearLayout.visibility = View.VISIBLE
                binding.textNoResult.text = "검색 결과가 없습니다"
            }
        })



        // "RESULT_OK" : 새로고침 (사용처: 게시판 생성 및 삭제 후)
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == RESULT_OK) {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        binding.makeBoardButton.setOnClickListener {
            val intent = Intent(this, BoardMakeActivity::class.java)
            resultListener.launch(intent)
        }

        binding.boardSearchBar.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchKeyword = binding.boardSearchBar.text.toString()
                viewModel.searchBoard(searchKeyword)

                viewModel.boards.observe(this@BoardSearchActivity, {
                    boardSearchAdapter.setBoards(it)
                })

            }

            override fun afterTextChanged(p0: Editable?) {}

        })



        boardSearchAdapter.setItemClickListener(object: BoardSearchAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Board, position: Int) {
                Intent(this@BoardSearchActivity, BoardActivity::class.java).apply{
                    putExtra("board_name", data.name)
                    putExtra("board_id", data.id)
                }.run{resultListener.launch(this)}
            }
        })




    }
}