package com.unoffical.barcablaugranes.model

sealed class PostCategory(val title:String,
                          val author:String,
                          val commentsCount:String,
                          val linkToPost:String?) {
    class MainFivePost(title:String,
                       author:String,
                       commentsCount:String,
                       linkToPost:String?,
                       val linkToImage:String?) : PostCategory(title, author, commentsCount, linkToPost)

    class LatestStoryPost(title:String,
                          author:String,
                          commentsCount:String,
                          linkToPost:String?,
                          val description: String,
                          val date:String) : PostCategory(title, author, commentsCount, linkToPost)
}