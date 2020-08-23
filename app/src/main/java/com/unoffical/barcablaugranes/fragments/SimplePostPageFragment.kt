package com.unoffical.barcablaugranes.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unoffical.barcablaugranes.R
import com.unoffical.barcablaugranes.adapter.CommentsAdapter
import com.unoffical.barcablaugranes.viewmodels.PostPageViewModel
import com.unoffical.barcablaugranes.viewmodels.PostPageViewModelFactory


class SimplePostPageFragment : Fragment(R.layout.fragment_post_page) {

    companion object {
        const val BUNDLE_TITLE = "BUNDLE_TITLE"
        const val BUNDLE_URL = "BUNDLE_URL"
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var parentView: View
    private lateinit var pContentTableLayout: TableLayout
    private lateinit var mCommentsRecyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: CommentsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar = view.findViewById(R.id.progress_bar)
        parentView = view
        pContentTableLayout = view.findViewById(R.id.post_p_content_table_layout)

        val title: String = requireArguments().get(BUNDLE_TITLE) as String
        val url: String = requireArguments().get(BUNDLE_URL) as String

        val postPageViewModel = ViewModelProviders.of(this, PostPageViewModelFactory(url))
            .get(PostPageViewModel::class.java)
        postPageViewModel.htmlContentLiveData.observe(viewLifecycleOwner, Observer {
            // set text view to post content
            view.findViewById<TextView>(R.id.post_title_text_view).text = title
            addPContentToTableLayout(it)
        })

        postPageViewModel.commentsLiveData.observe(viewLifecycleOwner, Observer {
            viewManager = LinearLayoutManager(context)
            viewAdapter = CommentsAdapter(it.toMutableList())
            mCommentsRecyclerView = view.findViewById<RecyclerView>(
                R.id.fragment_post_page_recyclerview).apply {
                layoutManager = viewManager
                adapter = viewAdapter
            }
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

    private fun destroyProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun addPContentToTableLayout(pList: List<String>) {
        for(p in pList) {
            val tableRow = TableRow(context)
            val tableElement = layoutInflater.inflate(R.layout.p_text_view, tableRow, false)
            tableElement.findViewById<TextView>(R.id.p_text_view).text = p
            tableRow.addView(tableElement)
            pContentTableLayout.addView(tableRow)
        }
    }
}
