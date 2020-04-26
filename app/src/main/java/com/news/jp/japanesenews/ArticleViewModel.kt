package com.news.jp.japanesenews

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.jsoup.select.Elements


class ArticleViewModel
    : ViewModel() {
    private val TAG = "MyActivity"
    private var url : String = ""
    private var spannableStringContent = MutableLiveData<SpannableStringBuilder>()

    fun setUrl(inURL : String?) {
        if (inURL != null) {
            url = inURL
        }
    }

    fun getContent() : LiveData<SpannableStringBuilder> {

        if(spannableStringContent.value.isNullOrEmpty()) {
            val spannableBuilder = SpannableStringBuilder()
            this.viewModelScope.launch(Dispatchers.IO) {
                val elements = getNewsElements(url)

                format(elements, spannableBuilder)
                Log.d(TAG, spannableBuilder.toString())
                spannableStringContent.postValue(spannableBuilder)
            }
        }

        return spannableStringContent
    }

    private fun formatNode(node : Node, spannableBuilder: SpannableStringBuilder) {
        when(node) {
            is TextNode -> {
                spannableBuilder.append(node.text())
            }
            is Element -> {
                if(node.tag().name == "ruby") {
                    handleRubyElement(node, spannableBuilder)
                }
                else {
                    formatElement(node, spannableBuilder)
                }
            }
        }
    }

    private fun formatElement(element: Element, spannableBuilder: SpannableStringBuilder) {
        element.childNodes().forEach {child-> formatNode(child, spannableBuilder)}
        when(element.tag().name) {
            "a" -> {

            }
        }
    }

    private fun handleRubyElement(node : Node, spannableBuilder: SpannableStringBuilder) {
        val previousTextPos = spannableBuilder.length
        var afterTextPos = spannableBuilder.length
        node.childNodes().forEach { child ->
            when(child) {
                is TextNode -> {
                    spannableBuilder.append(child.text())
                    Log.d(TAG, child.text())
                    afterTextPos = spannableBuilder.length
                }
                is Element -> {
                    if(child.tag().name == "rt") {
                        val span = RubySpan(child.text(), RubySpan.VISIBILE.VISIBLE)
                        spannableBuilder.setSpan(span, previousTextPos, afterTextPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        Log.d(TAG, child.text() + " " + previousTextPos + " " + afterTextPos)
                    }
                }
            }

        }
        Log.d(TAG, "--------------------------------")
    }

    private fun format(elements : Elements, spannableBuilder: SpannableStringBuilder) {
        val ps = elements.select("p")
        for( p in ps) {
            formatElement(p, spannableBuilder)
            spannableBuilder.append("\n\n")
        }


    }


    private fun largeLog(tag: String, content: String, limit :Int = 1024) {
        if (content.length > limit) {
            Log.d(tag, content.substring(0, limit))
            largeLog(tag, content.substring(limit), limit)
        } else {
            Log.d(tag, content)
        }
    }

    suspend private fun getNewsElements(url : String?) : Elements
    {

        val websiteDoc = Jsoup.connect(url)
            .header("Accept-Encoding", "gzip, deflate")
            .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
            .maxBodySize(0)
            .get()
        val elements = websiteDoc.select("div.article-main__body")

        return elements
    }
}