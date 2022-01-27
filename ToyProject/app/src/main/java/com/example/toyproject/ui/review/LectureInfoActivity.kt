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
import androidx.core.view.iterator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ActivityLectureInfoBinding
import com.example.toyproject.network.CreateReview
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LectureInfoActivity: AppCompatActivity() {
    private lateinit var binding: ActivityLectureInfoBinding
    private val viewModel: LectureInfoViewModel by viewModels()

    private lateinit var lectureReviewAdapter: LectureReviewAdapter
    private lateinit var lectureReviewLayoutManager: LinearLayoutManager

    private lateinit var informationAdapter: InformationAdapter
    private lateinit var informationLayoutManager: LinearLayoutManager

    @SuppressLint("NotifyDataSetChanged", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLectureInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lectureReviewAdapter = LectureReviewAdapter()
        lectureReviewLayoutManager= LinearLayoutManager(this)
        informationAdapter = InformationAdapter()
        informationLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewReviews.apply {
            adapter = lectureReviewAdapter
            layoutManager=lectureReviewLayoutManager
        }

        binding.recyclerViewExams.apply {
            adapter = informationAdapter
            layoutManager = informationLayoutManager
        }

        viewModel.getLectureInfo(intent.getIntExtra("lecture_professor_id", 1))
        viewModel.getReviews(intent.getIntExtra("id", 1))
        viewModel.getInformation(intent.getIntExtra("id", 1))


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
                binding.reviewLayout.visibility = VISIBLE
                binding.notFoundText1.visibility = GONE
                binding.ratingBar.rating = it.review.rating
                binding.ratingNum.text = it.review.rating.toString()
                binding.assignmentAvg.text = it.review.homework
                binding.teamAvg.text = it.review.team_activity
                binding.gradingAvg.text = it.review.grading
                binding.attendAvg.text = it.review.attendance
                binding.examAvg.text = it.review.test_count

            }
        }

        viewModel.reviewResult.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        viewModel.reviewList.observe(this) {
            lectureReviewAdapter.setReviews(it)
            lectureReviewAdapter.notifyDataSetChanged()
        }

        viewModel.informationResult.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        viewModel.informationList.observe(this){
            if(it.size==0){
                binding.notFoundText2.visibility= VISIBLE
            }
            else{
                binding.notFoundText2.visibility= GONE
            }
            informationAdapter.setInformation(it)
            informationAdapter.notifyDataSetChanged()
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
            val homework = binding.assignmentGroup.children
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).resources.getResourceName(it.id) }
                .toList().joinToString()
            val team = binding.teamGroup.children
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).resources.getResourceName(it.id) }
                .toList().joinToString()
            val grading = binding.gradingGroup.children
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).resources.getResourceName(it.id) }
                .toList().joinToString()
            val attendance = binding.attendanceGroup.children
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).resources.getResourceName(it.id) }
                .toList().joinToString()
            val exam = binding.examGroup.children
                .filter { (it as Chip).isChecked }
                .map { (it as Chip).resources.getResourceName(it.id) }
                .toList().joinToString()
            val year = binding.spinnerSemester.selectedItem.toString().substring(0,4).toInt()
            val seasonTemp = binding.spinnerSemester.selectedItem.toString().substring(6)
            var season = 1
            season = when (seasonTemp) {
                "1학기" -> {
                    1
                }
                "2학기" -> {
                    2
                }
                "여름학기" -> {
                    3
                }
                else -> {
                    4
                }
            }

            viewModel.postReview(intent.getIntExtra("id", 1),
                CreateReview(binding.ratingUser.rating.toInt(), getNumber(homework), getNumber(team), getNumber(grading), getNumber(attendance), getNumber(exam), binding.reviewText.text.toString(), year, season))
        }

        viewModel.postReviewResult.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        }

        viewModel.review.observe(this){
            binding.createReviewLayout.visibility = GONE
            viewModel.getLectureInfo(intent.getIntExtra("lecture_professor_id", 1))
            viewModel.getReviews(intent.getIntExtra("id", 1))
        }

        binding.writeExamButton.setOnClickListener {
            binding.createInformationLayout.visibility = VISIBLE
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun getNumber(Str: String): Int{
        return Str.substring(Str.indexOf("_")+1).toInt()
    }
}