package com.unoffical.barcablaugranes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.unoffical.barcablaugranes.model.Comment
import com.unoffical.barcablaugranes.model.PostDetails
import com.unoffical.barcablaugranes.repository.PostPageRepository

class PostPageViewModel(url: String) : ViewModel() {


    private val postPageRepository = PostPageRepository(url)
    var postLiveData: LiveData<PostDetails>
    var commentsLiveData: LiveData<List<Comment>>

    init {
        postLiveData = postPageRepository.getPostLiveData()
        commentsLiveData = postPageRepository.getCommentLiveData()
    }
}