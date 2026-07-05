# 📋 TodoApp — Quản lý công việc

> Bài Test Intern Developer — SRT Group  
> Ngôn ngữ: **Java 17** | Framework: **Spring Boot 3**

---

## Demo

Giao diện light theme với đầy đủ tính năng CRUD, phân trang, tìm kiếm và lọc.

---

## Tech Stack

| Thành phần | Công nghệ |
|---|---|
| **Ngôn ngữ** | Java 17 |
| **Framework** | Spring Boot 3.2.5 |
| **ORM** | Spring Data JPA + Hibernate |
| **Database** | H2 In-Memory (zero-config) |
| **Validation** | Spring Boot Validation (Bean Validation 3) |
| **Frontend** | Thymeleaf + Vanilla CSS + Vanilla JS |
| **Build** | Maven 3.9 |
| **Container** | Docker + Docker Compose |
| **Testing** | JUnit 5 + Mockito + MockMvc |

---

## Tính năng


- Hiển thị danh sách công việc (dạng card grid)
- Thêm công việc mới (có validation đầy đủ)
- Chỉnh sửa công việc (modal edit)
- Xóa công việc (có xác nhận)
- Đánh dấu hoàn thành / chưa hoàn thành (toggle 1 click)
- Tìm kiếm theo tên/mô tả + lọc theo trạng thái
- Phân trang (Pageable) + sắp xếp (nhiều tiêu chí)
- Responsive UI (mobile-friendly)
- Docker + Docker Compose
- Unit Tests (Service + Controller + Repository)

---

## Yêu cầu hệ thống

### Chạy bằng Maven (cách đơn giản nhất)
- Java 17+
- Maven 3.8+

### Chạy bằng Docker
- Docker Desktop

---

## Hướng dẫn chạy

### Cách 1: Maven (khuyên dùng)

```bash
git clone https://github.com/toantran-tech/ToDoList.git
cd ToDoList
mvn spring-boot:run
```

Truy cập: **http://localhost:8080**

---

### Cách 2: Build JAR rồi chạy

```bash
mvn clean package -DskipTests

java -jar target/todo-app.jar
```

---

### Cách 3: Docker Compose

```bash
docker-compose up --build

docker-compose up -d --build

docker-compose down
```

Truy cập: **http://localhost:8080**

---

## 🗄️ H2 Console (xem Database)

Sau khi chạy ứng dụng, truy cập:

```
URL:      http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:tododb
Username: sa
Password: (để trống)
```

---

## 🧪 Chạy Tests

```bash
mvn test

mvn test -Dtest=TodoServiceTest

mvn test -Dtest=TodoApiControllerTest

mvn test -Dtest=TodoRepositoryTest


```

---

##  REST API

Base URL: `http://localhost:8080/api/todos`

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/todos` | Lấy danh sách (hỗ trợ filter, search, phân trang) |
| `GET` | `/api/todos/{id}` | Lấy chi tiết 1 todo |
| `POST` | `/api/todos` | Tạo todo mới |
| `PUT` | `/api/todos/{id}` | Cập nhật todo |
| `PATCH` | `/api/todos/{id}/toggle` | Toggle trạng thái |
| `DELETE` | `/api/todos/{id}` | Xóa todo |
| `GET` | `/api/todos/stats` | Lấy thống kê |

### Query Parameters

```
GET /api/todos?search=keyword&status=TODO&priority=HIGH&page=0&size=10&sortBy=deadline&sortDir=asc
```

| Tham số | Giá trị | Mô tả |
|---|---|---|
| `search` | string | Tìm kiếm trong title/description |
| `status` | `TODO` / `IN_PROGRESS` / `DONE` | Lọc theo trạng thái |
| `priority` | `LOW` / `MEDIUM` / `HIGH` / `URGENT` | Lọc theo độ ưu tiên |
| `page` | số nguyên >= 0 | Trang (bắt đầu từ 0) |
| `size` | 1-100 | Số item mỗi trang |
| `sortBy` | `createdAt` / `deadline` / `title` / `priority` | Trường sắp xếp |
| `sortDir` | `asc` / `desc` | Chiều sắp xếp |

### Ví dụ Request/Response

**Tạo todo mới:**
```bash
curl -X POST http://localhost:8080/api/todos \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Học Spring Boot",
    "description": "Đọc tài liệu chính thức",
    "status": "TODO",
    "priority": "HIGH",
    "deadline": "2024-12-31T23:59:00"
  }'
