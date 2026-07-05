# 📋 KẾ HOẠCH XÂY DỰNG TODO LIST APP
## Bài Test Intern Developer — SRT Group

---

## 🎯 Mục tiêu

Xây dựng ứng dụng **Quản lý công việc (Todo List)** đáp ứng đầy đủ yêu cầu bài test, thể hiện:
- Tư duy giải quyết vấn đề
- Chất lượng mã nguồn sạch, dễ đọc
- Tổ chức dự án chuyên nghiệp
- Xử lý các tình huống phát sinh

---

## 🛠️ Tech Stack

| Layer | Công nghệ | Lý do chọn |
|---|---|---|
| **Ngôn ngữ** | Java 17 | Yêu cầu đề bài |
| **Framework** | Spring Boot 3.x | Phổ biến, production-ready |
| **ORM** | Spring Data JPA + Hibernate | Clean data access layer |
| **Database** | H2 (in-memory) | Zero-config, chạy ngay không cần cài thêm |
| **Validation** | Spring Boot Validation (Bean Validation) | Xử lý dữ liệu không hợp lệ |
| **Frontend** | Thymeleaf + Vanilla CSS + JS | SSR, không cần build frontend riêng |
| **Build tool** | Maven | Quản lý dependencies |
| **Container** | Docker + Docker Compose | Khuyến khích từ đề bài |
| **Testing** | JUnit 5 + Mockito | Unit test + Integration test |

> **Tại sao H2?** Người chấm bài chỉ cần `mvn spring-boot:run` là chạy được ngay, không cần cài MySQL/PostgreSQL.
> H2 Console tích hợp tại `http://localhost:8080/h2-console` để kiểm tra DB trực tiếp.

---

## ✅ Tính năng triển khai

### Bắt buộc
- [x] **Hiển thị danh sách công việc** — Danh sách dạng card, hiển thị tiêu đề, mô tả, deadline, priority, trạng thái
- [x] **Thêm công việc mới** — Form validation đầy đủ (tiêu đề bắt buộc, deadline không được là quá khứ...)
- [x] **Chỉnh sửa công việc** — Modal edit, cập nhật inline
- [x] **Xóa công việc** — Xác nhận trước khi xóa
- [x] **Đánh dấu hoàn thành/chưa hoàn thành** — Toggle 1 click, cập nhật ngay lập tức (AJAX)
- [x] **Tìm kiếm / Lọc theo trạng thái** — Search theo tên, lọc theo: Tất cả / Đang làm / Hoàn thành / Quá hạn

### Khuyến khích (sẽ làm)
- [x] **Phân trang** — Spring Data Pageable, chọn số item/trang
- [x] **Sắp xếp** — Theo deadline, ngày tạo, priority, tên (ASC/DESC)
- [x] **Responsive UI** — Mobile-first CSS, giao diện đẹp premium
- [x] **Docker** — Dockerfile + docker-compose.yml
- [x] **Unit Test** — Service layer tests + Controller integration tests

---

## 📁 Cấu trúc dự án

```
todo-app/
├── src/
│   ├── main/
│   │   ├── java/com/srtgroup/todo/
│   │   │   ├── TodoApplication.java                    # Entry point
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   ├── TodoController.java                 # Thymeleaf web controller (GET pages)
│   │   │   │   └── TodoApiController.java              # REST API controller (/api/todos)
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── TodoService.java                    # Interface — định nghĩa contract
│   │   │   │   └── TodoServiceImpl.java                # Implementation — business logic
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   └── TodoRepository.java                 # Spring Data JPA Repository
│   │   │   │
│   │   │   ├── model/
│   │   │   │   └── Todo.java                           # JPA Entity (@Entity)
│   │   │   │
│   │   │   ├── dto/
│   │   │   │   ├── TodoRequest.java                    # Input DTO (có @Valid annotations)
│   │   │   │   ├── TodoResponse.java                   # Output DTO (trả về client)
│   │   │   │   └── PagedResponse.java                  # Wrapper cho phân trang
│   │   │   │
│   │   │   ├── enums/
│   │   │   │   ├── Priority.java                       # LOW, MEDIUM, HIGH, URGENT
│   │   │   │   └── Status.java                         # TODO, IN_PROGRESS, DONE
│   │   │   │
│   │   │   ├── exception/
│   │   │   │   ├── TodoNotFoundException.java           # Custom exception
│   │   │   │   ├── ValidationException.java            # Custom validation exception
│   │   │   │   └── GlobalExceptionHandler.java         # @ControllerAdvice xử lý tập trung
│   │   │   │
│   │   │   └── mapper/
│   │   │       └── TodoMapper.java                     # Entity <-> DTO conversion
│   │   │
│   │   └── resources/
│   │       ├── application.properties                  # Config H2, JPA, server port
│   │       ├── data.sql                                # Seed data mẫu
│   │       ├── templates/
│   │       │   ├── index.html                          # Trang chủ (danh sách + filter)
│   │       │   ├── error.html                          # Trang lỗi đẹp
│   │       │   └── fragments/
│   │       │       ├── layout.html                     # Layout chung (head, nav, footer)
│   │       │       └── todo-card.html                  # Fragment card công việc
│   │       └── static/
│   │           ├── css/
│   │           │   └── style.css                       # CSS premium, responsive
│   │           └── js/
│   │               └── app.js                          # AJAX toggle, modal, search
│   │
│   └── test/
│       └── java/com/srtgroup/todo/
│           ├── service/
│           │   └── TodoServiceTest.java                # Unit test service (Mockito)
│           ├── controller/
│           │   └── TodoApiControllerTest.java          # Integration test API (@SpringBootTest)
│           └── repository/
│               └── TodoRepositoryTest.java             # Repository test (@DataJpaTest)
│
├── Dockerfile                                          # Build Docker image
├── docker-compose.yml                                  # Chạy app bằng Docker
├── pom.xml                                             # Maven dependencies
└── README.md                                           # Hướng dẫn cài đặt & chạy
```

