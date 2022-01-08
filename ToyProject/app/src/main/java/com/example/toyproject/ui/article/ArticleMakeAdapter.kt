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



class ArticleMakeAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var images: ArrayList<Uri?> = arrayListOf()
    private var texts: ArrayList<String> = arrayListOf()
    inner class ImageViewHolder(
        val binding: ItemArticleUploadImageBinding, myCustomEditTextListener: MyCustomEditTextListener): RecyclerView.ViewHolder(binding.root){
        var mEditText: EditText
        var myCustomEditTextListener: MyCustomEditTextListener

        init {
            mEditText = binding.articleDescription
            this.myCustomEditTextListener = myCustomEditTextListener
            mEditText.addTextChangedListener(myCustomEditTextListener)
        }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemArticleUploadImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding, MyCustomEditTextListener())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = images[position]
        when(holder){
            is ImageViewHolder -> {
                Glide.with(context).load(data).into(holder.binding.articleImage)
                if(texts.size>0 && texts[position] !=""){
                    holder.myCustomEditTextListener.updatePosition(holder.adapterPosition)
                    holder.binding.articleDescription.setText(holder.adapterPosition)
                    //holder.binding.articleDescription.addTextChangedListener(MemoTextWatcher(position))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun setImages(images: ArrayList<Uri?>){
        this.images = images
    }

    fun returnTexts(): ArrayList<String>{
        return texts
    }

    inner class MyCustomEditTextListener : TextWatcher {
        private var position = 0
        fun updatePosition(position: Int) {
            this.position = position
        }

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            texts[position] = ""
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            texts[position] = charSequence.toString()
        }

        override fun afterTextChanged(editable: Editable) {
            // no op
        }
    }
}