# Course Shop - Spring MVC + Thymeleaf

## Giới thiệu

Course Shop là ứng dụng web quản lý và bán khóa học trực tuyến được xây dựng bằng Spring Boot, Spring MVC và Thymeleaf.

## Công nghệ sử dụng

- **Backend Framework**: Spring Boot 3.5.3
- **View Template**: Thymeleaf
- **Security**: Spring Security (Form-based Authentication + JWT cho API)
- **Database**: MySQL 8
- **ORM**: Spring Data JPA (Hibernate)
- **Java Version**: 24
- **Build Tool**: Maven

## Các tính năng chính

### 1. Dành cho người dùng
- ✅ Xem danh sách khóa học
- ✅ Tìm kiếm và lọc khóa học theo chủ đề
- ✅ Xem chi tiết khóa học
- ✅ Đăng ký tài khoản và đăng nhập
- ✅ Thêm khóa học vào giỏ hàng
- ✅ Thanh toán khóa học (VNPay)
- ✅ Xem khóa học đã đăng ký
- ✅ Học tập và theo dõi tiến độ

### 2. Dành cho giảng viên
- ✅ Quản lý khóa học của mình
- ✅ Tạo, sửa, xóa khóa học
- ✅ Thêm module và video bài giảng
- ✅ Xem thống kê học viên

### 3. Dành cho quản trị viên
- ✅ Quản lý người dùng
- ✅ Quản lý chủ đề khóa học
- ✅ Xem báo cáo thống kê

## Cấu trúc dự án

```
Course-Shop/
├── src/
## Cấu hình bổ sung

### Cloudinary (Upload hình ảnh)
```properties
cloudinary.cloud.name=your_cloud_name
cloudinary.api.key=your_api_key
cloudinary.api.secret=your_api_secret
```

### Email (Gmail SMTP)
```properties
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
```

### VNPay (Thanh toán)
```properties
vnpay.tmn-code=your_tmn_code
vnpay.hash-secret=your_hash_secret
```

## Kiến trúc ứng dụng

### Hybrid Architecture
Dự án này sử dụng kiến trúc kết hợp:
1. **Spring MVC + Thymeleaf**: Cho giao diện web người dùng
2. **REST API + JWT**: Cho mobile app hoặc external clients

### Security Configuration
- Web pages: Form-based authentication với session
- REST API: JWT Bearer token authentication
- CSRF protection: Enabled cho web, disabled cho API

## Phát triển

### Hot Reload
Dự án đã cấu hình Spring DevTools để hỗ trợ hot reload:
```properties
spring.thymeleaf.cache=false
```

### Database Migration
Sử dụng Hibernate auto DDL:
```properties
spring.jpa.hibernate.ddl-auto=update
```
⚠️ Đổi sang `validate` trong production

## Testing
```bash
mvn test
```

## Deployment

### Build for production
```bash
mvn clean package -DskipTests
```

### Chạy production build
```bash
java -jar target/Course-Shop-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## Troubleshooting

### Lỗi kết nối database
- Kiểm tra MySQL đã chạy
- Kiểm tra username/password trong application.properties
- Kiểm tra database đã được tạo

### Lỗi port đã được sử dụng
Thay đổi port trong application.properties:
```properties
server.port=8080
```

## Đóng góp
Mọi đóng góp đều được chào đón! Vui lòng tạo issue hoặc pull request.

## License
MIT License

## Liên hệ
- Email: codemaster3979@gmail.com
│   ├── main/
│   │   ├── java/
│   │   │   └── com/jungle/courseshop/
│   │   │       ├── config/          # Cấu hình (Security, JWT, etc.)
│   │   │       ├── controller/
│   │   │       │   ├── web/         # MVC Controllers (Thymeleaf)
│   │   │       │   └── *Controller  # REST API Controllers (JSON)
│   │   │       ├── dto/             # Data Transfer Objects
│   │   │       ├── entity/          # JPA Entities
│   │   │       ├── exception/       # Exception Handlers
│   │   │       ├── repository/      # Spring Data Repositories
│   │   │       ├── service/         # Business Logic
│   │   │       └── validation/      # Custom Validators
│   │   └── resources/
│   │       ├── templates/           # Thymeleaf Templates
│   │       │   ├── auth/           # Login, Register pages
│   │       │   ├── courses/        # Course pages
│   │       │   ├── cart/           # Shopping cart
│   │       │   ├── my-courses/     # User's enrolled courses
│   │       │   ├── lecturer/       # Lecturer pages
│   │       │   └── layout.html     # Base layout
│   │       ├── static/             # CSS, JS, Images
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Cài đặt và chạy ứng dụng

### Yêu cầu hệ thống
- Java 24 hoặc cao hơn
- Maven 3.6+
- MySQL 8.0+

### Các bước cài đặt

1. **Clone repository**
```bash
git clone <repository-url>
cd Course-Shop
```

2. **Tạo database MySQL**
```sql
CREATE DATABASE course_shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **Cấu hình database**
Mở file `src/main/resources/application.properties` và cập nhật thông tin:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/course_shop
spring.datasource.username=root
spring.datasource.password=your_password
```

4. **Build project**
```bash
mvn clean install
```

5. **Chạy ứng dụng**
```bash
mvn spring-boot:run
```

Hoặc chạy file JAR:
```bash
java -jar target/Course-Shop-0.0.1-SNAPSHOT.jar
```

6. **Truy cập ứng dụng**
- Web UI: http://localhost:3979
- Swagger API Docs: http://localhost:3979/swagger-ui.html

## Tài khoản mặc định

### Admin
- Username: `admin`
- Password: `Admin@123`

## API Endpoints

### Web Pages (MVC)
- `GET /` - Trang chủ
- `GET /courses` - Danh sách khóa học
- `GET /courses/{id}` - Chi tiết khóa học
- `GET /auth/login` - Đăng nhập
- `GET /auth/register` - Đăng ký
- `GET /cart` - Giỏ hàng (yêu cầu đăng nhập)
- `GET /my-courses` - Khóa học của tôi (yêu cầu đăng nhập)
- `GET /lecturer/courses` - Quản lý khóa học (giảng viên)

### REST API
- `POST /api/auth/login` - Đăng nhập (JWT)
- `POST /api/auth/register` - Đăng ký
- `GET /api/public/courses` - Danh sách khóa học
- `GET /api/public/course/{id}` - Chi tiết khóa học
- `POST /api/courses` - Tạo khóa học (giảng viên)
- `GET /api/courses/enrolled` - Khóa học đã đăng ký


