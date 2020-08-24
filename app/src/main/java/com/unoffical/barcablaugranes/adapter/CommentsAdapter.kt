package com.unoffical.barcablaugranes.adapter

import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.unoffical.barcablaugranes.R
import com.unoffical.barcablaugranes.model.Comment

class CommentsAdapter(private val comments: MutableList<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder>() {

    class CommentsViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int = comments.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.post_single_comment_item, parent, false)
        return CommentsViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentsViewHolder, position: Int) {
        val view = holder.view

        // set padding for comment indentation
        val scale: Float = holder.view.resources.displayMetrics.density
        val dp = 15 * (comments[position].depth - 1)
        view.setPadding((dp * scale + 0.5f).toInt(), 0, 0, 0)

        comments[position].apply {
            view.findViewById<TextView>(R.id.post_single_comment_username).text = this.username
            view.findViewById<TextView>(R.id.post_single_comment_created_on).text = this.createdOn
            // Hide content and title TextView if comment does not have either
            val contentTextView = view.findViewById<TextView>(R.id.post_single_comment_content)
            val titleTextView = view.findViewById<TextView>(R.id.post_single_comment_title)
            if(this.body == null) contentTextView.visibility = View.GONE
                else contentTextView.text = fromHtml(this.body)
            if(this.title == null) titleTextView.visibility = View.GONE
                else titleTextView.text = fromHtml(this.title)

            // attach current comment as a tag to the view
            view.tag = this
        }

        // When comment clicked, inflate children comments
        view.setOnClickListener {
            val parentComment = it.tag as Comment
            val currentPosition: Int = comments.indexOf(parentComment)
            val childrenComments = parentComment.children
            if(childrenComments.isNotEmpty()) {
                if(parentComment.expanded) {
                    for(child in childrenComments) {
                        comments.removeAt(currentPosition + 1)
                    }
                    parentComment.expanded = false
                    notifyItemRangeRemoved(currentPosition + 1, childrenComments.size)
                } else {
                    parentComment.expanded = true
                    comments.addAll(currentPosition + 1, childrenComments)
                    notifyItemRangeInserted(currentPosition + 1, childrenComments.size)
                }
            }
        }
    }

    private fun fromHtml(html: String) : Spanned {
        return try {
            Html.fromHtml(html, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
        } catch (e: Exception) {
            // Dummy
            SpannableString("")
        }
    }
}