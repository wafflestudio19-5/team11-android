package com.example.toyproject.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.toyproject.databinding.FragmentNotifyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotifyFragment : Fragment() {

    private lateinit var binding: FragmentNotifyBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotifyBinding.inflate(inflater, container, false)
        return binding.root
    }

}