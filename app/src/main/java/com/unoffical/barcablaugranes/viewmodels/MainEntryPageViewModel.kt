package com.unoffical.barcablaugranes.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unoffical.barcablaugranes.model.PostCategory
import com.unoffical.barcablaugranes.repository.MainEntryRepository

class MainEntryPageViewModel : ViewModel() {

    val mMainEntryPostElements =  MutableLiveData<List<PostCategory>>()
    private val repository: MainEntryRepository = MainEntryRepository.getInstance()

    init {
        repository.getMainPosts {
            mMainEntryPostElements.value = it
        }
    }
}