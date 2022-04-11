package com.sn.hackernewsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sn.hackernewsapp.databinding.ItemArticleCardviewBinding
import com.sn.hackernewsapp.models.Article
import com.sn.hackernewsapp.models.ArticleType


class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>()
{
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    private lateinit var binding: ItemArticleCardviewBinding
    lateinit var articleType: ArticleType
    val differ = AsyncListDiffer(this, differCallback)
    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        binding =
            ItemArticleCardviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int) = position

    companion object {
        val differCallback = object : DiffUtil.ItemCallback<Article>() {
            override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]

        holder.itemView.apply {
            articleType = if (article.url?.contains("item?id") == true){
                ArticleType.INTERNAL
            }else{
                ArticleType.EXTERNAL
            }
            binding.tvTitle.text = article.title
            binding.tvSource.text = article.source()
            binding.tvPublishedAt.text = article.time_ago
            setOnClickListener {
                onItemClickListener?.let { it(article) }
            }
        }
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}

