package com.unoffical.barcablaugranes.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.unoffical.barcablaugranes.model.Comment
import com.unoffical.barcablaugranes.model.CommentResponse
import okhttp3.Headers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class PostPageRepository (private val url:String) {

    private val comment_url_template = "https://www.barcablaugranes.com/comments/load_comments/"
    private lateinit var commentUrl: String
    val htmlContentLiveData = MutableLiveData<String>()
    val commentsLiveData = MutableLiveData<List<Comment>>()

    fun getHtmlContentLiveData(): LiveData<String> {
        updateHtmlContentLiveData()
        return htmlContentLiveData
    }

    fun getCommentLiveData() : LiveData<List<Comment>> {
        return commentsLiveData
    }

    fun updateHtmlContentLiveData() {
        HttpRequest.getRequestWithCallback(url) { _, response ->
            val htmlBody = response.body()?.string()
            val document: Document = Jsoup.parse(htmlBody)
            val contentTag = "div.c-entry-content"

            // Fetch Comments Id for comments section
            val commentsIdTag: Element? = document.selectFirst("span.c-byline__gear")
                ?.selectFirst("a")
            val commentsId = commentsIdTag?.attr("data-entry-admin")

            // create comments url request for this post
            commentUrl = comment_url_template + commentsId
            // Update comment live data
            updateCommentLiveData()

            // return html string for post content
            htmlContentLiveData.postValue(document.selectFirst(contentTag)?.run { this.html() } ?: "")
        }
    }

    fun updateCommentLiveData() {
        HttpRequest.getRequestWithCallback(commentUrl, headers = Headers
            .of(mutableMapOf("Accept" to "application/json",
                "Accept-Language" to "en-US"))) { _, response ->
            val json = response.body()?.string()
            val comments = Gson().fromJson(json, CommentResponse::class.java).comments
            commentsLiveData.postValue(comments)
        }
    }
}