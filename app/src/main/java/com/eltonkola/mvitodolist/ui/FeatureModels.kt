package com.eltonkola.mvitodolist.ui

import com.eltonkola.adapterz_lib.BaseDataItem
import com.eltonkola.mvitodolist.data.model.NoteItem

//ui model, will be rendered on the adapter
data class NoteUiItem(val data: NoteItem) : BaseDataItem

//compare items, we want done below
object TodoComparator : Comparator<NoteItem> {
    override fun compare(todo1: NoteItem, todo2: NoteItem): Int {
        val doneCompareResult = todo1.done.compareTo(todo2.done)
        return if (doneCompareResult == 0) {
            todo1.id.compareTo(todo2.id)
        } else {
            doneCompareResult
        }
    }
}

//all action we can take from ui
sealed class TodoActions {
    class Update(val item: NoteItem) : TodoActions()
    class Delete(val item: NoteItem) : TodoActions()
    class Create(val title: String) : TodoActions()
    class ResetDialog : TodoActions()
}

//all reactions that the data layer can trigger
sealed class TodoReactions {

    sealed class UpdateElement{
        class Start : TodoReactions()
        class End(val items: List<NoteItem>) : TodoReactions()
        class Error(val error: String) : TodoReactions()
    }
    sealed class DeleteElement {
        class Start : TodoReactions()
        class End(val items: List<NoteItem>) : TodoReactions()
        class Error(val error: String) : TodoReactions()
    }
    sealed class CreateElement {
        class Start : TodoReactions()
        class End(val items: List<NoteItem>) : TodoReactions()
        class Error(val error: String) : TodoReactions()
    }
    class ErrorReported : TodoReactions()
    class EmptyDataState : TodoReactions()
}

//representation of the ui layer
data class UiState(
    val isLoading: Boolean = false,
    val emptyState: Boolean = false,
    val errorMsg: String = "",
    val data: List<NoteItem> = emptyList()
)