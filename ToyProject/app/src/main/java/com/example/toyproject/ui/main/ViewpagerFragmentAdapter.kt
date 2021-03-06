package com.example.toyproject.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.toyproject.ui.main.homeFragment.HomeFragment
import com.example.toyproject.ui.main.listFragment.ListFragment
import com.example.toyproject.ui.main.notifyFragment.NotifyFragment
import com.example.toyproject.ui.main.noteFragment.NoteFragment
import com.example.toyproject.ui.main.tableFragment.TableFragment

class ViewpagerFragmentAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val fragmentList = listOf<Fragment>(HomeFragment(), TableFragment(), ListFragment(), NotifyFragment(), NoteFragment())

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

}