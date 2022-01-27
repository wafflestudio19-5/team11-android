package com.example.toyproject.ui.review

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ActivityLectureInfoBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LectureInfoActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLectureInfoBinding
    private val viewModel: LectureInfoViewModel by viewModels()

    private lateinit var lectureReviewAdapter: LectureReviewAdapter
    private lateinit var lectureReviewLayoutManager: LinearLayoutManager


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLectureInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lectureReviewAdapter = LectureReviewAdapter()
        lectureReviewLayoutManager= LinearLayoutManager(this)

        binding.recyclerViewReviews.apply {
            adapter = lectureReviewAdapter
            layoutManager=lectureReviewLayoutManager
        }

        viewModel.getLectureInfo(intent.getIntExtra("lecture_professor_id", 10))
        viewModel.getReviews(intent.getIntExtra("id", 10))


        viewModel.result.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        viewModel.lectureInfo.observe(this) {
            binding.lectureName.text = it.subject_name
            binding.professorName.text = it.professor
            binding.lectureSemester.text = it.semester.joinToString(", ")
            val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, it.semester)
            binding.spinnerSemester.adapter = spinnerAdapter
            if(it.review==null){
                binding.reviewLayout.visibility = GONE
                binding.notFoundText1.visibility = VISIBLE
            }
            else{
                binding.ratingBar.rating = it.review.rating.toFloat()
                binding.ratingNum.text = it.review.rating.toString()
                binding.assignmentAvg.text = it.review.homework
                binding.teamAvg.text = it.review.team_activity
                binding.gradingAvg.text = it.review.grading
                binding.attendAvg.text = it.review.attendance
                binding.examAvg.text = it.review.test_count.toString()

            }
        }

        viewModel.reviewResult.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        viewModel.reviewList.observe(this) {
            lectureReviewAdapter.setReviews(it)
            Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
            lectureReviewAdapter.notifyDataSetChanged()
        }

        binding.writeReviewButton.setOnClickListener {
            binding.createReviewLayout.visibility = VISIBLE
        }

        binding.spinnerSemester.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        binding.postReviewButton.setOnClickListener {
            val selectedChip = binding.assignmentGroup.children
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).text.toString() }
        }

        binding.writeExamButton.setOnClickListener {
            binding.createInformationLayout.visibility = VISIBLE
        }
    }

    override fun onBackPressed() {
        finish()
    }
}