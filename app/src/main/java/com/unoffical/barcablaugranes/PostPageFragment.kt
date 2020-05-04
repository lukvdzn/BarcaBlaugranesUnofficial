package com.unoffical.barcablaugranes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class PostPageFragment : Fragment(R.layout.fragment_post_page) {

    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progress_bar)

        doAsync {
            val content = getContentFromUrl(tag ?: "")
            uiThread {
                view.findViewById<TextView>(R.id.post_content_text_view).text = content
                destroyProgressBar()
            }
        }
    }

    private fun destroyProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun getContentFromUrl(url: String): String {
        val contentTag = "div.c-entry-content"
        val document: Document = Jsoup.connect(url).get()
        println(document.body())
        return document.selectFirst(contentTag)?.run { this.text() } ?: ""
    }
}
