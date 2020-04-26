package com.news.jp.japanesenews

import androidx.lifecycle.LiveData

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class NewsRepository(private val newsDAO: NewsDAO) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allNews: LiveData<List<News>> = newsDAO.getAllNews()

    suspend fun insert(news: News) {
        newsDAO.addNews(news)
    }

    suspend fun deleteAll() {
        newsDAO.deleteAllNews()
    }
}