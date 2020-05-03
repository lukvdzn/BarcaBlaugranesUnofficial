package com.unoffical.barcablaugranes

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class MainEntryPageFragment : Fragment(R.layout.fragment_main_entry_page) {

    private lateinit var jsoupParser: JsoupParser
    private lateinit var tableLayout: TableLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainUrl = getString(R.string.main_url)
        tableLayout = view.findViewById(R.id.main_entry_page_table_layout)
        jsoupParser = JsoupParser(mainUrl)

        doAsync {
            val list:List<MainFivePost> = jsoupParser.handleMainPage()
            uiThread {initialiseTableLayout(list) }
        }
    }

    private fun initialiseTableLayout(mainPostInfoList : List<MainFivePost>) {
        for (info in mainPostInfoList) {
            val tableRow: TableRow = createPostInfoTableRow(info, R.layout.main_five_card_view)
            tableLayout.addView(tableRow)
        }
    }

    private fun createPostInfoTableRow(postInfo: MainFivePost, @LayoutRes resourceId: Int): TableRow {
        // Create new row
        val tableRow = TableRow(context)
        // Inflate the corresponding view
        val tableElement =
            layoutInflater.inflate(resourceId, tableRow, false)

        // Update all views containing information about each team in the table
        val titleTextView = tableElement.findViewById<TextView>(R.id.title_post_text_view)
        val authorTextView =
            tableElement.findViewById<TextView>(R.id.author_post_text_view)
        val commentsTextView =
            tableElement.findViewById<TextView>(R.id.comments_post_text_view)
        titleTextView.text = postInfo.title
        authorTextView.text = "By ${postInfo.author}"
        commentsTextView.text = "${postInfo.commentsCount} Comments"

        tableElement
            .findViewById<ImageView>(R.id.img_preview_image_view)
            ?.let { postImageView -> {
                postInfo.linkToImage?.apply {
                    Picasso.get()?.let { it.load(this) }?.into(postImageView)
                }
            }
            }

        // Attach the link of the post to the card view so when clicking on it, we are able to
        // fetch the html document from the url link
        tableElement.tag = postInfo.linkToPost
        tableRow.addView(tableElement)
        return tableRow
    }

}