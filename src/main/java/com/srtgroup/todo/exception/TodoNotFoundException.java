package com.srtgroup.todo.exception;

public class TodoNotFoundException extends RuntimeException {

    public TodoNotFoundException(Long id) {
        super("Không tìm thấy công việc với ID: " + id);
    }

    public TodoNotFoundException(String message) {
        super(message);
    }
}
