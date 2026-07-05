
INSERT INTO todos (title, description, status, priority, deadline, created_at, updated_at) VALUES
('Nghiên cứu Spring Boot 3',
 'Đọc tài liệu chính thức, làm quen với các annotation mới trong Spring Boot 3',
 'IN_PROGRESS', 'HIGH',
 DATEADD('DAY', 3, CURRENT_TIMESTAMP),
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Viết unit test cho service layer',
 'Sử dụng JUnit 5 và Mockito để viết test coverage đạt >= 80%',
 'TODO', 'HIGH',
 DATEADD('DAY', 5, CURRENT_TIMESTAMP),
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Thiết kế giao diện UI',
 'Thiết kế giao diện dark mode cho ứng dụng Todo List, responsive trên mobile',
 'DONE', 'MEDIUM',
 DATEADD('DAY', -1, CURRENT_TIMESTAMP),
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Cài đặt Docker',
 'Viết Dockerfile và docker-compose.yml để containerize ứng dụng',
 'TODO', 'MEDIUM',
 DATEADD('DAY', 7, CURRENT_TIMESTAMP),
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Review code với team',
 'Tổ chức buổi code review, thảo luận về kiến trúc và best practices',
 'TODO', 'LOW',
 DATEADD('DAY', 10, CURRENT_TIMESTAMP),
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Nộp bài test intern',
 'Hoàn thiện source code, viết README chi tiết và nộp lên GitHub',
 'IN_PROGRESS', 'URGENT',
 DATEADD('DAY', 2, CURRENT_TIMESTAMP),
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Học Thymeleaf template',
 'Tìm hiểu Thymeleaf expressions, fragments, layouts và tích hợp với Spring MVC',
 'DONE', 'MEDIUM',
 DATEADD('DAY', -3, CURRENT_TIMESTAMP),
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Tối ưu hiệu năng database',
 'Thêm index vào các cột hay được query, kiểm tra explain plan',
 'TODO', 'LOW',
 DATEADD('DAY', 14, CURRENT_TIMESTAMP),
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Viết tài liệu API',
 'Tài liệu hóa tất cả REST endpoints bằng README hoặc Swagger',
 'TODO', 'MEDIUM',
 DATEADD('DAY', 6, CURRENT_TIMESTAMP),
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

('Xử lý bảo mật cơ bản',
 'Thêm input sanitization, CSRF protection và rate limiting',
 'TODO', 'HIGH',
 DATEADD('DAY', -2, CURRENT_TIMESTAMP),
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
