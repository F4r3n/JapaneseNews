package com.news.jp.japanesenews

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements



class NewsActivity : AppCompatActivity() {
    private val TAG = "MyActivity"

    private lateinit var articleViewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.DarkTheme);

        setContentView(R.layout.news_activity)

        articleViewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)

        val ab: ActionBar? = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true);
        val news = intent.extras?.getParcelable<NewsParcelable>("News")
        val textView = findViewById<TextView>(R.id.news_content)
        textView.setLineSpacing(0.0f,1.5f)
        if (news != null) {
            articleViewModel.setUrl(news.url)
            findViewById<TextView>(R.id.news_title).text = news.title
            Glide.with(this).load(news.imageUrl).into(findViewById<ImageView>(R.id.news_picture));

            articleViewModel.getContent().observe(this, Observer { newsContent ->
                findViewById<TextView>(R.id.news_content).text = newsContent

            })

        }
    }




}