// todo 관련 상태관리

import { createSlice } from "@reduxjs/toolkit"

const initialTodoState = {
  todoList: [
    { id: 1, content: "8조 화이팅!", isCompleted: false },
    { id: 2, content: "카공조아 화이팅!!", isCompleted: false },
    { id: 3, content: "조현철 짱!!!", isCompleted: false },
  ],
}

const todoSlice = createSlice({
  name: "todo",
  initialState: initialTodoState,
  reducers: {
    update(state, action) {
      state.todoList.find(todo => todo.id === action.payload).isCompleted = !state.todoList.find(todo => todo.id === action.payload).isCompleted
    },
  },
})

export const todoActions = todoSlice.actions

export default todoSlice.reducer
