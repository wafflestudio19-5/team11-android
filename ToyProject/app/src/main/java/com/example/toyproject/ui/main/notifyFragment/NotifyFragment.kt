package com.example.toyproject.ui.main.notifyFragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.R
import com.example.toyproject.databinding.FragmentNotifyBinding
import com.example.toyproject.network.dto.Notification
import com.example.toyproject.ui.article.ArticleActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotifyFragment : Fragment() {

    private lateinit var binding: FragmentNotifyBinding
    private val viewModel: NotifyViewModel by activityViewModels()

    private lateinit var notifyAdapter: NotifyRecyclerViewAdapter
    private lateinit var notifyLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotifyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notifyAdapter = NotifyRecyclerViewAdapter()
        notifyLayoutManager = LinearLayoutManager(activity)

        binding.recyclerViewNotification.apply{
            adapter = notifyAdapter
            layoutManager = notifyLayoutManager
        }

        binding.refreshLayout.setOnRefreshListener {
            notifyAdapter.resetNotification()
            viewModel.getNotificationList()
            binding.refreshLayout.isRefreshing = false
        }

        binding.refreshLayout.setColorSchemeResources(R.color.PrimaryVariant)

        viewModel.getNotificationList()

        viewModel.notificationList.observe(viewLifecycleOwner, {
            notifyAdapter.setNotification(it)
        })

        notifyAdapter.setItemClickListener(object: NotifyRecyclerViewAdapter.OnItemClickListener{
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onItemClick(v: View, data: Notification, position: Int) {
                activity?.let { v.setBackgroundColor(it.getColor(R.color.Background)) }
                viewModel.readNotification(data.id)
                Intent(activity, ArticleActivity::class.java).apply{
                    putExtra("board_name", data.board_name)
                    putExtra("board_id", data.board_id)
                    putExtra("article_id", data.article_id)
                }.run{startActivity(this)}
            }
        })
    }

    override fun onResume(){
        super.onResume()

        notifyAdapter = NotifyRecyclerViewAdapter()
        notifyLayoutManager = LinearLayoutManager(activity)

        binding.recyclerViewNotification.apply{
            adapter = notifyAdapter
            layoutManager = notifyLayoutManager
        }

        binding.refreshLayout.setOnRefreshListener {
            notifyAdapter.resetNotification()
            viewModel.getNotificationList()
            binding.refreshLayout.isRefreshing = false
        }

        binding.refreshLayout.setColorSchemeResources(R.color.PrimaryVariant)

        viewModel.getNotificationList()

        viewModel.notificationList.observe(viewLifecycleOwner, {
            notifyAdapter.setNotification(it)
        })

        notifyAdapter.setItemClickListener(object: NotifyRecyclerViewAdapter.OnItemClickListener{
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onItemClick(v: View, data: Notification, position: Int) {
                activity?.let { v.setBackgroundColor(it.getColor(R.color.Background)) }
                viewModel.readNotification(data.id)
                Intent(activity, ArticleActivity::class.java).apply{
                    putExtra("board_name", data.board_name)
                    putExtra("board_id", data.board_id)
                    putExtra("article_id", data.article_id)
                }.run{startActivity(this)}
            }
        })
    }

}