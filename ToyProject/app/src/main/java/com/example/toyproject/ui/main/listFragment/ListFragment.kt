package com.example.toyproject.ui.main.listFragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.R
import com.example.toyproject.databinding.FragmentListBinding
import com.example.toyproject.network.dto.Board
import com.example.toyproject.ui.board.BoardActivity
import com.example.toyproject.ui.board.BoardSearchActivity
import com.example.toyproject.ui.board.HotBestBoardActivity
import com.example.toyproject.ui.board.MyArticleBoardActivity
import com.example.toyproject.ui.review.ReviewActivity
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

        // "RESULT_OK" : ???????????? (?????????: ????????? ?????? ??? ?????? ???)
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == AppCompatActivity.RESULT_OK) {
                    binding.scroll.scrollTo(0, binding.scroll.top)
                    viewModel.getBoardList()
                }
            }

        binding.myArticleView.setOnClickListener{
            Intent(activity, MyArticleBoardActivity::class.java).apply{
                putExtra("board_name", "?????? ??? ???")
                putExtra("board_interest", "article")
            }.run{startActivity(this)}
        }

        binding.myCommentView.setOnClickListener{
            Intent(activity, MyArticleBoardActivity::class.java).apply{
                putExtra("board_name", "?????? ??? ???")
                putExtra("board_interest", "comment")
            }.run{startActivity(this)}
        }

        binding.myScrapView.setOnClickListener{
            Intent(activity, MyArticleBoardActivity::class.java).apply{
                putExtra("board_name", "?????????")
                putExtra("board_interest", "scrap")
            }.run{startActivity(this)}
        }

        binding.hotBoardView.setOnClickListener{
            Intent(activity, HotBestBoardActivity::class.java).apply{
                putExtra("board_name", "HOT ?????????")
                putExtra("board_interest", "hot")
            }.run{startActivity(this)}
        }

        binding.bestBoardView.setOnClickListener {
            Intent(activity, HotBestBoardActivity::class.java).apply {
                putExtra("board_name", "BEST ?????????")
                putExtra("board_interest", "best")
            }.run{resultListener.launch(this)}
        }

        binding.lectureReviewView.setOnClickListener {
            Intent(activity, ReviewActivity::class.java).apply {

            }.run{resultListener.launch(this)}
        }



        // ????????? ?????? ??? ????????? ???????????? ????????? ??? ?????????, ???????????? listener ??????
        binding.searchOtherBoard.setOnClickListener{
            val intent = Intent(activity, BoardSearchActivity::class.java)
            resultListener.launch(intent)
        }

        binding.refreshLayout.setOnRefreshListener {
            Handler(Looper.getMainLooper()).postDelayed({
                generalAdapter.resetBoards()
                promotionAdapter.resetBoards()
                organizationAdapter.resetBoards()
                departmentAdapter.resetBoards()
                careerAdapter.resetBoards()
                defaultAdapter.resetBoards()
                viewModel.getBoardList() },
                100)
            binding.refreshLayout.isRefreshing = false
        }

        binding.refreshLayout.setColorSchemeResources(R.color.PrimaryVariant)


        viewModel.getBoardList()

        viewModel.defaultBoardList.observe(viewLifecycleOwner, {
            defaultAdapter.setDefaultBoards(it)
        })

        viewModel.careerBoardList.observe(viewLifecycleOwner, {
            careerAdapter.setBoards(it)
        })

        viewModel.promotionBoardList.observe(viewLifecycleOwner, {
            promotionAdapter.setBoards(it)
        })

        viewModel.organizationBoardList.observe(viewLifecycleOwner, {
            organizationAdapter.setBoards(it)
        })

        viewModel.departmentBoardList.observe(viewLifecycleOwner, {
            departmentAdapter.setBoards(it)
        })

        viewModel.generalBoardList.observe(viewLifecycleOwner, {
            generalAdapter.setBoards(it)
        })

        // ???????????? ?????? ???????????? ?????? ??????????????? ????????? ??????, ??? ????????? ???????????? ????????? ?????? ????????? ???????????? listener ??????
        generalAdapter.setItemClickListener(object: GeneralRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: Board, position: Int) {
                Intent(activity, BoardActivity::class.java).apply{
                    putExtra("board_name", data.name)
                    putExtra("board_id", data.id)
                }.run{resultListener.launch(this)}

            }

            override fun pinClick(v: View, data: Board, position: Int, favorite: Boolean) {
                if(favorite) {
                    Toast.makeText(activity, "??????????????? ????????????.",Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(activity, "??????????????? ????????????.",Toast.LENGTH_SHORT).show()
                }
                viewModel.putFavoriteBoard(data.id)
                //??? ??? ?????? ?????????
                Handler(Looper.getMainLooper()).postDelayed({
                    generalAdapter.resetBoards()
                    promotionAdapter.resetBoards()
                    organizationAdapter.resetBoards()
                    departmentAdapter.resetBoards()
                    careerAdapter.resetBoards()
                    defaultAdapter.resetBoards()
                    viewModel.getBoardList() },
                    100)
            }
        })

        defaultAdapter.setItemClickListener(object: DefaultRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: Board, position: Int) {
                Intent(activity, BoardActivity::class.java).apply{
                    putExtra("board_name", data.name)
                    putExtra("board_id", data.id)
                }.run{startActivity(this)}
            }

            override fun pinClick(v: View, data: Board, position: Int, favorite: Boolean) {
                if(favorite) {
                    Toast.makeText(activity, "??????????????? ????????????.",Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(activity, "??????????????? ????????????.",Toast.LENGTH_SHORT).show()
                }
                viewModel.putFavoriteBoard(data.id)
                //??? ??? ?????? ?????????
                Handler(Looper.getMainLooper()).postDelayed({
                    generalAdapter.resetBoards()
                    promotionAdapter.resetBoards()
                    organizationAdapter.resetBoards()
                    departmentAdapter.resetBoards()
                    careerAdapter.resetBoards()
                    defaultAdapter.resetBoards()
                    viewModel.getBoardList() },
                    100)
            }
        })

        careerAdapter.setItemClickListener(object: CareerRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: Board, position: Int) {
                Intent(activity, BoardActivity::class.java).apply{
                    putExtra("board_name", data.name)
                    putExtra("board_id", data.id)
                }.run{startActivity(this)}
            }

            override fun pinClick(v: View, data: Board, position: Int, favorite: Boolean) {
                if(favorite) {
                    Toast.makeText(activity, "??????????????? ????????????.",Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(activity, "??????????????? ????????????.",Toast.LENGTH_SHORT).show()
                }
                viewModel.putFavoriteBoard(data.id)
                //??? ??? ?????? ?????????
                Handler(Looper.getMainLooper()).postDelayed({
                    generalAdapter.resetBoards()
                    promotionAdapter.resetBoards()
                    organizationAdapter.resetBoards()
                    departmentAdapter.resetBoards()
                    careerAdapter.resetBoards()
                    defaultAdapter.resetBoards()
                    viewModel.getBoardList() },
                    100)
            }
        })

        promotionAdapter.setItemClickListener(object:
            PromotionRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: Board, position: Int) {
                Intent(activity, BoardActivity::class.java).apply{
                    putExtra("board_name", data.name)
                    putExtra("board_id", data.id)
                }.run{startActivity(this)}
            }

            override fun pinClick(v: View, data: Board, position: Int, favorite: Boolean) {
                if(favorite) {
                    Toast.makeText(activity, "??????????????? ????????????.",Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(activity, "??????????????? ????????????.",Toast.LENGTH_SHORT).show()
                }
                viewModel.putFavoriteBoard(data.id)
                //??? ??? ?????? ?????????
                Handler(Looper.getMainLooper()).postDelayed({
                    generalAdapter.resetBoards()
                    promotionAdapter.resetBoards()
                    organizationAdapter.resetBoards()
                    departmentAdapter.resetBoards()
                    careerAdapter.resetBoards()
                    defaultAdapter.resetBoards()
                    viewModel.getBoardList() },
                    100)
            }
        })

        organizationAdapter.setItemClickListener(object:
            OrganizationRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: Board, position: Int) {
                Intent(activity, BoardActivity::class.java).apply{
                    putExtra("board_name", data.name)
                    putExtra("board_id", data.id)
                }.run{startActivity(this)}
            }

            override fun pinClick(v: View, data: Board, position: Int, favorite: Boolean) {
                if(favorite) {
                    Toast.makeText(activity, "??????????????? ????????????.",Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(activity, "??????????????? ????????????.",Toast.LENGTH_SHORT).show()
                }
                viewModel.putFavoriteBoard(data.id)
                //??? ??? ?????? ?????????
                Handler(Looper.getMainLooper()).postDelayed({
                    generalAdapter.resetBoards()
                    promotionAdapter.resetBoards()
                    organizationAdapter.resetBoards()
                    departmentAdapter.resetBoards()
                    careerAdapter.resetBoards()
                    defaultAdapter.resetBoards()
                    viewModel.getBoardList() },
                    100)
            }
        })

        departmentAdapter.setItemClickListener(object:
            DepartmentRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(v: View, data: Board, position: Int) {
                Intent(activity, BoardActivity::class.java).apply{
                    putExtra("board_name", data.name)
                    putExtra("board_id", data.id)
                }.run{startActivity(this)}
            }

            override fun pinClick(v: View, data: Board, position: Int, favorite: Boolean) {
                if(favorite) {
                    Toast.makeText(activity, "??????????????? ????????????.",Toast.LENGTH_SHORT).show()
                } else{
                    Toast.makeText(activity, "??????????????? ????????????.",Toast.LENGTH_SHORT).show()
                }
                viewModel.putFavoriteBoard(data.id)
                //??? ??? ?????? ?????????
                Handler(Looper.getMainLooper()).postDelayed({
                    generalAdapter.resetBoards()
                    promotionAdapter.resetBoards()
                    organizationAdapter.resetBoards()
                    departmentAdapter.resetBoards()
                    careerAdapter.resetBoards()
                    defaultAdapter.resetBoards()
                    viewModel.getBoardList() },
                    100)
            }
        })



    }

}