package com.example.toyproject.ui.main.noteFragment

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityNoteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteBinding
    private val viewModel: NoteViewModel by viewModels()

    private lateinit var noteAdapter: NoteRecyclerViewAdapter
    private lateinit var noteLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteAdapter = NoteRecyclerViewAdapter()
        noteLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewMessage.apply{
            adapter = noteAdapter
            layoutManager = noteLayoutManager
        }

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_hold_fade_out)

        viewModel.talker.observe(this, {
            binding.messagePerson.text = it
        })

        binding.backArrow.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_left)

        }

        binding.articleFullMoreButton.setOnClickListener {
            val array = arrayOf("새로고침", "전체 삭제", "차단", "신고")
            val builder = AlertDialog.Builder(this)
            builder.setItems(array) { a, which ->
                val selected = array[which]
                // TODO (다른 선택지들)
                when (selected) {
                    "전체 삭제" -> {
                        val mBuilder = AlertDialog.Builder(this@NoteActivity)
                            .setMessage("모든 쪽지를 삭제하시겠습니까?")
                            .setNegativeButton("취소") { dialogInterface, i ->
                                dialogInterface.dismiss()
                            }
                            .setPositiveButton("삭제") { dialogInterface, i ->
                                noteAdapter.resetMessages()
                                finish()
                            }
                        val dialog = mBuilder.create()
                        dialog.findViewById<TextView>(android.R.id.message)?.textSize = 13f
                        dialog.show()
                    }
                    "새로고침" -> {
                        noteAdapter.resetMessages()
                        viewModel.getMessageList(intent.getIntExtra("message_room_id", 0))
                    }
                }
                Toast.makeText(this@NoteActivity, selected, Toast.LENGTH_SHORT).show()
            }
            val dialog = builder.create()
            dialog.show()
        }

        binding.refreshLayout.setOnRefreshListener {
            noteAdapter.resetMessages()
            viewModel.getMessageList(intent.getIntExtra("message_room_id", 0))
            binding.refreshLayout.isRefreshing = false
        }

        binding.refreshLayout.setColorSchemeResources(R.color.PrimaryVariant)

        viewModel.getMessageList(intent.getIntExtra("message_room_id", 0))

        viewModel.messageList.observe(this,{
            noteAdapter.setMessage(it)
        })

        binding.sendIcon.setOnClickListener {
            Intent(this@NoteActivity, NoteMakeActivity::class.java).apply{
                putExtra("message_room_id", intent.getIntExtra("message_room_id", 0))
            }.run{startActivity(this)}
        }



    }

    override fun onResume(){
        super.onResume()
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        noteAdapter = NoteRecyclerViewAdapter()
        noteLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewMessage.apply{
            adapter = noteAdapter
            layoutManager = noteLayoutManager
        }

        //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_hold_fade_out)

        viewModel.talker.observe(this, {
            binding.messagePerson.text = it
        })

        binding.backArrow.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_left)

        }

        binding.articleFullMoreButton.setOnClickListener {
            val array = arrayOf("새로고침", "전체 삭제", "차단", "신고")
            val builder = AlertDialog.Builder(this)
            builder.setItems(array) { a, which ->
                val selected = array[which]
                // TODO (다른 선택지들)
                when (selected) {
                    "전체 삭제" -> {
                        val mBuilder = AlertDialog.Builder(this@NoteActivity)
                            .setMessage("모든 쪽지를 삭제하시겠습니까?")
                            .setNegativeButton("취소") { dialogInterface, i ->
                                dialogInterface.dismiss()
                            }
                            .setPositiveButton("삭제") { dialogInterface, i ->
                                noteAdapter.resetMessages()
                                finish()
                            }
                        val dialog = mBuilder.create()
                        dialog.findViewById<TextView>(android.R.id.message)?.textSize = 13f
                        dialog.show()
                    }
                    "새로고침" -> {
                        noteAdapter.resetMessages()
                        viewModel.getMessageList(intent.getIntExtra("message_room_id", 0))
                    }
                }
                Toast.makeText(this@NoteActivity, selected, Toast.LENGTH_SHORT).show()
            }
            val dialog = builder.create()
            dialog.show()
        }

        binding.refreshLayout.setOnRefreshListener {
            noteAdapter.resetMessages()
            viewModel.getMessageList(intent.getIntExtra("message_room_id", 0))
            binding.refreshLayout.isRefreshing = false
        }

        binding.refreshLayout.setColorSchemeResources(R.color.PrimaryVariant)

        viewModel.getMessageList(intent.getIntExtra("message_room_id", 0))

        viewModel.messageList.observe(this,{
            noteAdapter.setMessage(it)
        })

        binding.sendIcon.setOnClickListener {
            Intent(this@NoteActivity, NoteMakeActivity::class.java).apply{
                putExtra("message_room_id", intent.getIntExtra("message_room_id", 0))
            }.run{startActivity(this)}
        }
    }

}