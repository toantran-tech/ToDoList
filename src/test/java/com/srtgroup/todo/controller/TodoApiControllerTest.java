package com.srtgroup.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srtgroup.todo.dto.TodoRequest;
import com.srtgroup.todo.enums.Priority;
import com.srtgroup.todo.enums.Status;
import com.srtgroup.todo.model.Todo;
import com.srtgroup.todo.repository.TodoRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("TodoApiController Integration Tests")
class TodoApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TodoRepository todoRepository;

    private Long createdId;

    @BeforeEach
    void setUp() {
        // Tạo một todo mẫu trước mỗi test
        Todo todo = todoRepository.save(Todo.builder()
                .title("Integration Test Todo")
                .description("Test description for integration")
                .status(Status.TODO)
                .priority(Priority.MEDIUM)
                .deadline(LocalDateTime.now().plusDays(5))
                .build());
        createdId = todo.getId();
    }

    @AfterEach
    void tearDown() {
        todoRepository.deleteAll();
    }

    // ============================================================
    // GET LIST
    // ============================================================
    @Test
    @Order(1)
    @DisplayName("GET /api/todos - Trả về 200 và danh sách todos")
    void getTodos_returnsOk() throws Exception {
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", is(not(empty()))))
                .andExpect(jsonPath("$.totalElements", greaterThan(0)));
    }

    @Test
    @Order(2)
    @DisplayName("GET /api/todos?status=TODO - Lọc theo trạng thái đúng")
    void getTodos_withStatusFilter_returnsFilteredList() throws Exception {
        mockMvc.perform(get("/api/todos").param("status", "TODO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].status", everyItem(is("TODO"))));
    }

    @Test
    @Order(3)
    @DisplayName("GET /api/todos?search=Integration - Tìm kiếm theo keyword")
    void getTodos_withSearch_returnsMatchingResults() throws Exception {
        mockMvc.perform(get("/api/todos").param("search", "Integration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title", containsStringIgnoringCase("Integration")));
    }

    @Test
    @Order(4)
    @DisplayName("GET /api/todos?page=0&size=5 - Phân trang hoạt động đúng")
    void getTodos_withPagination_returnsPaginatedResult() throws Exception {
        mockMvc.perform(get("/api/todos").param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentPage", is(0)))
                .andExpect(jsonPath("$.pageSize", is(5)));
    }

    // ============================================================
    // GET BY ID
    // ============================================================
    @Test
    @Order(5)
    @DisplayName("GET /api/todos/{id} - Trả về 200 khi ID tồn tại")
    void getTodoById_existingId_returnsOk() throws Exception {
        mockMvc.perform(get("/api/todos/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(createdId.intValue())))
                .andExpect(jsonPath("$.title", is("Integration Test Todo")));
    }

    @Test
    @Order(6)
    @DisplayName("GET /api/todos/{id} - Trả về 404 khi ID không tồn tại")
    void getTodoById_nonExistentId_returns404() throws Exception {
        mockMvc.perform(get("/api/todos/{id}", 99999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error").exists());
    }

    // ============================================================
    // CREATE
    // ============================================================
    @Test
    @Order(7)
    @DisplayName("POST /api/todos - Tạo thành công, trả về 201")
    void createTodo_validRequest_returns201() throws Exception {
        TodoRequest request = TodoRequest.builder()
                .title("New Integration Todo")
                .description("Created from integration test")
                .status(Status.TODO)
                .priority(Priority.HIGH)
                .deadline(LocalDateTime.now().plusDays(7))
                .build();

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title", is("New Integration Todo")))
                .andExpect(jsonPath("$.priority", is("HIGH")));
    }

    @Test
    @Order(8)
    @DisplayName("POST /api/todos - Trả về 400 khi title trống")
    void createTodo_blankTitle_returns400() throws Exception {
        TodoRequest request = TodoRequest.builder().title("").build();

        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @Order(9)
    @DisplayName("POST /api/todos - Trả về 400 khi body null/rỗng")
    void createTodo_emptyBody_returns400() throws Exception {
        mockMvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    // ============================================================
    // UPDATE
    // ============================================================
    @Test
    @Order(10)
    @DisplayName("PUT /api/todos/{id} - Cập nhật thành công")
    void updateTodo_validRequest_returnsUpdated() throws Exception {
        TodoRequest request = TodoRequest.builder()
                .title("Updated Title")
                .status(Status.IN_PROGRESS)
                .priority(Priority.HIGH)
                .build();

        mockMvc.perform(put("/api/todos/{id}", createdId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));
    }

    // ============================================================
    // TOGGLE
    // ============================================================
    @Test
    @Order(11)
    @DisplayName("PATCH /api/todos/{id}/toggle - Toggle trạng thái đúng")
    void toggleStatus_existingId_changesStatus() throws Exception {
        mockMvc.perform(patch("/api/todos/{id}/toggle", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PROGRESS"))); // TODO -> IN_PROGRESS
    }

    @Test
    @Order(12)
    @DisplayName("PATCH /api/todos/99999/toggle - Trả về 404 khi ID không tồn tại")
    void toggleStatus_nonExistentId_returns404() throws Exception {
        mockMvc.perform(patch("/api/todos/{id}/toggle", 99999L))
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // DELETE
    // ============================================================
    @Test
    @Order(13)
    @DisplayName("DELETE /api/todos/{id} - Xóa thành công, trả về 200")
    void deleteTodo_existingId_returns200() throws Exception {
        mockMvc.perform(delete("/api/todos/{id}", createdId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @Order(14)
    @DisplayName("DELETE /api/todos/99999 - Trả về 404 khi ID không tồn tại")
    void deleteTodo_nonExistentId_returns404() throws Exception {
        mockMvc.perform(delete("/api/todos/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    // ============================================================
    // STATS
    // ============================================================
    @Test
    @Order(15)
    @DisplayName("GET /api/todos/stats - Trả về thống kê đúng keys")
    void getStats_returnsCorrectKeys() throws Exception {
        mockMvc.perform(get("/api/todos/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").exists())
                .andExpect(jsonPath("$.todo").exists())
                .andExpect(jsonPath("$.inProgress").exists())
                .andExpect(jsonPath("$.done").exists())
                .andExpect(jsonPath("$.overdue").exists());
    }
}
