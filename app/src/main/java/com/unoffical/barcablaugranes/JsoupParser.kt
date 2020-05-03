package com.unoffical.barcablaugranes

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class JsoupParser(private val mainUrl:String) {

    private val mainFiveElementTag: String = "div.c-entry-box--compact.c-entry-box--compact--article.c-five-up__entry.c-entry-box--compact--hero"
    private val containerBoxTag: String = "div.c-entry-box--compact__body"
    private val commentsCountTag: String = "span[data-ui='comment-data']"
    private val authorTag: String = "a[data-analytics-link='author-name']"
    private val titleTag: String = "a[data-analytics-link='article']"
    private val alternativeContent: String = ""


    fun handleMainPage(): List<MainFivePost> {
        val document:Document = Jsoup.connect(mainUrl).get();
        val mainFive: Elements = document.select(mainFiveElementTag)
        val list: MutableList<MainFivePost> = mutableListOf()

        for(elem: Element in mainFive) {
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
}