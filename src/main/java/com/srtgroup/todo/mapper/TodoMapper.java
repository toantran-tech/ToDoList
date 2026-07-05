package com.srtgroup.todo.mapper;

import com.srtgroup.todo.dto.TodoRequest;
import com.srtgroup.todo.dto.TodoResponse;
import com.srtgroup.todo.enums.Priority;
import com.srtgroup.todo.enums.Status;
import com.srtgroup.todo.model.Todo;
import org.springframework.stereotype.Component;

@Component
public class TodoMapper {

    public TodoResponse toResponse(Todo todo) {
        if (todo == null) return null;
        return TodoResponse.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .description(todo.getDescription())
                .status(todo.getStatus())
                .priority(todo.getPriority())
                .deadline(todo.getDeadline())
                .createdAt(todo.getCreatedAt())
                .updatedAt(todo.getUpdatedAt())
                .build();
    }

    public Todo toEntity(TodoRequest request) {
        if (request == null) return null;
        return Todo.builder()
                .title(request.getTitle().trim())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .status(request.getStatus() != null ? request.getStatus() : Status.TODO)
                .priority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM)
                .deadline(request.getDeadline())
                .build();
    }

    public void updateEntity(Todo todo, TodoRequest request) {
        if (request.getTitle() != null) {
            todo.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            todo.setDescription(request.getDescription().trim());
        }
        if (request.getStatus() != null) {
            todo.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            todo.setPriority(request.getPriority());
        }
        todo.setDeadline(request.getDeadline());
    }
}
