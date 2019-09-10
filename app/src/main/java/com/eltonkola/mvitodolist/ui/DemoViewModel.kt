package com.eltonkola.mvitodolist.ui

import com.eltonkola.mvitodolist.data.NoteRepo
import com.eltonkola.mvitodolist.data.model.NoteItem
import com.eltonkola.ssmvi.BaseViewModel.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class DemoViewModel : BaseViewModel<UiState, TodoActions, TodoReactions>(UiState()) {

    //from reaction to new ui state
    override fun reactionToStateRedux(reaction: TodoReactions) {
        with(viewState.value!!) {
            viewState.value = when (reaction) {
                is TodoReactions.UpdateElement.Start -> {
                    this.copy(isLoading = true)
                }
                is TodoReactions.UpdateElement.End -> this.copy(
                    isLoading = false,
                    data = reaction.items,
                    emptyState = false
                )
                is TodoReactions.UpdateElement.Error -> this.copy(
                    isLoading = false,
                    errorMsg = reaction.error
                )
                is TodoReactions.DeleteElement.Start -> this.copy(isLoading = true)
                is TodoReactions.DeleteElement.End -> this.copy(
                    isLoading = false,
                    data = reaction.items,
                    emptyState = false
                )
                is TodoReactions.DeleteElement.Error -> this.copy(
                    isLoading = false,
                    errorMsg = reaction.error
                )
                is TodoReactions.CreateElement.Start -> this.copy(isLoading = true)
                is TodoReactions.CreateElement.End -> this.copy(
                    isLoading = false,
                    data = reaction.items,
                    emptyState = false
                )
                is TodoReactions.CreateElement.Error -> this.copy(
                    isLoading = false,
                    errorMsg = reaction.error
                )
                is TodoReactions.ErrorReported -> this.copy(errorMsg = "")
                is TodoReactions.EmptyDataState -> this.copy(emptyState = true)
            }
        }
    }

    //bind your actions
    override fun bindActions(event: TodoActions) {
        when (event) {
            is TodoActions.Create -> createNote(event.title)
            is TodoActions.Update -> updateNote(event.item)
            is TodoActions.Delete -> deleteNote(event.item)
            is TodoActions.ResetDialog -> resetDialog()
        }
    }

    //view model logic, any operation can call postReaction(AnyReactionType), and redux will take over, dont have to use rx

    private val repo = NoteRepo()

    private fun createNote(note: String) {
        repo.createNote(note)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
            .map<TodoReactions> { TodoReactions.CreateElement.End(it) }
            .defaultIfEmpty(TodoReactions.EmptyDataState())
            .onErrorReturn { TodoReactions.CreateElement.Error("Error saving note: ${it.message}") }
            .startWith(TodoReactions.CreateElement.Start())
            .subscribe { postReaction(it) }
            .autoDispose()

    }

    private fun resetDialog() {
        postReaction(TodoReactions.ErrorReported())
    }

    private fun deleteNote(item: NoteItem) {
        repo.deleteNote(item)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
            .map<TodoReactions> { TodoReactions.CreateElement.End(it) }
            .defaultIfEmpty(TodoReactions.EmptyDataState())
            .onErrorReturn { TodoReactions.CreateElement.Error("Error saving note: ${it.message}") }
            .startWith(TodoReactions.CreateElement.Start())
            .subscribe { postReaction(it) }
            .autoDispose()
    }

    private fun updateNote(item: NoteItem) {
        repo.updateNote(item)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
            .map<TodoReactions> { TodoReactions.CreateElement.End(it) }
            .defaultIfEmpty(TodoReactions.EmptyDataState())
            .onErrorReturn { TodoReactions.CreateElement.Error("Error saving note: ${it.message}") }
            .startWith(TodoReactions.CreateElement.Start())
            .subscribe { postReaction(it) }
            .autoDispose()
    }

}