```

**Response:**
```json
{
  "id": 1,
  "title": "Học Spring Boot",
  "description": "Đọc tài liệu chính thức",
  "status": "TODO",
  "priority": "HIGH",
  "deadline": "2024-12-31T23:59:00",
  "createdAt": "2024-07-05T21:00:00",
  "updatedAt": "2024-07-05T21:00:00"
}
```

**Error Response (400):**
```json
{
  "timestamp": "2024-07-05T21:00:00",
  "status": 400,
  "error": "Dữ liệu không hợp lệ",
  "message": "...",
  "path": "/api/todos",
  "fieldErrors": {
    "title": "Tiêu đề không được để trống"
  }
}
```

---

## Cấu trúc dự án

```
Todo_List_Application/
├── src/
│   ├── main/
│   │   ├── java/com/srtgroup/todo/
│   │   │   ├── TodoApplication.java          # Entry point
│   │   │   ├── controller/
│   │   │   │   ├── TodoController.java       # Thymeleaf controller
│   │   │   │   └── TodoApiController.java    # REST API controller
│   │   │   ├── service/
│   │   │   │   ├── TodoService.java          # Interface
│   │   │   │   └── TodoServiceImpl.java      # Business logic
│   │   │   ├── repository/
│   │   │   │   └── TodoRepository.java       # Spring Data JPA
│   │   │   ├── model/
│   │   │   │   └── Todo.java                 # JPA Entity
│   │   │   ├── dto/
│   │   │   │   ├── TodoRequest.java          # Input DTO (validation)
│   │   │   │   ├── TodoResponse.java         # Output DTO
│   │   │   │   └── PagedResponse.java        # Pagination wrapper
│   │   │   ├── enums/
│   │   │   │   ├── Status.java               # TODO, IN_PROGRESS, DONE
│   │   │   │   └── Priority.java             # LOW, MEDIUM, HIGH, URGENT
│   │   │   ├── exception/
│   │   │   │   ├── TodoNotFoundException.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── mapper/
│   │   │       └── TodoMapper.java           # Entity <-> DTO
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── data.sql                      # Seed data mẫu
│   │       ├── templates/index.html          # Thymeleaf UI
│   │       └── static/
│   │           ├── css/style.css             # Light theme CSS
│   │           └── js/app.js                 # Frontend JS
│   └── test/
│       └── java/com/srtgroup/todo/
│           ├── service/TodoServiceTest.java          # Unit tests (Mockito)
│           ├── controller/TodoApiControllerTest.java # Integration tests
│           └── repository/TodoRepositoryTest.java    # Repository tests
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## Xử lý dữ liệu không hợp lệ

| Trường hợp | Cách xử lý |
|---|---|
| Tiêu đề trống | `@NotBlank` → 400 Bad Request + message tiếng Việt |
| Tiêu đề > 255 ký tự | `@Size(max=255)` → 400 |
| ID không tồn tại | `TodoNotFoundException` → 404 Not Found |
| Request body null | `@Valid` → 400 với danh sách lỗi field |
| Sort field không hợp lệ | Whitelist validation → fallback về `createdAt` |
| Page/size không hợp lệ | Clamp về giá trị hợp lệ (0, 10) |
| Lỗi server | `GlobalExceptionHandler` → 500 + message thân thiện |

---

## 👤 Thông tin ứng viên

- **Họ tên**: Trần Văn Công Toàn
- **Email**: toan0974102841@gmail.com
- **GitHub**: https://github.com/toantran-tech
