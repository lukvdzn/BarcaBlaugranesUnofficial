package com.unoffical.barcablaugranes.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.unoffical.barcablaugranes.model.Comment
import com.unoffical.barcablaugranes.model.CommentResponse
import com.unoffical.barcablaugranes.model.buildCommentTree
import okhttp3.Headers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class PostPageRepository (private val url:String) {

    private val commentUrlTemplate = "https://www.barcablaugranes.com/comments/load_comments/"
    private lateinit var commentUrl: String
    val htmlContentLiveData = MutableLiveData<List<String>>()
    val commentsLiveData = MutableLiveData<List<Comment>>()

    fun getHtmlContentLiveData(): LiveData<List<String>> {
        updateHtmlContentLiveData()
        return htmlContentLiveData
    }

    fun getCommentLiveData() : LiveData<List<Comment>> {
        return commentsLiveData
    }

    private fun updateHtmlContentLiveData() {
        HttpRequest.getRequestWithCallback(url) { _, response ->
            val htmlBody = response.body()?.string()
            val document: Document = Jsoup.parse(htmlBody)
            val contentTag = "div.c-entry-content"

            // Fetch Comments Id for comments section
            val commentsIdTag: Element? = document.selectFirst("span.c-byline__gear")
                ?.selectFirst("a")
            val commentsId = commentsIdTag?.attr("data-entry-admin")

            // create comments url request for this post
            commentUrl = commentUrlTemplate + commentsId
            // Update comment live data
            updateCommentLiveData()

            val pTags = document.selectFirst(contentTag)
                ?.select("p")
                ?.map { it.text() } ?: emptyList<String>()

            // return html string for post content
            htmlContentLiveData.postValue(pTags)
        }
    }

    private fun updateCommentLiveData() {
        HttpRequest.getRequestWithCallback(commentUrl, headers = Headers
            .of(mutableMapOf("Accept" to "application/json",
                "Accept-Language" to "en-US"))) { _, response ->
            val json = response.body()?.string()
            val comments = Gson().fromJson(json, CommentResponse::class.java).comments
            commentsLiveData.postValue(buildCommentTree(comments))
        }
    }
}