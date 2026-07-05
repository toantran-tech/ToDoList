package com.srtgroup.todo.service;

import com.srtgroup.todo.dto.PagedResponse;
import com.srtgroup.todo.dto.TodoRequest;
import com.srtgroup.todo.dto.TodoResponse;
import com.srtgroup.todo.enums.Priority;
import com.srtgroup.todo.enums.Status;
import com.srtgroup.todo.exception.TodoNotFoundException;
import com.srtgroup.todo.mapper.TodoMapper;
import com.srtgroup.todo.model.Todo;
import com.srtgroup.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TodoResponse> getTodos(
            String keyword, Status status, Priority priority,
            int page, int size, String sortBy, String sortDir) {

        Sort sort = buildSort(sortBy, sortDir);
        Pageable pageable = PageRequest.of(page, size, sort);
        String kw = StringUtils.hasText(keyword) ? keyword.trim() : null;

        Page<Todo> todoPage = todoRepository.findByFilters(kw, status, priority, pageable);

        List<TodoResponse> content = todoPage.getContent().stream()
                .map(todoMapper::toResponse)
                .toList();

        return PagedResponse.<TodoResponse>builder()
                .content(content)
                .currentPage(todoPage.getNumber())
                .totalPages(todoPage.getTotalPages())
                .totalElements(todoPage.getTotalElements())
                .pageSize(todoPage.getSize())
                .hasNext(todoPage.hasNext())
                .hasPrevious(todoPage.hasPrevious())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public TodoResponse getTodoById(Long id) {
        return todoMapper.toResponse(findTodoOrThrow(id));
    }

    @Override
    public TodoResponse createTodo(TodoRequest request) {
        validateRequest(request);
        Todo saved = todoRepository.save(todoMapper.toEntity(request));
        return todoMapper.toResponse(saved);
    }

    @Override
    public TodoResponse updateTodo(Long id, TodoRequest request) {
        validateRequest(request);
        Todo todo = findTodoOrThrow(id);
        todoMapper.updateEntity(todo, request);
        return todoMapper.toResponse(todoRepository.save(todo));
    }

    @Override
    public TodoResponse toggleStatus(Long id) {
        Todo todo = findTodoOrThrow(id);
        Status next = switch (todo.getStatus()) {
            case TODO        -> Status.IN_PROGRESS;
            case IN_PROGRESS -> Status.DONE;
            case DONE        -> Status.TODO;
        };
        todo.setStatus(next);
        return todoMapper.toResponse(todoRepository.save(todo));
    }

    @Override
    public void deleteTodo(Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total",      todoRepository.count());
        stats.put("todo",       todoRepository.countByStatus(Status.TODO));
        stats.put("inProgress", todoRepository.countByStatus(Status.IN_PROGRESS));
        stats.put("done",       todoRepository.countByStatus(Status.DONE));
        stats.put("overdue",    todoRepository.countOverdue(LocalDateTime.now()));
        return stats;
    }

    private Todo findTodoOrThrow(Long id) {
        return todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    private void validateRequest(TodoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request không được null");
        }
        if (!StringUtils.hasText(request.getTitle())) {
            throw new IllegalArgumentException("Tiêu đề không được để trống");
        }
    }

    private Sort buildSort(String sortBy, String sortDir) {
        List<String> allowed = List.of("createdAt", "updatedAt", "deadline", "title", "priority", "status");
        String field = allowed.contains(sortBy) ? sortBy : "createdAt";
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return Sort.by(direction, field);
    }
}
