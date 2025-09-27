package pnu.plato.calendar.presentation.todo

import dagger.hilt.android.lifecycle.HiltViewModel
import pnu.plato.calendar.presentation.common.base.BaseViewModel
import pnu.plato.calendar.presentation.todo.intent.ToDoEvent
import pnu.plato.calendar.presentation.todo.intent.ToDoSideEffect
import pnu.plato.calendar.presentation.todo.intent.ToDoState
import javax.inject.Inject

@HiltViewModel
class ToDoViewModel @Inject constructor() :
    BaseViewModel<ToDoState, ToDoEvent, ToDoSideEffect>(initialState = ToDoState()) {

    override suspend fun handleEvent(event: ToDoEvent) {
        when(event) {
            else -> Unit // TODO
        }
    }
}