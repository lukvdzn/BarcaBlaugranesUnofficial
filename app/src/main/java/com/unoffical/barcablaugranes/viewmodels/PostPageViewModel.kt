package com.unoffical.barcablaugranes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.unoffical.barcablaugranes.model.Comment
import com.unoffical.barcablaugranes.repository.PostPageRepository

class PostPageViewModel(private val url: String) : ViewModel() {


    private val postPageRepository = PostPageRepository(url)
    var htmlContentLiveData: LiveData<String>
    var commentsLiveData: LiveData<List<Comment>>

    init {
        htmlContentLiveData = postPageRepository.getHtmlContentLiveData()
        commentsLiveData = postPageRepository.getCommentLiveData()
    }
}