package com.srtgroup.todo.controller;

import com.srtgroup.todo.enums.Priority;
import com.srtgroup.todo.enums.Status;
import com.srtgroup.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/")
    public String index(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(defaultValue = "0")         int page,
            @RequestParam(defaultValue = "9")         int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc")      String sortDir,
            Model model) {

        model.addAttribute("todos",    todoService.getTodos(search, status, priority, page, size, sortBy, sortDir));
        model.addAttribute("stats",    todoService.getStats());
        model.addAttribute("statuses", Status.values());
        model.addAttribute("priorities", Priority.values());

        model.addAttribute("currentSearch",   search);
        model.addAttribute("currentStatus",   status);
        model.addAttribute("currentPriority", priority);
        model.addAttribute("currentSort",     sortBy);
        model.addAttribute("currentSortDir",  sortDir);
        model.addAttribute("currentPage",     page);
        model.addAttribute("currentSize",     size);

        return "index";
    }
}