---

## 🗃️ Database Schema

### Bảng `todos`

| Column | Type | Constraint | Mô tả |
|---|---|---|---|
| `id` | BIGINT | PK, AUTO_INCREMENT | Khóa chính |
| `title` | VARCHAR(255) | NOT NULL | Tiêu đề công việc |
| `description` | TEXT | NULLABLE | Mô tả chi tiết |
| `status` | VARCHAR(20) | NOT NULL, DEFAULT 'TODO' | TODO / IN_PROGRESS / DONE |
| `priority` | VARCHAR(10) | NOT NULL, DEFAULT 'MEDIUM' | LOW / MEDIUM / HIGH / URGENT |
| `deadline` | DATETIME | NULLABLE | Thời hạn hoàn thành |
| `created_at` | DATETIME | NOT NULL | Thời gian tạo (auto) |
| `updated_at` | DATETIME | NOT NULL | Thời gian cập nhật cuối (auto) |

---

## 🔌 REST API Endpoints

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/todos` | Lấy danh sách (có filter, search, phân trang) |
| `GET` | `/api/todos/{id}` | Lấy chi tiết 1 todo |
| `POST` | `/api/todos` | Tạo todo mới |
| `PUT` | `/api/todos/{id}` | Cập nhật todo |
| `PATCH` | `/api/todos/{id}/toggle` | Toggle trạng thái hoàn thành |
| `DELETE` | `/api/todos/{id}` | Xóa todo |

### Query Parameters cho `GET /api/todos`

```
?search=từ_khóa        — Tìm kiếm theo tiêu đề/mô tả
?status=TODO           — Lọc theo trạng thái (TODO / IN_PROGRESS / DONE)
?priority=HIGH         — Lọc theo độ ưu tiên
?page=0&size=10        — Phân trang
?sort=deadline,asc     — Sắp xếp
```

---

## 🎨 UI/UX Design

### Layout trang chính
```
┌──────────────────────────────────────────────┐
│  📝 TodoApp                    [+ Thêm mới]  │
├──────────────────────────────────────────────┤
│  [🔍 Tìm kiếm...]  [Trạng thái ▼] [Sort ▼] │
├──────────────────────────────────────────────┤
│  📊 Tổng: 12   Đang làm: 5   Hoàn thành: 6  │
│              Quá hạn: 1                      │
├──────────────────────────────────────────────┤
│ ┌──────────┐ ┌──────────┐ ┌──────────┐      │
│ │● URGENT  │ │● HIGH    │ │● MEDIUM  │      │
│ │Tiêu đề 1 │ │Tiêu đề 2 │ │Tiêu đề 3 │      │
│ │Mô tả...  │ │Mô tả...  │ │Mô tả...  │      │
│ │📅 31/12  │ │📅 15/01  │ │📅 20/01  │      │
│ │[✓][✏️][🗑]│ │[✓][✏️][🗑]│ │[✓][✏️][🗑]│      │
│ └──────────┘ └──────────┘ └──────────┘      │
│         [< 1  2  3  >]                       │
└──────────────────────────────────────────────┘
```

### Color Scheme (Dark Mode)
```
Background:   #0f0f1a  (navy dark)
Card:         #1a1a2e  (dark blue)
Accent:       #6c63ff  (indigo/purple)
Accent 2:     #00d4ff  (cyan)
Success:      #00c896  (green)
Warning:      #ffb347  (orange)
Danger:       #ff6b6b  (red)
Text:         #e8e8f0  (light gray)
```

---

## 🔒 Xử lý dữ liệu không hợp lệ

| Trường hợp | Cách xử lý |
|---|---|
| Tiêu đề rỗng | `@NotBlank` + message tiếng Việt |
| Tiêu đề quá dài (> 255 ký tự) | `@Size(max=255)` |
| Deadline là ngày quá khứ | Custom validator `@FutureOrPresent` |
| Status không hợp lệ | `@Enum` validator tùy chỉnh |
| ID không tồn tại | Ném `TodoNotFoundException` → HTTP 404 |
| Request body null/malformed | `@ControllerAdvice` bắt `MethodArgumentNotValidException` |
| Lỗi server không xác định | Global handler → HTTP 500 + response chuẩn |

### Cấu trúc Error Response thống nhất
```json
{
  "timestamp": "2024-12-01T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Tiêu đề không được để trống",
  "path": "/api/todos"
}
```

---

## 🧪 Unit Test Plan

### `TodoServiceTest.java` (Unit - dùng Mockito)
- `createTodo_withValidData_shouldReturnTodoResponse()`
- `createTodo_withBlankTitle_shouldThrowValidationException()`
- `updateTodo_withNonExistentId_shouldThrowNotFoundException()`
- `toggleStatus_fromTodoToDone_shouldUpdateStatusCorrectly()`
- `deleteTodo_withValidId_shouldCallRepositoryDelete()`
- `getTodos_withStatusFilter_shouldReturnFilteredList()`
- `getTodos_withSearchKeyword_shouldReturnMatchingList()`

### `TodoApiControllerTest.java` (Integration - dùng @SpringBootTest)
- `POST /api/todos` — 201 Created với body hợp lệ
- `POST /api/todos` với title rỗng — 400 Bad Request
- `GET /api/todos?status=DONE` — filter đúng trạng thái
- `GET /api/todos?search=keyword` — tìm kiếm đúng
- `DELETE /api/todos/9999` — 404 Not Found
- `PATCH /api/todos/{id}/toggle` — toggle đúng trạng thái

### `TodoRepositoryTest.java` (Repository - dùng @DataJpaTest)
- `findByStatus_shouldReturnCorrectTodos()`
- `findByTitleContainingIgnoreCase_shouldReturnMatchingTodos()`
- `save_shouldAutoSetCreatedAt()`

---

## 🐳 Docker Setup

### Dockerfile
```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/todo-app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### docker-compose.yml
```yaml
version: '3.8'
services:
  todo-app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=default
```

