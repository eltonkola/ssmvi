package com.eltonkola.mvitodolist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eltonkola.adapterz_lib.AdapterZ
import com.eltonkola.adapterz_lib.ViewRenderZ
import com.eltonkola.mvitodolist.R
import com.eltonkola.mvitodolist.ui.TodoActions.*
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.todo_item.view.*

class DemoFragment : Fragment() {

    companion object {
        fun newInstance() = DemoFragment()
    }

    private lateinit var viewModel: DemoViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(DemoViewModel::class.java)

        main_todoSubmit.setOnClickListener {
            if (main_todoInput.text.toString().isNotEmpty()) {
                viewModel.onAction(Create(main_todoInput.text.toString()))
                main_todoInput.text?.clear()
            }
        }

        val adapter = AdapterZ()
        adapter.addRenderer(todoItemRenderer({
            viewModel.onAction(Delete(it.data))
        }, {
            viewModel.onAction(Update(it.data))
        }))

        main_todoList.layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        main_todoList.setHasFixedSize(true)
        main_todoList.adapter = adapter


        viewModel.viewState.observe(this, Observer { state ->
            if (state.isLoading) {
                loading.visibility = View.VISIBLE
            } else {
                loading.visibility = View.GONE
            }

            if (state.errorMsg.isNotEmpty()) {
                Toast.makeText(context, state.errorMsg, Toast.LENGTH_SHORT).show()
                viewModel.onAction(ResetDialog())
            }

            adapter.submitList(state.data.sortedWith(TodoComparator).map { NoteUiItem(it) })
        })
    }

    //will render the list item
    private fun todoItemRenderer(
        doDelete: (NoteUiItem) -> Any,
        onChecked: (NoteUiItem) -> Any
    ): ViewRenderZ<NoteUiItem> {
        return ViewRenderZ(R.layout.todo_item) { vh, model ->
            vh.itemView.todoItem_checkbox.text = model.data.title
            vh.itemView.todoItem_checkbox.isChecked = model.data.done
            vh.itemView.todoItem_delete.setOnClickListener {
                doDelete.invoke(model)
            }
            vh.itemView.todoItem_checkbox.setOnCheckedChangeListener { _, checked ->
                onChecked.invoke(
                    NoteUiItem(model.data.copy(done = checked))
                )
            }
            true
        }
    }

}
