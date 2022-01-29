package com.example.toyproject.ui.main.noteFragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.R
import com.example.toyproject.databinding.FragmentNoteBinding
import com.example.toyproject.network.dto.MessageRoom
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding
    private val viewModel: NoteFragmentViewModel by activityViewModels()

    private lateinit var noteAdapter: NoteFragmentRecyclerViewAdapter
    private lateinit var  noteLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        noteAdapter = NoteFragmentRecyclerViewAdapter()
        noteLayoutManager = LinearLayoutManager(activity)

        binding.recyclerViewMessageRoom.apply{
            adapter = noteAdapter
            layoutManager = noteLayoutManager
        }

        binding.refreshLayout.setOnRefreshListener {
            noteAdapter.resetMessageRoom()
            viewModel.getMessageRoomList()
            binding.refreshLayout.isRefreshing = false
        }

        binding.refreshLayout.setColorSchemeResources(R.color.PrimaryVariant)

        viewModel.getMessageRoomList()

        viewModel.messageRoomList.observe(viewLifecycleOwner,{
            noteAdapter.setMessageRoom(it)
        })

        noteAdapter.setItemClickListener(object: NoteFragmentRecyclerViewAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: MessageRoom, position: Int) {
                Intent(activity, NoteActivity::class.java).apply{
                    putExtra("message_room_id", data.id)
                }.run{startActivity(this)}
            }
        })

    }

    override fun onStart(){
        super.onStart()

        viewModel.getMessageRoomList()
    }

    override fun onStop(){
        super.onStop()
        viewModel.getMessageRoomList()
    }

}