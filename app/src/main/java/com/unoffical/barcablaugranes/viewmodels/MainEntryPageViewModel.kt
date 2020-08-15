package com.unoffical.barcablaugranes.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.unoffical.barcablaugranes.model.PostCategory
import com.unoffical.barcablaugranes.repository.MainEntryRepository

class MainEntryPageViewModel : ViewModel() {

    var mMainEntryPostElements: LiveData<List<PostCategory>>
    private val repository: MainEntryRepository = MainEntryRepository.getInstance()

    init {
        mMainEntryPostElements = repository.getPostData()
    }
}