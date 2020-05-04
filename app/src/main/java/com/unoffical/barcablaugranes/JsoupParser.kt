package com.unoffical.barcablaugranes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class JsoupParser(private val mainUrl: String) {

    fun handleMainPage(): List<PostCategory> {
        val document: Document = Jsoup.connect(mainUrl).get();
        return getMainFivePostList(document) + getLatestStoryPostList(document)
    }

    private fun getMainFivePostList(document: Document): List<PostCategory> {
        val mainFiveElementTag =
            "div.c-entry-box--compact.c-entry-box--compact--article.c-five-up__entry.c-entry-box--compact--hero"
        val containerBoxTag = "div.c-entry-box--compact__body"
        val commentsCountTag = "span[data-ui='comment-data']"
        val authorTag = "a[data-analytics-link='author-name']"
        val titleTag = "a[data-analytics-link='article']"
        val alternativeContent = ""

        val mainFive: Elements = document.select(mainFiveElementTag)
        val list: MutableList<PostCategory> = mutableListOf()

        for (elem: Element in mainFive) {
            var linkToImage: String? = null
            var linkToPost: String? = null
            var title: String = alternativeContent
            var author: String = alternativeContent
            var commentsCount: String = alternativeContent

            // Post Image Link
            elem.selectFirst("img")?.attr("src")?.let { linkToImage = it }

            // The container in which the title the a href link and the picture is found
            elem.selectFirst(containerBoxTag)?.apply {
                this.selectFirst(commentsCountTag)?.text()?.let { commentsCount = it }
                this.selectFirst(authorTag)?.text()?.let { author = it }
                this.selectFirst(titleTag)?.attr("href")?.let { linkToPost = it }
                this.selectFirst(titleTag)?.text()?.let { title = it }
            }
            list.add(MainFivePost(title, author, commentsCount, linkToPost, linkToImage))
        }
        return list
    }

    private fun getLatestStoryPostList(document: Document): List<PostCategory> {
        val latestStoryBoxesTag = "div.c-compact-river"
        val latestStoryBoxTag = "div.c-entry-box--compact.c-entry-box--compact--article"
        val linkDataTag = "a[data-analytics-link='article']"
        val timeStampTag = "time.c-byline__item"
        val authorTag = "a[data-analytics-link='author-name']"
        val commentsCountTag = "span[data-ui='comment-data']"
        val descriptionTag = "p.p-dek.c-entry-box--compact__dek"
        val alternativeContent = ""

        val list: MutableList<PostCategory> = mutableListOf()

        val boxes: Elements = document.select(latestStoryBoxesTag)

        for (box: Element in boxes) {
            val latestStories: Elements = box.select(latestStoryBoxTag)

            for (latestStory: Element in latestStories) {
                var linkToPost: String? = null
                var title: String = alternativeContent
                var author: String = alternativeContent
                var commentsCount: String = alternativeContent
                var time: String = alternativeContent
                var summary: String = alternativeContent

                val linkData: Element? = latestStory.selectFirst(linkDataTag)
                linkData?.let {
                    title = it.text()
                    linkToPost = it.attr("href")
                }

                latestStory.selectFirst(timeStampTag)?.let { time = it.text() }
                latestStory.selectFirst(authorTag)?.let { author = it.text() }
                latestStory.selectFirst(commentsCountTag)?.let { commentsCount = it.text() }
                latestStory.selectFirst(descriptionTag)?.let { summary = it.text() }

                list.add(LatestStoryPost(title, author, commentsCount, linkToPost, summary, time))
            }
        }

        return list
    }
}