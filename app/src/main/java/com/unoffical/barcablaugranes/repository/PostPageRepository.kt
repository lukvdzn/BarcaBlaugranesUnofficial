package com.unoffical.barcablaugranes.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.unoffical.barcablaugranes.model.Comment
import com.unoffical.barcablaugranes.model.CommentResponse
import com.unoffical.barcablaugranes.model.PostDetails
import com.unoffical.barcablaugranes.model.buildCommentTree
import okhttp3.Headers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class PostPageRepository (private val url:String) {

    private val commentUrlTemplate = "https://www.barcablaugranes.com/comments/load_comments/"
    private lateinit var commentUrl: String
    private val postLiveData = MutableLiveData<PostDetails>()
    private val commentsLiveData = MutableLiveData<List<Comment>>()

    fun getPostLiveData(): LiveData<PostDetails> {
        updateHtmlContentLiveData()
        return postLiveData
    }

    fun getCommentLiveData() : LiveData<List<Comment>> {
        return commentsLiveData
    }

    private fun updateHtmlContentLiveData() {
        HttpRequest.getRequestWithCallback(url) { _, response ->
            val contentCss = "div.c-entry-content"
            val titleCss = "h1.c-page-title"
            val subTitleCss = "p.c-entry-summary"
            val authorCss = "span.c-byline__author-name"
            val dateCss = "time.c-byline__item"
            val pictureCss = "picture.c-picture"
            val imagePublisherCss = "span.e-image__meta"

            val postDetails: PostDetails = response.body()?.string()?.run {
                val document: Document = Jsoup.parse(this)
                // Fetch Comments Id for comments section
                val commentsIdTag: Element? = document.selectFirst("span.c-byline__gear")
                    ?.selectFirst("a")
                val commentsId = commentsIdTag?.attr("data-entry-admin")
                // create comments url request for this post
                commentUrl = commentUrlTemplate + commentsId
                // Update comment live data
                updateCommentLiveData()

                val pTags = document.selectFirst(contentCss)
                    ?.select("p")
                    ?.map {
                        val temp = it.html().replace("<br>", "$$$")
                        Jsoup.parse(temp).body().text().replace("$$$", "\n")}
                    ?: emptyList()

                // retrieve first image link Example of Image Link
                /*Example of Image source set, second with 620 pixels width is needed
                * <img srcset="https://cdn.vox-cdn.com/thumbor/LbV3ozyROrMaK9nrx0NBBfbv-xg=/0x0:4768x3179/320x213/filters:focal(2088x459:2850x1221)/cdn.vox-cdn.com/uploads/chorus_image/image/67290508/1228109962.jpg.0.jpg 320w, https://cdn.vox-cdn.com/thumbor/h31gkw9xMFexcAwnrZ0IHtGbzAY=/0x0:4768x3179/620x413/filters:focal(2088x459:2850x1221)/cdn.vox-cdn.com/uploads/chorus_image/image/67290508/1228109962.jpg.0.jpg 620w, https://cdn.vox-cdn.com/thumbor/V60t-DgKYetdURHhgKBjMewQfT4=/0x0:4768x3179/920x613/filters:focal(2088x459:2850x1221)/cdn.vox-cdn.com/uploads/chorus_image/image/67290508/1228109962.jpg.0.jpg 920w, https://cdn.vox-cdn.com/thumbor/k_WDq09SVFRzwAk3pj4nuKmae_w=/0x0:4768x3179/1220x813/filters:focal(2088x459:2850x1221)/cdn.vox-cdn.com/uploads/chorus_image/image/67290508/1228109962.jpg.0.jpg 1220w, https://cdn.vox-cdn.com/thumbor/Cu2_BGnpqEi-UMAO7VXrD2QhbWo=/0x0:4768x3179/1520x1013/filters:focal(2088x459:2850x1221)/cdn.vox-cdn.com/uploads/chorus_image/image/67290508/1228109962.jpg.0.jpg 1520w, https://cdn.vox-cdn.com/thumbor/q0ffqGvYbkIFLv4Tx5GUTanuW2I=/0x0:4768x3179/1820x1213/filters:focal(2088x459:2850x1221)/cdn.vox-cdn.com/uploads/chorus_image/image/67290508/1228109962.jpg.0.jpg 1820w, https://cdn.vox-cdn.com/thumbor/DHRhrYBTykqPtKf0rARqEPCAmnE=/0x0:4768x3179/2120x1413/filters:focal(2088x459:2850x1221)/cdn.vox-cdn.com/uploads/chorus_image/image/67290508/1228109962.jpg.0.jpg 2120w, https://cdn.vox-cdn.com/thumbor/GX7LL9dDKc5UAIuVJtkSVx-Bhbc=/0x0:4768x3179/2420x1613/filters:focal(2088x459:2850x1221)/cdn.vox-cdn.com/uploads/chorus_image/image/67290508/1228109962.jpg.0.jpg 2420w" sizes="(min-width: 1221px) 846px, (min-width: 880px) calc(100vw - 334px), 100vw" alt="Barcelona FC presents Ronald Koeman as new head coach" data-upload-width="4768" src="https://cdn.vox-cdn.com/thumbor/aHcrVDQ_dawYTr3J65wySrH_2KA=/0x0:4768x3179/1200x800/filters:focal(2088x459:2850x1221)/cdn.vox-cdn.com/uploads/chorus_image/image/67290508/1228109962.jpg.0.jpg">
                * */
                val imageLink: String = document.selectFirst(pictureCss)
                    ?.selectFirst("img")
                    ?.attr("srcset")
                    ?.dropWhile { char -> char != ',' } // drop first image link with 320 width pixels
                    ?.drop(2) // drop comma and next space character
                    ?.takeWhile { char -> char != ' ' } // take link
                    ?: ""

                val imagePublisher: String = document.selectFirst(imagePublisherCss)?.text() ?: ""

                val data: List<String> = listOf(titleCss, subTitleCss, authorCss, dateCss)
                    .map { document.selectFirst(it)?.text() ?: "" }
                PostDetails(data[0], data[1], data[2], data[3], imageLink, imagePublisher, pTags)
            } ?: PostDetails()

            // return html string for post content
            postLiveData.postValue(postDetails)
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