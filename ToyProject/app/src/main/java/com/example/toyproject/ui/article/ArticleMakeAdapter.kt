package com.example.toyproject.ui.article

import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.toyproject.databinding.ItemArticleUploadImageBinding
import kotlin.collections.ArrayList


data class ArticleImageInfo(
    val image: Uri?,
    val texts: String
)
class ArticleMakeAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var images: ArrayList<ArticleImageInfo> = arrayListOf()
    inner class ImageViewHolder(val binding: ItemArticleUploadImageBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemArticleUploadImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = images[position]
        when(holder){
            is ImageViewHolder -> {
                Glide.with(context).load(data.image).into(holder.binding.articleImage)
                holder.binding.articleDescription.text = data.texts
            }
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun setImages(images: ArrayList<ArticleImageInfo>){
        this.images = images
    }

}