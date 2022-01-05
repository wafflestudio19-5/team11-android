package com.example.toyproject.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.databinding.FragmentListBinding
import com.example.toyproject.network.dto.Board
import com.example.toyproject.ui.board.BoardActivity
import com.example.toyproject.ui.board.BoardSearchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private val viewModel: ListViewModel by activityViewModels()

    private lateinit var defaultAdapter: DefaultRecyclerViewAdapter
    private lateinit var defaultLayoutManager: LinearLayoutManager

    private lateinit var careerAdapter: CareerRecyclerViewAdapter
    private lateinit var careerLayoutManager: LinearLayoutManager

    private lateinit var promotionAdapter: PromotionRecyclerViewAdapter
    private lateinit var promotionLayoutManager: LinearLayoutManager

    private lateinit var organizationAdapter: OrganizationRecyclerViewAdapter
    private lateinit var organizationLayoutManager: LinearLayoutManager

    private lateinit var departmentAdapter: DepartmentRecyclerViewAdapter
    private lateinit var departmentLayoutManager: LinearLayoutManager

    private lateinit var generalAdapter: GeneralRecyclerViewAdapter
    private lateinit var generalLayoutManager : LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        defaultAdapter = DefaultRecyclerViewAdapter()
        defaultLayoutManager = LinearLayoutManager(activity)

        careerAdapter = CareerRecyclerViewAdapter()
        careerLayoutManager = LinearLayoutManager(activity)

        promotionAdapter = PromotionRecyclerViewAdapter()
        promotionLayoutManager = LinearLayoutManager(activity)

        organizationAdapter = OrganizationRecyclerViewAdapter()
        organizationLayoutManager = LinearLayoutManager(activity)

        departmentAdapter = DepartmentRecyclerViewAdapter()
        departmentLayoutManager = LinearLayoutManager(activity)

        generalAdapter = GeneralRecyclerViewAdapter()
        generalLayoutManager = LinearLayoutManager(activity)



        binding.recyclerViewDefaultBoard.apply{
            adapter = defaultAdapter
            layoutManager = defaultLayoutManager
        }

        binding.recyclerViewCareerBoard.apply{
            adapter = careerAdapter
            layoutManager = careerLayoutManager
        }

        binding.recyclerViewPromotionBoard.apply{
            adapter = promotionAdapter
            layoutManager = promotionLayoutManager
        }

        binding.recyclerViewOrganizationBoard.apply{
            adapter = organizationAdapter
            layoutManager = organizationLayoutManager
        }

        binding.recyclerViewDepartmentBoard.apply{
            adapter = departmentAdapter
            layoutManager = departmentLayoutManager
        }

        binding.recyclerViewGeneralBoard.apply{
            adapter = generalAdapter
            layoutManager = generalLayoutManager
        }


        binding.searchOtherBoard.setOnClickListener{
            val intent = Intent(activity, BoardSearchActivity::class.java)
            startActivity(intent)
        }


        viewModel.getBoardList()

        viewModel.defaultBoardList.observe(viewLifecycleOwner, {
            defaultAdapter.setDefaultBoards(it)
        })

        viewModel.careerBoardList.observe(viewLifecycleOwner, {
            careerAdapter.setDefaultBoards(it)
        })

        viewModel.promotionBoardList.observe(viewLifecycleOwner, {
            promotionAdapter.setDefaultBoards(it)
        })

        viewModel.organizationBoardList.observe(viewLifecycleOwner, {
            organizationAdapter.setDefaultBoards(it)
        })

        viewModel.departmentBoardList.observe(viewLifecycleOwner, {
            departmentAdapter.setDefaultBoards(it)
        })

        viewModel.generalBoardList.observe(viewLifecycleOwner, {
            generalAdapter.setDefaultBoards(it)
        })

        generalAdapter.setItemClickListener(object: GeneralRecyclerViewAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Board, position: Int) {
                Intent(activity, BoardActivity::class.java).apply{
                    putExtra("board_name", data.name)
                    putExtra("board_id", data.id)
                }.run{startActivity(this)}

            }
        })

        defaultAdapter.setItemClickListener(object: DefaultRecyclerViewAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Board, position: Int) {
                Intent(activity, BoardActivity::class.java).apply{
                    putExtra("board_name", data.name)
                    putExtra("board_id", data.id)
                }.run{startActivity(this)}
            }
        })



    }

}