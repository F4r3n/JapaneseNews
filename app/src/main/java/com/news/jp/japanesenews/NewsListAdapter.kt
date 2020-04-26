package com.news.jp.japanesenews

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class NewsListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<NewsListAdapter.NewsViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var news = emptyList<News>() // Cached copy of words

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val newsItemView : TextView = itemView.findViewById(R.id.textView)
        val imagePreview : ImageView = itemView.findViewById(R.id.Imagepreview)

        init {
            itemView.setOnClickListener(this);
        }

        override fun onClick(view: View?) {
            val position = layoutPosition // gets item position
            val intent = Intent(inflater.context, NewsActivity::class.java).apply {
                val pNews = NewsParcelable()
                pNews.from(news[position])
                putExtra("News", pNews)
            }
            inflater.context.startActivity(intent)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val current = news[position]
        holder.newsItemView.text = current.title
        Glide.with(inflater.context).load(current.imageUrl).into(holder.imagePreview);
    }

    internal fun setNews(news: List<News>) {
        this.news = news
        notifyDataSetChanged()
    }


    override fun getItemCount() = news.size
}