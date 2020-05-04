package com.unoffical.barcablaugranes

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainEntryPageFragment : Fragment(R.layout.fragment_main_entry_page) {

    private lateinit var jsoupParser: JsoupParser
    private lateinit var tableLayout: TableLayout
    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainUrl = getString(R.string.main_url)
        tableLayout = view.findViewById(R.id.main_entry_page_table_layout)
        progressBar = view.findViewById(R.id.progress_bar)
        jsoupParser = JsoupParser(mainUrl)


        doAsync {
            val list:List<PostCategory> = jsoupParser.handleMainPage()
            uiThread {
                destroyProgressBar()
                initialiseTableLayout(list)
            }
        }
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
        var tableElement: View? = null

        when(postInfo) {
            is MainFivePost -> {
                // Inflate the corresponding view
                tableElement =
                    layoutInflater.inflate(R.layout.main_five_card_view, tableRow, false)

                val imageView: ImageView = tableElement.findViewById(R.id.img_preview_image_view)
                postInfo.linkToImage?.apply {
                    Picasso.get().load(this).into(imageView)
                }
            }

            is LatestStoryPost -> {
                tableElement =
                    layoutInflater.inflate(R.layout.latest_story_card_view, tableRow, false)
                val dateTextView: TextView = tableElement.findViewById(R.id.date_post_text_view)
                val summaryTextView: TextView = tableElement.findViewById(R.id.summary_post_text_view)
                dateTextView.text = postInfo.date
                summaryTextView.text = postInfo.description
            }
        }

        val titleTextView = tableElement?.findViewById<TextView>(R.id.title_post_text_view)
        val authorTextView =
            tableElement?.findViewById<TextView>(R.id.author_post_text_view)
        val commentsTextView =
            tableElement?.findViewById<TextView>(R.id.comments_post_text_view)
        titleTextView?.text = postInfo.title
        authorTextView?.text = "By ${postInfo.author}"
        commentsTextView?.text = "${postInfo.commentsCount} Comments"

        // Attach the link of the post to the card view
        tableElement?.tag = postInfo.linkToPost
        tableRow.addView(tableElement)
        tableElement?.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.fragment_container,
                PostPageFragment(), it.tag as String).commit()
        }
        return tableRow
    }
}