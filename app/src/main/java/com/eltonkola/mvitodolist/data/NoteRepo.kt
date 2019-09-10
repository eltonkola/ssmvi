package com.eltonkola.mvitodolist.data

import com.eltonkola.mvitodolist.data.model.NoteItem
import io.reactivex.Single
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class NoteRepo {

    private var nextId: Long = 0
    private val data = mutableListOf<NoteItem>()

    fun createNote(text: String): Single<List<NoteItem>> {
        return Single.create<List<NoteItem>> { emitter ->
            if (Random.nextBoolean()) {
                data.add(NoteItem(nextId, text, false))
                nextId++
                emitter.onSuccess(data.toList())
            } else {
                emitter.onError(Exception("Error saving item"))
            }
        }.delay(200, TimeUnit.MILLISECONDS)
    }

    fun updateNote(item: NoteItem): Single<List<NoteItem>> {
        return Single.create<List<NoteItem>> { emitter ->
            if (Random.nextBoolean()) {
                val foundItem = data.first { it.id == item.id }
                val pos = data.indexOf(foundItem)
                data.remove(foundItem)
                data.add(pos, foundItem.copy(done = item.done))
                emitter.onSuccess(data.toList())
            } else {
                emitter.onError(Exception("Error updating item"))
            }
        }.delay(200, TimeUnit.MILLISECONDS)
    }

    fun deleteNote(item: NoteItem): Single<List<NoteItem>> {
        return Single.create<List<NoteItem>> { emitter ->
            if (Random.nextBoolean()) {
                val foundItem = data.first { it.id == item.id }
                data.remove(foundItem)
                emitter.onSuccess(data.toList())
            } else {
                emitter.onError(Exception("Error removing item"))
            }
        }.delay(200, TimeUnit.MILLISECONDS)
    }


}
