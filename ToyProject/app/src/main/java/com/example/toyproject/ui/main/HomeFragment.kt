package com.example.toyproject.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.toyproject.R
import com.example.toyproject.databinding.FragmentHomeBinding
import com.example.toyproject.ui.profile.UserActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = binding.toolbar

        toolbar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.search_button->{
                    true
                }
                R.id.profile_button->{
                    val intent = Intent(activity, UserActivity::class.java)
                    startActivity(intent)
                    true
                }
                else->false
            }
        }
    }



}