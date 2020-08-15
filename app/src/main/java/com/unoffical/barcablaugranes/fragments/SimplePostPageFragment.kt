package com.unoffical.barcablaugranes.fragments

import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ProgressBar
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.unoffical.barcablaugranes.R
import com.unoffical.barcablaugranes.model.Comment
import com.unoffical.barcablaugranes.repository.PostPageRepository
import com.unoffical.barcablaugranes.viewmodels.PostPageViewModel
import com.unoffical.barcablaugranes.viewmodels.PostPageViewModelFactory
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.lang.Exception


class SimplePostPageFragment : Fragment(R.layout.fragment_simple_post_page) {

    companion object {
        val BUNDLE_TITLE = "BUNDLE_TITLE"
        val BUNDLE_URL = "BUNDLE_URL"
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var parentView: View
    private lateinit var tableLayout: TableLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progress_bar)
        parentView = view
        tableLayout = view.findViewById(R.id.post_page_table_layout)
        val title: String = requireArguments().get(BUNDLE_TITLE) as String
        val url: String = requireArguments().get(BUNDLE_URL) as String

        val postPageViewModel = ViewModelProviders.of(this, PostPageViewModelFactory(url))
            .get(PostPageViewModel::class.java)
        postPageViewModel.htmlContentLiveData.observe(viewLifecycleOwner, Observer {
            val contentHtml: Spanned = fromHtml(it)
            // set text view to post content
            view.findViewById<TextView>(R.id.post_title_text_view).text = title
            view.findViewById<TextView>(R.id.post_content_text_view).text = contentHtml
        })

        postPageViewModel.commentsLiveData.observe(viewLifecycleOwner, Observer {
            // insert comments into table rows
            initialiseTableLayout(it)
            // make "Comments" text visible
            view.findViewById<TextView>(R.id.comments_literal).visibility = View.VISIBLE
            destroyProgressBar()
        })

        /*
        doAsync {
            // Set up parser
            val postPageRepository = PostPageRepository.getInstance(tag ?: "")

            // Get Page Content
            val content: String = postPageRepository.getContentFromUrl()
            val html: Spanned = fromHtml(content)
            // Get Json Comments
            val comments: List<Comment> = postPageRepository.getCommentsFromJson()

            uiThread {
                // set text view to post content
                view.findViewById<TextView>(R.id.post_content_text_view).text = html
                // insert comments into table rows
                initialiseTableLayout(comments)
                // make "Comments" text visible
                view.findViewById<TextView>(R.id.comments_literal).visibility = View.VISIBLE
                destroyProgressBar()
            }
        } */
    }

    private fun fromHtml(html: String) : Spanned {
        return try {
            Html.fromHtml(html, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
        } catch (e: Exception) {
            // Dummy
            SpannableString("")
        }
    }

    private fun destroyProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun initialiseTableLayout(comments: List<Comment>) {
        for(comment in comments) {
            val tableRow = inflatePostCommentCardView(comment)
            tableLayout.addView(tableRow)
        }
    }

    private fun inflatePostCommentCardView(comment: Comment): TableRow {
        // Create new row
        val tableRow = TableRow(context)

        val tableElement = layoutInflater.inflate(R.layout.post_single_comment, tableRow,
            false)

        tableElement.findViewById<TextView>(R.id.post_single_comment_username).text = comment.username
        tableElement.findViewById<TextView>(R.id.post_single_comment_created_on).text = comment.createdOn
        tableElement.findViewById<TextView>(R.id.post_single_comment_content).text = fromHtml(comment.body)
        tableElement.findViewById<TextView>(R.id.post_single_comment_title).text = fromHtml(comment.title)

        tableRow.addView(tableElement)

        return tableRow
    }
}
