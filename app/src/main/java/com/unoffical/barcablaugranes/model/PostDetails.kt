package com.unoffical.barcablaugranes.model

data class PostDetails(val title: String = "",
                       val subTitle: String = "",
                       val author: String = "",
                       val date: String = "",
                       val imageUrl: String = "",
                       val imagePublisher: String = "",
                       val content: List<String> = emptyList())