package com.eltonkola.mvitodolist.data.model


import java.io.Serializable

data class NoteItem(
    val id: Long = 0,
    val title: String,
    val done: Boolean = false
) : Serializable