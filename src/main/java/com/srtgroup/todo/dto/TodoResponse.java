package com.srtgroup.todo.dto;

import com.srtgroup.todo.enums.Priority;
import com.srtgroup.todo.enums.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoResponse {

    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private LocalDateTime deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isOverdue() {
        return deadline != null
                && status != Status.DONE
                && deadline.isBefore(LocalDateTime.now());
    }

    public String getStatusLabel() {
        if (status == null)
            return "";
        return switch (status) {
            case TODO -> "Chưa làm";
            case IN_PROGRESS -> "Đang làm";
            case DONE -> "Hoàn thành";
        };
    }

    public String getPriorityLabel() {
        if (priority == null)
            return "";
        return switch (priority) {
            case LOW -> "Thấp";
            case MEDIUM -> "Trung bình";
            case HIGH -> "Cao";
            case URGENT -> "Khẩn cấp";
        };
    }
}
