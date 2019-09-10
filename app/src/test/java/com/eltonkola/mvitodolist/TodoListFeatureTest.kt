package com.eltonkola.mvitodolist


import com.badoo.mvicore.android.AndroidTimeCapsule
import com.eltonkola.mvitodolist.mvicore.TodoListFeature
import com.eltonkola.mvitodolist.data.model.NoteItem
import org.junit.Assert.assertEquals
import org.junit.Test

class TodoListFeatureTest {

    @Test
    fun `creating a todo increments id`() {
        val feature = createFeature()

        feature.accept(
            TodoListFeature.Wish.Create(
                NoteItem(
                    title = "Test"
                )
            )
        )

        assertEquals(
            1,
            feature.state.nextId
        )
    }

    @Test
    fun `creating a todo adds it to the state with incremented id`() {
        val feature = createFeature()

        feature.accept(
            TodoListFeature.Wish.Create(
                NoteItem(
                    title = "Test"
                )
            )
        )
        feature.accept(
            TodoListFeature.Wish.Create(
                NoteItem(
                    title = "Test 1"
                )
            )
        )

        assertEquals(
            listOf(
                NoteItem(id = 0, title = "Test"),
                NoteItem(id = 1, title = "Test 1")
            ),
            feature.state.todos
        )
    }

    @Test
    fun `updating a todo replaces it in the state with given id`() {
        val feature = createFeature()
        val initialItems = listOf(
            NoteItem(title = "Test"),
            NoteItem(title = "Test 1")
        )
        val newItem =
            NoteItem(id = 0, title = "Test", done = true)

        initialItems.forEach {
            feature.accept(TodoListFeature.Wish.Create(it))
        }
        feature.accept(TodoListFeature.Wish.UpdateDone(newItem))

        assertEquals(
            listOf(newItem,
                NoteItem(id = 1, title = "Test 1")
            ),
            feature.state.todos
        )
    }

    @Test
    fun `updating todo with not existing id does not change the list`() {
        val feature = createFeature()

        feature.accept(
            TodoListFeature.Wish.Create(
                NoteItem(
                    title = "Test"
                )
            ))
        feature.accept(
            TodoListFeature.Wish.UpdateDone(
                NoteItem(
                    id = 1,
                    title = "Test"
                )
            ))

        assertEquals(
            listOf(NoteItem(id = 0, title = "Test")),
            feature.state.todos
        )
    }

    @Test
    fun `deleting todo removes it from a list`() {
        val feature = createFeature()

        feature.accept(
            TodoListFeature.Wish.Create(
                NoteItem(
                    title = "Test"
                )
            )
        )
        feature.accept(
            TodoListFeature.Wish.Create(
                NoteItem(
                    title = "Test 1"
                )
            )
        )

        feature.accept(
            TodoListFeature.Wish.Delete(
                NoteItem(
                    id = 1,
                    title = "Test 1"
                )
            )
        )

        assertEquals(
            listOf(NoteItem(id = 0, title = "Test")),
            feature.state.todos
        )
    }

    private fun createFeature() =
        TodoListFeature(AndroidTimeCapsule(null))
}