---

## 📅 Lịch trình thực hiện (1 buổi tối ~5 giờ)

```
19:00 — 20:30  Giai đoạn 1: Setup & Backend Core (1.5h)
               ├── Khởi tạo Spring Boot project (Maven)
               ├── Tạo Entity, Repository, DTO, Enums
               ├── Viết Service + Exception handling
               └── Test API bằng cURL / Postman

20:30 — 22:00  Giai đoạn 2: Frontend UI (1.5h)
               ├── Tạo Thymeleaf templates
               ├── Viết CSS premium (dark theme)
               └── Viết JS (AJAX toggle, modal, search)

22:00 — 22:45  Giai đoạn 3: Bonus features (45 phút)
               ├── Phân trang + sắp xếp
               ├── Seed data mẫu (data.sql)
               └── H2 Console config

22:45 — 23:30  Giai đoạn 4: Testing & Docker (45 phút)
               ├── Viết Unit Tests
               ├── Tạo Dockerfile + docker-compose
               └── Test toàn bộ luồng

23:30 — 24:00  Giai đoạn 5: README & Polish (30 phút)
               ├── Viết README.md chi tiết
               └── Review code, clean up, push GitHub
```

---

## 📝 README.md sẽ bao gồm

1. Giới thiệu dự án + ảnh chụp màn hình
2. Tech stack
3. Yêu cầu hệ thống (Java 17+, Maven 3.8+, Docker optional)
4. **Cách 1** — Chạy bằng Maven: `mvn spring-boot:run`
5. **Cách 2** — Chạy bằng Docker: `docker-compose up --build`
6. Truy cập H2 Console để xem database
7. API Documentation (bảng endpoint + ví dụ request/response)
8. Cấu trúc dự án giải thích
9. Hướng dẫn chạy tests: `mvn test`

---

*📅 Kế hoạch tạo ngày 05/07/2026 — Thực hiện tối cùng ngày*
*👤 Ứng viên: [Tên của bạn]*

