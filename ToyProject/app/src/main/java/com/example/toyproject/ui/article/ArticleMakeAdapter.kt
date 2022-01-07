package com.example.toyproject.ui.article

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.toyproject.R
import com.example.toyproject.databinding.ItemArticleUploadImageBinding
import com.example.toyproject.network.dto.ArticleImage
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class ArticleMakeAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var images: ArrayList<Uri?> = arrayListOf()
    inner class ImageViewHolder(val binding: ItemArticleUploadImageBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemArticleUploadImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = images[position]
        when(holder){
            is ImageViewHolder -> {
                /*
                val credentials: BasicAWSCredentials
                val key = holder.itemView.context.getString(R.string.AWS_ACCESS_KEY_ID)
                val secret = holder.itemView.context.getString(R.string.AWS_SECRET_ACCESS_KEY)
                val objectKey = data.image.substring(52)
                credentials = BasicAWSCredentials(key, secret)
                val s3 = AmazonS3Client(
                    credentials, Region.getRegion(
                        Regions.AP_NORTHEAST_2
                    )
                )
                val expires = Date(Date().getTime() + 1000 * 60) // 1 minute to expire
                val generatePresignedUrlRequest =
                    GeneratePresignedUrlRequest("team11bucket", objectKey) //generating the signatured url
                generatePresignedUrlRequest.expiration = expires
                val url: URL = s3.generatePresignedUrl(generatePresignedUrlRequest)
                holder.apply {
                    Glide.with(context)
                        .setDefaultRequestOptions(
                            RequestOptions()
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .placeholder(R.drawable.anonymous_photo)
                                .fitCenter()
                        )
                        .load(url.toString())
                        .into(itemView.findViewById(R.id.article_image))
                }
                //holder.binding.articleDescription.text = data.description
                 */
                Glide.with(context).load(data).into(holder.binding.articleImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    fun setImages(images: ArrayList<Uri?>){
        this.images = images
    }
}