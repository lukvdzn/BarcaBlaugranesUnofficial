package com.unoffical.barcablaugranes.model

import com.google.gson.annotations.SerializedName

data class Comment(@SerializedName(value = "id") val id: String = "",
                   @SerializedName(value = "parent_id") val parentId: String = "",
                   @SerializedName(value = "body") val body: String = "",
                   @SerializedName(value = "title") val title: String = "",
                   @SerializedName(value = "username") val username: String = "",
                   @SerializedName(value = "created_on_short") val createdOn: String = "",
                   @SerializedName(value = "ancestry") val ancestry: String = "",
                   @SerializedName(value = "depth") val depth: Int = 0)

data class CommentResponse(@SerializedName(value = "success") val success: Boolean = false,
                           @SerializedName(value = "comments") val comments: List<Comment> = emptyList())

/**
Example Json Response (abbreviated):
{
    "success": true,
    "comments": [
    {
        "id": 527254868,
        "parent_id": 527254748,
        "version": 1,
        "user_id": 2134123,
        "spam_flags_count": 0,
        "troll_flags_count": 0,
        "inappropriate_flags_count": 0,
        "recommended_flags_count": 0,
        "hidden": false,
        "body": "<p> Example </p>",
        "title": "Example Title",
        "signature": "",
        "username": "Joe Mama",
        "entry_id": 21010259,
        "created_on_long": "May  5, 2020 |  7:47 AM",
        "created_on_short": "05.05.20  7:47am",
        "created_on_timestamp": 1588657637,
        "bad_flags_count": 0,
        "permalink": "-",
        "ancestry": "0527254748/0527254868",
        "depth": 2
    }
    ]
}
 */