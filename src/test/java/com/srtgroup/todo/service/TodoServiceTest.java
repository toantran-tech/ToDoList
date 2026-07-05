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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TodoServiceImpl Unit Tests")
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private TodoMapper todoMapper;

    @InjectMocks
    private TodoServiceImpl todoService;

    // ---- Helper fixtures ----
    private Todo sampleTodo;
    private TodoResponse sampleResponse;
    private TodoRequest sampleRequest;

    @BeforeEach
    void setUp() {
        sampleTodo = Todo.builder()
                .id(1L)
                .title("Test Todo")
                .description("Test description")
                .status(Status.TODO)
                .priority(Priority.MEDIUM)
                .deadline(LocalDateTime.now().plusDays(3))
                .build();

        sampleResponse = TodoResponse.builder()
                .id(1L)
                .title("Test Todo")
                .description("Test description")
                .status(Status.TODO)
                .priority(Priority.MEDIUM)
                .build();

        sampleRequest = TodoRequest.builder()
                .title("Test Todo")
                .description("Test description")
                .status(Status.TODO)
                .priority(Priority.MEDIUM)
                .deadline(LocalDateTime.now().plusDays(3))
                .build();
    }

    // ============================================================
    // GET TODOS
    // ============================================================
    @Nested
    @DisplayName("getTodos()")
    class GetTodosTests {

        @Test
        @DisplayName("Trả về danh sách phân trang khi có dữ liệu")
        void getTodos_withData_returnsPagedResponse() {
            Page<Todo> page = new PageImpl<>(List.of(sampleTodo), PageRequest.of(0, 10), 1);
            given(todoRepository.findByFilters(any(), any(), any(), any())).willReturn(page);
            given(todoMapper.toResponse(any(Todo.class))).willReturn(sampleResponse);

            PagedResponse<TodoResponse> result = todoService.getTodos(null, null, null, 0, 10, "createdAt", "desc");

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getCurrentPage()).isEqualTo(0);
        }

        @Test
        @DisplayName("Trả về trang rỗng khi không có dữ liệu")
        void getTodos_noData_returnsEmptyPage() {
            Page<Todo> emptyPage = Page.empty();
            given(todoRepository.findByFilters(any(), any(), any(), any())).willReturn(emptyPage);

            PagedResponse<TodoResponse> result = todoService.getTodos("keyword", Status.DONE, null, 0, 10, "title", "asc");

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    // ============================================================
    // GET BY ID
    // ============================================================
    @Nested
    @DisplayName("getTodoById()")
    class GetByIdTests {

        @Test
        @DisplayName("Trả về TodoResponse khi ID tồn tại")
        void getTodoById_existingId_returnsResponse() {
            given(todoRepository.findById(1L)).willReturn(Optional.of(sampleTodo));
            given(todoMapper.toResponse(sampleTodo)).willReturn(sampleResponse);

            TodoResponse result = todoService.getTodoById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Test Todo");
        }

        @Test
        @DisplayName("Ném TodoNotFoundException khi ID không tồn tại")
        void getTodoById_nonExistentId_throwsNotFoundException() {
            given(todoRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> todoService.getTodoById(999L))
                    .isInstanceOf(TodoNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    // ============================================================
    // CREATE TODO
    // ============================================================
    @Nested
    @DisplayName("createTodo()")
    class CreateTodoTests {

        @Test
        @DisplayName("Tạo todo thành công với dữ liệu hợp lệ")
        void createTodo_validRequest_returnsSavedTodo() {
            given(todoMapper.toEntity(sampleRequest)).willReturn(sampleTodo);
            given(todoRepository.save(sampleTodo)).willReturn(sampleTodo);
            given(todoMapper.toResponse(sampleTodo)).willReturn(sampleResponse);

            TodoResponse result = todoService.createTodo(sampleRequest);

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Test Todo");
            then(todoRepository).should(times(1)).save(any(Todo.class));
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi request null")
        void createTodo_nullRequest_throwsIllegalArgument() {
            assertThatThrownBy(() -> todoService.createTodo(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("null");
        }

        @Test
        @DisplayName("Ném IllegalArgumentException khi tiêu đề trống")
        void createTodo_blankTitle_throwsIllegalArgument() {
            TodoRequest blankRequest = TodoRequest.builder().title("   ").build();

            assertThatThrownBy(() -> todoService.createTodo(blankRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tiêu đề");
        }
    }

    // ============================================================
    // UPDATE TODO
    // ============================================================
    @Nested
    @DisplayName("updateTodo()")
    class UpdateTodoTests {

        @Test
        @DisplayName("Cập nhật todo thành công khi ID tồn tại")
        void updateTodo_existingId_returnsUpdatedTodo() {
            given(todoRepository.findById(1L)).willReturn(Optional.of(sampleTodo));
            given(todoRepository.save(sampleTodo)).willReturn(sampleTodo);
            given(todoMapper.toResponse(sampleTodo)).willReturn(sampleResponse);
            willDoNothing().given(todoMapper).updateEntity(any(Todo.class), any(TodoRequest.class));

            TodoResponse result = todoService.updateTodo(1L, sampleRequest);

            assertThat(result).isNotNull();
            then(todoMapper).should().updateEntity(eq(sampleTodo), eq(sampleRequest));
            then(todoRepository).should().save(sampleTodo);
        }

        @Test
        @DisplayName("Ném TodoNotFoundException khi ID không tồn tại")
        void updateTodo_nonExistentId_throwsNotFoundException() {
            given(todoRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> todoService.updateTodo(99L, sampleRequest))
                    .isInstanceOf(TodoNotFoundException.class);
        }
    }

    // ============================================================
    // TOGGLE STATUS
    // ============================================================
    @Nested
    @DisplayName("toggleStatus()")
    class ToggleStatusTests {

        @Test
        @DisplayName("TODO -> IN_PROGRESS khi toggle")
        void toggleStatus_fromTodo_changestoInProgress() {
            sampleTodo.setStatus(Status.TODO);
            given(todoRepository.findById(1L)).willReturn(Optional.of(sampleTodo));
            given(todoRepository.save(any(Todo.class))).willReturn(sampleTodo);

            TodoResponse expectedResponse = TodoResponse.builder()
                    .id(1L).title("Test").status(Status.IN_PROGRESS).build();
            given(todoMapper.toResponse(any(Todo.class))).willReturn(expectedResponse);

            todoService.toggleStatus(1L);

            assertThat(sampleTodo.getStatus()).isEqualTo(Status.IN_PROGRESS);
        }

        @Test
        @DisplayName("IN_PROGRESS -> DONE khi toggle")
        void toggleStatus_fromInProgress_changesToDone() {
            sampleTodo.setStatus(Status.IN_PROGRESS);
            given(todoRepository.findById(1L)).willReturn(Optional.of(sampleTodo));
            given(todoRepository.save(any(Todo.class))).willReturn(sampleTodo);
            given(todoMapper.toResponse(any(Todo.class))).willReturn(sampleResponse);

            todoService.toggleStatus(1L);

            assertThat(sampleTodo.getStatus()).isEqualTo(Status.DONE);
        }

        @Test
        @DisplayName("DONE -> TODO khi toggle (vòng lặp)")
        void toggleStatus_fromDone_changesBackToTodo() {
            sampleTodo.setStatus(Status.DONE);
            given(todoRepository.findById(1L)).willReturn(Optional.of(sampleTodo));
            given(todoRepository.save(any(Todo.class))).willReturn(sampleTodo);
            given(todoMapper.toResponse(any(Todo.class))).willReturn(sampleResponse);

            todoService.toggleStatus(1L);

            assertThat(sampleTodo.getStatus()).isEqualTo(Status.TODO);
        }
    }

    // ============================================================
    // DELETE
    // ============================================================
    @Nested
    @DisplayName("deleteTodo()")
    class DeleteTodoTests {

        @Test
        @DisplayName("Xóa thành công khi ID tồn tại")
        void deleteTodo_existingId_callsRepository() {
            given(todoRepository.existsById(1L)).willReturn(true);
            willDoNothing().given(todoRepository).deleteById(1L);

            todoService.deleteTodo(1L);

            then(todoRepository).should(times(1)).deleteById(1L);
        }

        @Test
        @DisplayName("Ném TodoNotFoundException khi ID không tồn tại")
        void deleteTodo_nonExistentId_throwsNotFoundException() {
            given(todoRepository.existsById(999L)).willReturn(false);

            assertThatThrownBy(() -> todoService.deleteTodo(999L))
                    .isInstanceOf(TodoNotFoundException.class)
                    .hasMessageContaining("999");

            then(todoRepository).should(never()).deleteById(any());
        }
    }

    // ============================================================
    // STATS
    // ============================================================
    @Nested
    @DisplayName("getStats()")
    class StatsTests {

        @Test
        @DisplayName("Trả về đúng các key thống kê")
        void getStats_returnsCorrectKeys() {
            given(todoRepository.count()).willReturn(10L);
            given(todoRepository.countByStatus(Status.TODO)).willReturn(3L);
            given(todoRepository.countByStatus(Status.IN_PROGRESS)).willReturn(4L);
            given(todoRepository.countByStatus(Status.DONE)).willReturn(3L);
            given(todoRepository.countOverdue(any(LocalDateTime.class))).willReturn(2L);

            Map<String, Long> stats = todoService.getStats();

            assertThat(stats).containsKeys("total", "todo", "inProgress", "done", "overdue");
            assertThat(stats.get("total")).isEqualTo(10L);
            assertThat(stats.get("inProgress")).isEqualTo(4L);
            assertThat(stats.get("overdue")).isEqualTo(2L);
        }
    }
}
