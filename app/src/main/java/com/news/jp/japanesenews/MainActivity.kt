package com.news.jp.japanesenews

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONArray
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class MainActivity : AppCompatActivity() {

    private val TAG = "MyActivity"
    private lateinit var newsViewModel: NewsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = NewsListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        newsViewModel = ViewModelProvider(this).get(NewsViewModel::class.java)
        newsViewModel.allNews.observe(this, Observer { news ->
            // Update the cached copy of the words in the adapter.
            news?.let { adapter.setNews(it) }
        })
        val ab: ActionBar? = supportActionBar
        ab?.title="News"

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            GlobalScope.launch { GetEasyNews()}
        }
    }



    override fun onStart() {
        super.onStart()

    }

    private fun GetEasyNews() {

        newsViewModel.deleteAll()
        val url = URL("https://www3.nhk.or.jp/news/easy/news-list.json")
        val con = url.openConnection() as HttpURLConnection
        val data = con.inputStream.bufferedReader().readText()
        val json = JSONArray(data)

        if(json.length() > 0)
        {
            val allNews = json.getJSONObject(0)
            allNews.keys().forEach {
                val newsPerDay = allNews.getJSONArray(it)
                for(i in 0 until newsPerDay.length()) {
                    val news = newsPerDay.getJSONObject(i)

                    val title = news.getString("title")
                    val id = news.getString("news_id")
                    val newsUrl = "https://www3.nhk.or.jp/news/easy/$id/$id.html"
                    val jsonFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss")
                    val zone: ZoneId = ZoneId.systemDefault()

                    val dateStringStart = news.getString("news_publication_time")
                    val startDateTime: OffsetDateTime = LocalDateTime.parse(dateStringStart, jsonFormatter).atOffset(
                        ZoneOffset.UTC)
                    val userStartDateTime: ZonedDateTime = startDateTime.atZoneSameInstant(zone)

                    val imageUrl : String = news.getString("news_web_image_uri")

                    val newsObject = News(newsUrl, title, Date.from(userStartDateTime.toInstant()), imageUrl)
                    newsViewModel.insert(newsObject)
                }
            }
        }
    }

    private fun extractNews(url :String)
    {
        Log.d(TAG, "test")
    }

}
