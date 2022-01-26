package com.example.toyproject.ui.review

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityLectureInfoBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.WithFragmentBindings

@AndroidEntryPoint
class LectureInfoActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLectureInfoBinding
    private val viewModel: LectureInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLectureInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getLectureInfo(2)

        viewModel.result.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        viewModel.lectureInfo.observe(this) {
            binding.lectureName.text = it.subject_name
            binding.professorName.text = it.professor
            binding.lectureSemester.text = it.semester.joinToString(", ")
            if(it.review==null){
                binding.reviewLayout.visibility = GONE
                binding.notFoundText1.visibility = VISIBLE
            }
            else{
                binding.ratingBar.rating = it.review.rating.toFloat()
                binding.assignmentAvg.text = it.review.homework
                binding.teamAvg.text = it.review.team_activity
                binding.gradingAvg.text = it.review.grading
                binding.attendAvg.text = it.review.attendance
                binding.examAvg.text = it.review.test_count.toString()

            }
        }
        binding.writeReviewButton.setOnClickListener {
            binding.createReviewLayout.visibility = VISIBLE
        }

        binding.writeExamButton.setOnClickListener {
            binding.createInformationLayout.visibility = VISIBLE
        }
    }
}