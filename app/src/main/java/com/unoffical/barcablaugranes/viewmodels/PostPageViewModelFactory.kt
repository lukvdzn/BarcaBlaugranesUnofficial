package com.unoffical.barcablaugranes.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


@Suppress("UNCHECKED_CAST")
class PostPageViewModelFactory(private val param: String) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PostPageViewModel(param) as T
    }
}