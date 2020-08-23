package com.unoffical.barcablaugranes.adapter

import android.content.Context
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.unoffical.barcablaugranes.R
import com.unoffical.barcablaugranes.model.Comment
import java.lang.Exception

class CommentExpandableAdapter(private val context:Context,
                               private val list: List<Comment>): BaseExpandableListAdapter() {

    override fun getChild(groupPosition: Int,
                          childPosition: Int): Any = list[groupPosition].children[childPosition]

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val comment:Comment = getChild(groupPosition, childPosition) as Comment
        val view: View = if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.post_single_comment_item, null)
        } else convertView
        view.findViewById<TextView>(R.id.post_single_comment_username).text = comment.username
        view.findViewById<TextView>(R.id.post_single_comment_created_on).text = comment.createdOn
        view.findViewById<TextView>(R.id.post_single_comment_content).text = fromHtml(comment.body)
        view.findViewById<TextView>(R.id.post_single_comment_title).text = fromHtml(comment.title)
        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int = list[groupPosition].children.size

    override fun getGroup(groupPosition: Int): Any = list[groupPosition]

    override fun getGroupCount(): Int = list.size

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val comment:Comment = getGroup(groupPosition) as Comment
        val view: View = if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            inflater.inflate(R.layout.post_single_comment_group, null)
        } else convertView
        view.findViewById<TextView>(R.id.post_single_comment_username).text = comment.username
        view.findViewById<TextView>(R.id.post_single_comment_created_on).text = comment.createdOn
        view.findViewById<TextView>(R.id.post_single_comment_content).text = fromHtml(comment.body)
        view.findViewById<TextView>(R.id.post_single_comment_title).text = fromHtml(comment.title)
        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun hasStableIds(): Boolean = true


    private fun fromHtml(html: String) : Spanned {
        return try {
            Html.fromHtml(html, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH)
        } catch (e: Exception) {
            // Dummy
            SpannableString("")
        }
    }
}