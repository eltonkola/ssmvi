# Simple Stupid MVI
Basic example of mvi using mvvm and rx.

After going throw some demos using the latest android mvi framework out there, i came out with a simple solution, that i can play with, using exiting mvvm pattern.

This sample app is a todo note app, with a stupid NoteRepo that will randomly fail to do any crud operation.

The idea is simple, we still use the android architecture ViewModel for out logic, but we also will have an mvi flow of events and state.

The activity/fragment will only have a single interaction with the viewmodel, sending events by calling:
```kotlin
   viewModel.onAction(..)
``` 
and will render/bind the view state as normal with 
```kotlin
viewModel.viewState.observe(this, Observer { state ->
    ...
    })
``` 

Next Step will be defining all possible interactions with our viewModel, will call them actions, and looks something like this:
```kotlin
sealed class TodoActions {
    class Update(val item: NoteItem) : TodoActions()
    class Delete(val item: NoteItem) : TodoActions()
    class Create(val title: String) : TodoActions()
    class ResetDialog : TodoActions()
}
``` 

if we need any other kind of ui/user driven event we need to define it here, after that we will define the reactions. A reaction is a result of an action. It can be one or many. Lets say we want to save some data, it can fail, load the data, or have any other result (like empty state), for this reason for every action, we will create any possible reaction, in our case it will look like this:
```kotlin
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
  
```        

where basically for the crud operations we want 3 states, and an error toast event, and empty data state.
Lets define the ui state too, and we are set:

```kotlin
data class UiState(
    val isLoading: Boolean = false,
    val emptyState: Boolean = false,
    val errorMsg: String = "",
    val data: List<NoteItem> = emptyList()
)
```     
this state will reflect the ui state of the demo at any time

No we extend the BaseViewModel (it is on the project, did no see any need to create it as a separate library): 

```kotlin
   class DemoViewModel : BaseViewModel<UiState, TodoActions, TodoReactions>(UiState())
  
```        
 and override the bindActions method, calling internal methods to run crud operations
 
 ```kotlin
     override fun bindActions(event: TodoActions) {
        when (event) {
            is TodoActions.Create -> createNote(event.title)
            is TodoActions.Update -> updateNote(event.item)
            is TodoActions.Delete -> deleteNote(event.item)
            is TodoActions.ResetDialog -> resetDialog()
        }
    }
```  

the other method we need to override is the redux method that will generate a new state on reaction event received:
 ```kotlin
    override fun reactionToStateRedux(reaction: TodoReactions) {
        with(viewState.value!!) {
            viewState.value = when (reaction) {
                is TodoReactions.UpdateElement.Start -> {
                    this.copy(isLoading = true)
                }
                ....
           }
      }
```   

now all we nedd to do, is call postReaction(it) from out crud methods, here is an example:

  ```kotlin   private fun createNote(note: String) {
        repo.createNote(note)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .toObservable()
            //we will post TodoReactions from here on
            .map<TodoReactions> { TodoReactions.CreateElement.End(it) }
            //send empty state
            .defaultIfEmpty(TodoReactions.EmptyDataState())
            //handle error
            .onErrorReturn { TodoReactions.CreateElement.Error("Error saving note: ${it.message}") }
            //send initial state
            .startWith(TodoReactions.CreateElement.Start())
            //send the event
            .subscribe { postReaction(it) }
            .autoDispose()

    }
```  

We don't have to use rx in here, this is just an example.

