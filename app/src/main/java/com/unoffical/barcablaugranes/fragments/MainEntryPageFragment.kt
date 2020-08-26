package com.unoffical.barcablaugranes.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.squareup.picasso.Picasso
import com.unoffical.barcablaugranes.R
import com.unoffical.barcablaugranes.model.PostCategory
import com.unoffical.barcablaugranes.viewmodels.MainEntryPageViewModel


class MainEntryPageFragment : Fragment(R.layout.fragment_main_entry_page) {

    private lateinit var tableLayout: TableLayout
    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialise main views
        tableLayout = view.findViewById(R.id.main_entry_page_table_layout)
        progressBar = view.findViewById(R.id.progress_bar)
        // Create View Model
        val mainEntryPageViewModel = ViewModelProviders.of(this).get(MainEntryPageViewModel::class.java)

        mainEntryPageViewModel.mMainEntryPostElements.observe(viewLifecycleOwner, Observer {
            destroyProgressBar()
            initialiseTableLayout(it)
        })
    }

    private fun destroyProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun initialiseTableLayout(mainPostInfoList : List<PostCategory>) {
        for (info in mainPostInfoList) {
            val tableRow: TableRow = createPostInfoTableRow(info)
            tableLayout.addView(tableRow)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createPostInfoTableRow(postInfo: PostCategory): TableRow {
        // Create new row
        val tableRow = TableRow(context)

        val tableElement: View = when(postInfo) {
            is PostCategory.MainFivePost -> {
                // Inflate the main five post card view
                val tableElement = layoutInflater.inflate(R.layout.main_five_card_view, tableRow,
                    false)

                // Add image from link into Image View
                val imageView: ImageView = tableElement.findViewById(R.id.img_preview_image_view)
                postInfo.linkToImage?.apply {
                    Picasso.get().load(this).into(imageView)
                }

                tableElement
            }

            is PostCategory.LatestStoryPost -> {
                // Inflate the latest stories card view
                val tableElement = layoutInflater.inflate(R.layout.latest_story_card_view, tableRow,
                    false)

                // Add date and summary of post
                tableElement.findViewById<TextView>(R.id.date_post_text_view).text = postInfo.date
                tableElement.findViewById<TextView>(R.id.summary_post_text_view).text = postInfo.description

                tableElement
            }
        }

        // Set all other views accordingly
        val titleTextView = tableElement.findViewById<TextView>(R.id.title_post_text_view)
        val authorTextView = tableElement.findViewById<TextView>(R.id.author_post_text_view)
        val commentsTextView = tableElement.findViewById<TextView>(R.id.comments_post_text_view)
        titleTextView?.text = postInfo.title
        authorTextView?.text = "By ${postInfo.author}"
        commentsTextView?.text = "${postInfo.commentsCount} Comments"

        // Attach a bundle with link url
        val bundle = Bundle()
        bundle.putString(SimplePostPageFragment.BUNDLE_URL, postInfo.linkToPost)
        tableElement.tag = bundle

        tableRow.addView(tableElement)
        tableElement.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                val postPageFragment = SimplePostPageFragment()
                // attach bundle to fragment
                postPageFragment.arguments = it.tag as Bundle

                this.replace(R.id.fragment_container, postPageFragment)
                this.addToBackStack(null)
                commit()
            }
        }
        return tableRow
    }
}