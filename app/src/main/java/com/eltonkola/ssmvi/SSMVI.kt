package com.eltonkola.ssmvi.BaseViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseViewModel<T, E, D>(initialState: T) : ViewModel() {

    var viewState = MutableLiveData<T>().apply {
        value = initialState
    }

    fun onAction(action: E) {
        bindActions(action)
    }

    fun postReaction(reaction: D) {
        reactionToStateRedux(reaction)
    }

    protected abstract fun bindActions(event: E)
    protected abstract fun reactionToStateRedux(reaction: D)

    private val compositeDisposable = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun Disposable.autoDispose() {
        compositeDisposable.add(this)
    }

}

