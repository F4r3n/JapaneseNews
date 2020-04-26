package com.news.jp.japanesenews

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.*


class NewsParcelable() : Parcelable {
    var url : String? = null
    var title : String? = null
    var date : Date = Date.from(Instant.now())
    var imageUrl : String? = null

    fun from(inNews : News)
    {
        url = inNews.url
        title = inNews.title
        date = inNews.date
        imageUrl = inNews.imageUrl
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
        dest.writeString(title)
        dest.writeLong(date.time)
        dest.writeString(imageUrl)
    }

    constructor(parcel: Parcel) : this() {
        url = parcel.readString()
        title = parcel.readString()
        date = Date(parcel.readLong())
        imageUrl = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewsParcelable> {
        override fun createFromParcel(parcel: Parcel): NewsParcelable {
            return NewsParcelable(parcel)
        }

        override fun newArray(size: Int): Array<NewsParcelable?> {
            return arrayOfNulls(size)
        }
    }
}


@Entity(tableName = "news_table")
data class News(val inUrl : String, val inTitle : String, val inDate: Date, val inImageURL : String = "") : Comparable<News> {
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
    var url : String = inUrl
    var title : String = inTitle
    var date : Date = inDate
    var imageUrl : String = inImageURL

    override fun compareTo(other: News): Int {
        return other.date!!.compareTo(this.date)
    }
}


@Dao
interface NewsDAO {

    @Query("Select * FROM news_table")
    fun getAllNews(): LiveData<List<News>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNews(news: News)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNews(news: List<News>)

    @Delete
    suspend fun deleteNews(news: News)

    @Query("Delete from news_table")
    suspend fun deleteAllNews()
}

class DateConverter {

    @TypeConverter
    fun toDate(dateLong : Long) : Date{
        return Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date : Date) : Long{
        return date.time
    }
}

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = arrayOf(News::class), version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
public abstract class NewsRoomDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NewsRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): NewsRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NewsRoomDatabase::class.java,
                    "news_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

    private class WordDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.newsDao())
                }
            }
        }

        suspend fun populateDatabase(newsDao: NewsDAO) {

        }
    }
}

