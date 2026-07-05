package com.srtgroup.todo.service;

import com.srtgroup.todo.dto.PagedResponse;
import com.srtgroup.todo.dto.TodoRequest;
import com.srtgroup.todo.dto.TodoResponse;
import com.srtgroup.todo.enums.Priority;
import com.srtgroup.todo.enums.Status;

import java.util.Map;

public interface TodoService {

    PagedResponse<TodoResponse> getTodos(
            String keyword, Status status, Priority priority,
            int page, int size, String sortBy, String sortDir);

    TodoResponse getTodoById(Long id);

    TodoResponse createTodo(TodoRequest request);

    TodoResponse updateTodo(Long id, TodoRequest request);

    TodoResponse toggleStatus(Long id);

    void deleteTodo(Long id);

    Map<String, Long> getStats();
}
