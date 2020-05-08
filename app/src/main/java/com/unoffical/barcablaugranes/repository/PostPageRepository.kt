package com.unoffical.barcablaugranes.repository

import com.google.gson.Gson
import com.unoffical.barcablaugranes.model.Comment
import com.unoffical.barcablaugranes.model.CommentResponse
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class PostPageRepository(private val url:String, private val comment_url_template: String) {

    private lateinit var commentUrl: String

    fun getContentFromUrl(): String {
        val contentTag = "div.c-entry-content"
        val document: Document = Jsoup.connect(url).get()

        // Fetch Comments Id for comments section
        val commentsIdTag: Element? = document.selectFirst("span.c-byline__gear")
            ?.selectFirst("a")
        val commentsId = commentsIdTag?.attr("data-entry-admin")

        // create comments url request for this post
        commentUrl = comment_url_template + commentsId

        // return html string for post content
        return document.selectFirst(contentTag)?.run { this.html() } ?: ""
    }

    fun getCommentsFromJson(): List<Comment> {
        val json: String = Jsoup
            .connect(commentUrl)
            .ignoreContentType(true)
            .headers(mutableMapOf("Accept" to "application/json", "Accept-Language" to "en-US"))
            .execute()
            .body()

        return Gson().fromJson(json, CommentResponse::class.java).comments
    }
}