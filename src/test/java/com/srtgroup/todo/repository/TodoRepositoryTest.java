package com.srtgroup.todo.repository;

import com.srtgroup.todo.enums.Priority;
import com.srtgroup.todo.enums.Status;
import com.srtgroup.todo.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("TodoRepository Tests")
class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        todoRepository.deleteAll();

        todoRepository.save(Todo.builder()
                .title("Spring Boot Tutorial")
                .description("Learn Spring Boot fundamentals")
                .status(Status.TODO)
                .priority(Priority.HIGH)
                .deadline(LocalDateTime.now().plusDays(5))
                .build());

        todoRepository.save(Todo.builder()
                .title("Viết unit test")
                .description("Viết test coverage cho service layer")
                .status(Status.IN_PROGRESS)
                .priority(Priority.HIGH)
                .deadline(LocalDateTime.now().plusDays(2))
                .build());

        todoRepository.save(Todo.builder()
                .title("Deploy lên server")
                .description("Build Docker và deploy")
                .status(Status.DONE)
                .priority(Priority.MEDIUM)
                .deadline(LocalDateTime.now().minusDays(1)) // Đã qua deadline
                .build());

        todoRepository.save(Todo.builder()
                .title("Review code")
                .description(null)
                .status(Status.TODO)
                .priority(Priority.LOW)
                .deadline(LocalDateTime.now().minusDays(2)) // Quá hạn
                .build());
    }

    @Test
    @DisplayName("Lọc theo status=TODO trả về đúng số lượng")
    void findByStatus_TODO_returnsCorrectCount() {
        Page<Todo> result = todoRepository.findByStatus(Status.TODO, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).allMatch(t -> t.getStatus() == Status.TODO);
    }

    @Test
    @DisplayName("Lọc theo status=DONE trả về đúng")
    void findByStatus_DONE_returnsOnlyDone() {
        Page<Todo> result = todoRepository.findByStatus(Status.DONE, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Deploy lên server");
    }

    @Test
    @DisplayName("Tìm kiếm theo keyword trong title (không phân biệt hoa thường)")
    void findByFilters_keywordInTitle_returnsMatches() {
        Page<Todo> result = todoRepository.findByFilters("spring", null, null, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).containsIgnoringCase("spring");
    }

    @Test
    @DisplayName("Tìm kiếm theo keyword trong description")
    void findByFilters_keywordInDescription_returnsMatches() {
        Page<Todo> result = todoRepository.findByFilters("Docker", null, null, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getDescription()).containsIgnoringCase("Docker");
    }

    @Test
    @DisplayName("Filter kết hợp keyword + status")
    void findByFilters_keywordAndStatus_returnsFilteredList() {
        Page<Todo> result = todoRepository.findByFilters("test", Status.IN_PROGRESS, null, PageRequest.of(0, 10));
        assertThat(result.getContent()).allMatch(t -> t.getStatus() == Status.IN_PROGRESS);
    }

    @Test
    @DisplayName("Filter không có keyword trả về toàn bộ theo status")
    void findByFilters_nullKeyword_returnsAllMatchingStatus() {
        Page<Todo> result = todoRepository.findByFilters(null, Status.TODO, null, PageRequest.of(0, 10));
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    @DisplayName("countByStatus đúng với từng trạng thái")
    void countByStatus_returnsCorrectCount() {
        assertThat(todoRepository.countByStatus(Status.TODO)).isEqualTo(2);
        assertThat(todoRepository.countByStatus(Status.IN_PROGRESS)).isEqualTo(1);
        assertThat(todoRepository.countByStatus(Status.DONE)).isEqualTo(1);
    }

    @Test
    @DisplayName("countOverdue đếm đúng công việc quá hạn chưa DONE")
    void countOverdue_returnsCorrectOverdueCount() {
        long overdue = todoRepository.countOverdue(LocalDateTime.now());
        // "Review code" (deadline -2 days, status TODO) bị quá hạn
        // "Deploy lên server" (deadline -1 day, status DONE) không tính
        assertThat(overdue).isEqualTo(1);
    }

    @Test
    @DisplayName("save() tự động set createdAt")
    void save_setsCreatedAtAutomatically() {
        Todo todo = todoRepository.save(Todo.builder()
                .title("Auto timestamp test")
                .status(Status.TODO)
                .priority(Priority.LOW)
                .build());

        assertThat(todo.getCreatedAt()).isNotNull();
        assertThat(todo.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Phân trang hoạt động đúng")
    void findByFilters_pagination_returnsCorrectPage() {
        Page<Todo> page0 = todoRepository.findByFilters(null, null, null, PageRequest.of(0, 2));
        Page<Todo> page1 = todoRepository.findByFilters(null, null, null, PageRequest.of(1, 2));

        assertThat(page0.getContent()).hasSize(2);
        assertThat(page0.getTotalPages()).isEqualTo(2);
        assertThat(page1.getContent()).hasSize(2);
    }
}
