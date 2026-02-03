# 🎓 Course Shop - Hệ Thống Bán Khóa Học Lập Trình

## 📋 Mục Lục
- [Giới Thiệu](#giới-thiệu)
- [Tính Năng Chính](#tính-năng-chính)
- [Công Nghệ Sử Dụng](#công-nghệ-sử-dụng)
- [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
- [Hướng Dẫn Cài Đặt](#hướng-dẫn-cài-đặt)
- [Cấu Hình](#cấu-hình)
- [Hướng Dẫn Sử Dụng](#hướng-dẫn-sử-dụng)
- [Cấu Trúc Dự Án](#cấu-trúc-dự-án)
- [API Endpoints](#api-endpoints)

---

## 🌟 Giới Thiệu

**Course Shop** là một nền tảng học tập trực tuyến toàn diện, cho phép người dùng mua và học các khóa học lập trình chất lượng cao. Hệ thống được xây dựng với Spring Boot, tích hợp thanh toán trực tuyến, xác thực eKYC cho giảng viên, hệ thống quiz và chứng chỉ, cùng với AI hỗ trợ học tập thông minh.

### 🎯 Mục Tiêu

- Cung cấp nền tảng học tập lập trình chuyên nghiệp
- Kết nối học viên với giảng viên có chất lượng
- Tích hợp AI để hỗ trợ học tập hiệu quả
- Đảm bảo tính minh bạch và uy tín qua eKYC

---

## ✨ Tính Năng Chính

### 1. 🛒 Mua Khóa Học & Thanh Toán
- **Duyệt khóa học**: Xem danh sách khóa học với bộ lọc theo chủ đề, giá, độ khó
- **Chi tiết khóa học**: Xem thông tin chi tiết, giảng viên, nội dung, đánh giá
- **Giỏ hàng**: Thêm nhiều khóa học vào giỏ hàng
- **Thanh toán trực tuyến**: Tích hợp cổng thanh toán an toàn (VNPay, PayPal, Stripe)
- **Quản lý đơn hàng**: Theo dõi lịch sử mua hàng và trạng thái thanh toán
- **Email xác nhận**: Tự động gửi email sau khi thanh toán thành công

### 2. 👨‍🏫 Đăng Ký Làm Giảng Viên (eKYC)
- **Đăng ký giảng viên**: Form đăng ký với thông tin cá nhân và chuyên môn
- **Upload tài liệu**: 
  - Chứng minh nhân dân/Căn cước công dân (CCCD)
  - Bằng cấp, chứng chỉ liên quan
  - Ảnh chân dung
- **Xác thực eKYC**: 
  - Kiểm tra tính hợp lệ của CCCD
  - Xác thực danh tính qua AI
  - Validation trùng lặp CCCD (mỗi CCCD chỉ đăng ký 1 lần)
- **Duyệt hồ sơ**: 
  - Admin xem danh sách hồ sơ chờ duyệt
  - Xem chi tiết từng hồ sơ
  - Approve hoặc Reject với lý do
- **Thông báo kết quả**: Email tự động thông báo trạng thái duyệt
- **Quản lý khóa học**: Giảng viên tạo và quản lý khóa học của mình

### 3. 📝 Quiz & Chứng Chỉ
- **Hệ thống bài quiz**: 
  - Mỗi khóa học có nhiều bài quiz theo từng module
  - Các dạng câu hỏi: trắc nghiệm, code, điền khuyết
  - Giới hạn thời gian làm bài
  - Hiển thị kết quả chi tiết sau khi hoàn thành
- **Tiến độ học tập**: 
  - Theo dõi % hoàn thành khóa học
  - Đánh dấu bài học đã hoàn thành
  - Lưu lại điểm quiz
- **Chứng chỉ hoàn thành**:
  - Điều kiện: hoàn thành 100% khóa học + đạt điểm tối thiểu quiz
  - Chứng chỉ có mã xác thực duy nhất
  - Tải xuống PDF chứng chỉ
  - Email tự động gửi chứng chỉ
  - Xác minh chứng chỉ trực tuyến

### 4. 🤖 AI Hỗ Trợ Học Tập
- **Chatbot AI thông minh**:
  - Trả lời câu hỏi về nội dung khóa học
  - Giải thích code, debug lỗi
  - Gợi ý cách giải quyết bài tập
  - Hỗ trợ 24/7
- **Hỗ trợ quiz**:
  - Gợi ý hướng suy nghĩ (không đưa đáp án trực tiếp)
  - Giải thích lý thuyết liên quan
  - Ví dụ minh họa
- **Học tập cá nhân hóa**:
  - Phân tích điểm yếu của học viên
  - Đề xuất bài học ôn tập
  - Gợi ý khóa học phù hợp
- **Tích hợp Cerebras AI**: Sử dụng API AI tiên tiến cho phản hồi nhanh và chính xác

### 5. 🎯 Các Tính Năng Khác
- **Xác thực & Phân quyền**: 
  - Đăng ký/Đăng nhập bảo mật
  - OAuth2 (Google, Facebook)
  - Phân quyền: User, Lecturer, Admin
- **Quản trị hệ thống**:
  - Dashboard thống kê tổng quan
  - Quản lý người dùng, khóa học, chủ đề
  - Quản lý giảng viên và hồ sơ eKYC
  - Báo cáo doanh thu và hoạt động
- **Đánh giá & Phản hồi**:
  - Đánh giá khóa học (1-5 sao)
  - Bình luận và thảo luận
  - Báo cáo vấn đề
- **Responsive Design**: Giao diện thân thiện trên mọi thiết bị

---

## 🛠 Công Nghệ Sử Dụng

### Backend
- **Framework**: Spring Boot 3.5.3
- **Java Version**: Java 21
- **Database**: MySQL 8.0
- **ORM**: Spring Data JPA (Hibernate)
- **Security**: Spring Security + OAuth2
- **Template Engine**: Thymeleaf

### Frontend
- **HTML5, CSS3, JavaScript**
- **Bootstrap 5**: Responsive UI
- **Thymeleaf**: Server-side rendering
- **AJAX**: Dynamic content loading

### Third-party Services
- **Cloudinary**: Lưu trữ hình ảnh và video
- **Payment Gateway**: VNPay/PayPal/Stripe
- **Cerebras AI**: AI chatbot hỗ trợ học tập
- **Email Service**: SMTP (Gmail/SendGrid)
- **eKYC Service**: Xác thực danh tính

### Tools & Libraries
- **Maven**: Dependency management
- **Lombok**: Reduce boilerplate code
- **Docker**: Containerization
- **Git**: Version control

---

## 💻 Yêu Cầu Hệ Thống

### Bắt Buộc
- **Java**: JDK 21 trở lên
- **Maven**: 3.6+ 
- **MySQL**: 8.0+
- **Git**: Để clone repository

### Khuyến Nghị
- **IDE**: IntelliJ IDEA / Eclipse / VS Code
- **RAM**: 4GB trở lên
- **Disk Space**: 2GB cho project và dependencies
- **OS**: Windows 10/11, macOS, Linux

---

## 🚀 Hướng Dẫn Cài Đặt

### Bước 1: Clone Repository

```bash
git clone https://github.com/yourusername/course-shop.git
cd course-shop
```

### Bước 2: Cài Đặt MySQL

1. Tải và cài đặt MySQL Server 8.0+
2. Tạo database mới:

```sql
CREATE DATABASE course_shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. Tạo user (nếu cần):

```sql
CREATE USER 'courseshop_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON course_shop.* TO 'courseshop_user'@'localhost';
FLUSH PRIVILEGES;
```

### Bước 3: Cấu Hình Application

1. Mở file `src/main/resources/application.properties`
2. Cập nhật thông tin database:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/course_shop
spring.datasource.username=root
spring.datasource.password=your_password
```

3. Tạo file `.env` trong thư mục gốc và thêm các API keys:

```properties
CEREBRAS_API_KEY=your_cerebras_api_key
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
VNPAY_TMN_CODE=your_vnpay_code
VNPAY_HASH_SECRET=your_vnpay_secret
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_app_password
```

### Bước 4: Build Project

```bash
# Windows
mvnw.cmd clean install

# Linux/Mac
./mvnw clean install
```

### Bước 5: Chạy Ứng Dụng

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

Hoặc chạy trực tiếp file JAR:

```bash
java -jar target/Course-Shop-0.0.1-SNAPSHOT.jar
```

### Bước 6: Truy Cập Ứng Dụng

- **URL**: http://localhost:3979
- **Admin**: http://localhost:3979/admin
- **API Docs**: http://localhost:3979/swagger-ui.html (nếu được cấu hình)

---

## ⚙️ Cấu Hình

### Cloudinary (Lưu trữ File)

1. Đăng ký tài khoản tại: https://cloudinary.com
2. Lấy thông tin API từ Dashboard
3. Cập nhật vào `application.properties`:

```properties
cloudinary.cloud.name=your_cloud_name
cloudinary.api.key=your_api_key
cloudinary.api.secret=your_api_secret
```

### Cerebras AI (Chatbot)

1. Đăng ký API key tại: https://cerebras.ai
2. Thêm vào file `.env`:

```properties
CEREBRAS_API_KEY=your_api_key
```

### Email Configuration (Gmail)

1. Bật 2-Step Verification trong Google Account
2. Tạo App Password: https://myaccount.google.com/apppasswords
3. Cấu hình trong `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Payment Gateway

#### VNPay
1. Đăng ký merchant tại: https://vnpay.vn
2. Lấy TMN Code và Hash Secret
3. Cấu hình trong code

---

## 📖 Hướng Dẫn Sử Dụng

### 👤 Dành Cho Học Viên

#### 1. Đăng Ký & Đăng Nhập
1. Truy cập trang chủ: http://localhost:3979
2. Click **"Đăng ký"** ở góc trên phải
3. Điền thông tin: Email, Mật khẩu, Họ tên, Số điện thoại
4. Xác nhận email (kiểm tra hộp thư)
5. Đăng nhập với tài khoản đã tạo

#### 2. Mua Khóa Học
1. **Browse khóa học**:
   - Vào menu **"Khóa học"**
   - Dùng bộ lọc: Chủ đề, Giá, Độ khó, Đánh giá
   - Xem preview video và mô tả

2. **Thêm vào giỏ hàng**:
   - Click vào khóa học muốn mua
   - Xem chi tiết: Nội dung, giảng viên, đánh giá
   - Click **"Thêm vào giỏ hàng"**

3. **Thanh toán**:
   - Click icon giỏ hàng ở header
   - Kiểm tra danh sách khóa học
   - Click **"Thanh toán"**
   - Chọn phương thức: VNPay/PayPal/Stripe
   - Hoàn tất thanh toán
   - Nhận email xác nhận

#### 3. Học Tập
1. **Truy cập khóa học**:
   - Vào **"Khóa học của tôi"**
   - Click vào khóa học đã mua
   - Xem danh sách bài học

2. **Xem bài học**:
   - Click vào bài học để xem video
   - Đánh dấu hoàn thành sau khi xem xong
   - Tải tài liệu đính kèm (nếu có)

3. **Sử dụng AI Support**:
   - Click icon chatbot ở góc dưới phải
   - Hỏi câu hỏi về nội dung bài học
   - Nhờ giải thích code hoặc debug lỗi
   - AI sẽ trả lời ngay lập tức

#### 4. Làm Quiz
1. Sau khi hoàn thành module, click **"Làm Quiz"**
2. Đọc kỹ đề bài và thời gian
3. Chọn/Điền đáp án cho từng câu hỏi
4. Click **"Nộp bài"** khi hoàn thành
5. Xem kết quả và giải thích chi tiết
6. Có thể làm lại nếu chưa đạt

#### 5. Nhận Chứng Chỉ
1. Điều kiện:
   - Hoàn thành 100% bài học
   - Đạt điểm tối thiểu ở tất cả quiz (thường ≥ 70%)
2. Sau khi đủ điều kiện:
   - Vào **"Khóa học của tôi"** → chọn khóa học
   - Click **"Tải chứng chỉ"**
   - Chứng chỉ PDF tự động tải về
   - Nhận email có chứng chỉ đính kèm
3. Xác minh chứng chỉ: Dùng mã QR code trên chứng chỉ

---

### 👨‍🏫 Dành Cho Giảng Viên

#### 1. Đăng Ký Làm Giảng Viên
1. **Điều kiện**: Đã có tài khoản học viên
2. Vào **"Hồ sơ"** → Click **"Đăng ký làm giảng viên"**
3. **Điền thông tin**:
   - Họ tên đầy đủ
   - Số CCCD (validation trùng lặp)
   - Ngày sinh
   - Chuyên môn
   - Kinh nghiệm
   - Mô tả bản thân

4. **Upload tài liệu**:
   - **CCCD**: Mặt trước + Mặt sau (định dạng JPG/PNG, < 5MB)
   - **Chứng chỉ**: Bằng cấp, chứng chỉ liên quan
   - **Ảnh chân dung**: Ảnh rõ mặt để xác thực

5. **Xác thực eKYC**:
   - Hệ thống tự động kiểm tra tính hợp lệ của CCCD
   - Xác thực danh tính qua AI
   - Kiểm tra trùng lặp CCCD trong database

6. **Chờ duyệt**:
   - Hồ sơ được gửi đến Admin
   - Theo dõi trạng thái trong **"Trạng thái KYC"**
   - Nhận email thông báo kết quả (1-3 ngày làm việc)

#### 2. Tạo Khóa Học
1. Sau khi được duyệt, vào **"Dashboard Giảng viên"**
2. Click **"Tạo khóa học mới"**
3. **Thông tin cơ bản**:
   - Tên khóa học
   - Mô tả ngắn và chi tiết
   - Chủ đề/Danh mục
   - Độ khó: Beginner/Intermediate/Advanced
   - Giá bán
   - Ảnh thumbnail

4. **Nội dung khóa học**:
   - Tạo các Section (Chương)
   - Thêm Lecture (Bài học) vào mỗi Section
   - Upload video bài giảng (MP4, < 500MB)
   - Thêm tài liệu (PDF, PPT, code samples)
   - Viết mô tả cho mỗi bài

5. **Quiz**:
   - Tạo quiz cho mỗi Section
   - Thêm câu hỏi: Trắc nghiệm, Code, Tự luận
   - Đặt đáp án đúng và giải thích
   - Cấu hình thời gian làm bài và điểm đạt

6. **Review & Xuất bản**:
   - Preview khóa học
   - Kiểm tra lỗi chính tả, video
   - Click **"Xuất bản"** khi sẵn sàng

#### 3. Quản Lý Khóa Học
- **Dashboard**: Xem thống kê học viên, doanh thu, đánh giá
- **Chỉnh sửa**: Cập nhật nội dung bất cứ lúc nào
- **Trả lời câu hỏi**: Tương tác với học viên trong phần Q&A
- **Phân tích**: Xem report chi tiết về engagement

---

### 🛡️ Dành Cho Admin

#### 1. Đăng Nhập Admin
- URL: http://localhost:3979/admin
- Username: admin@courseshop.com
- Password: admin123 (đổi sau lần đăng nhập đầu)

#### 2. Quản Lý Hồ Sơ Giảng Viên (eKYC)
1. **Danh sách chờ duyệt**:
   - Vào **"Admin"** → **"Giảng viên"** → **"Hồ sơ chờ duyệt"**
   - Xem danh sách tất cả hồ sơ pending
   - Thông tin hiển thị: Họ tên, Email, Ngày nộp, CCCD

2. **Xem chi tiết hồ sơ**:
   - Click vào **"Xem chi tiết"** của từng hồ sơ
   - Xem đầy đủ thông tin:
     - Thông tin cá nhân
     - Ảnh CCCD (mặt trước/sau)
     - Ảnh chân dung
     - Chứng chỉ, bằng cấp
     - Mô tả kinh nghiệm
   - Kiểm tra tính hợp lệ và xác thực

3. **Approve/Reject**:
   - **Approve**: Click **"Phê duyệt"** nếu hồ sơ hợp lệ
   - **Reject**: Click **"Từ chối"** và nhập lý do cụ thể
   - Hệ thống tự động gửi email thông báo cho ứng viên

4. **Validation CCCD**:
   - Hệ thống tự động kiểm tra CCCD trùng lặp
   - Nếu CCCD đã được sử dụng → Từ chối tự động
   - Admin có thể xem lịch sử sử dụng CCCD

#### 3. Dashboard & Báo Cáo
- **Tổng quan**: Thống kê user, khóa học, doanh thu
- **Quản lý người dùng**: Xem, sửa, xóa, phân quyền
- **Quản lý khóa học**: Duyệt, ẩn/hiện khóa học
- **Quản lý chủ đề**: CRUD topics và categories
- **Báo cáo**: Export dữ liệu, phân tích xu hướng

---

## 📁 Cấu Trúc Dự Án

```
Course-Shop/
├── src/
│   ├── main/
│   │   ├── java/com/jungle/courseshop/
│   │   │   ├── config/              # Cấu hình Spring, Security
│   │   │   ├── controller/          # Controllers (MVC)
│   │   │   │   ├── admin/          # Admin controllers
│   │   │   │   ├── lecturer/       # Lecturer controllers
│   │   │   │   └── user/           # User controllers
│   │   │   ├── dto/                # Data Transfer Objects
│   │   │   ├── entity/             # JPA Entities
│   │   │   ├── repository/         # Spring Data JPA Repositories
│   │   │   ├── service/            # Business Logic
│   │   │   ├── exception/          # Custom Exceptions
│   │   │   ├── validation/         # Custom Validators
│   │   │   └── utils/              # Utility classes
│   │   │
│   │   └── resources/
│   │       ├── application.properties  # Cấu hình chính
│   │       ├── static/
│   │       │   ├── css/            # Stylesheets
│   │       │   ├── js/             # JavaScript files
│   │       │   └── images/         # Static images
│   │       └── templates/          # Thymeleaf templates
│   │           ├── admin/          # Admin pages
│   │           ├── lecturer/       # Lecturer pages
│   │           ├── courses/        # Course pages
│   │           ├── auth/           # Login/Register
│   │           ├── cart/           # Shopping cart
│   │           ├── payment/        # Payment pages
│   │           ├── my-courses/     # Student learning pages
│   │           ├── ai/             # AI chatbot interface
│   │           └── email/          # Email templates
│   │
│   └── test/                       # Unit & Integration tests
│
├── target/                         # Build output
├── .env                           # Environment variables (API keys)
├── pom.xml                        # Maven dependencies
├── Dockerfile                     # Docker configuration
└── README.md                      # This file
```

---

## 🔌 API Endpoints

### User Authentication
- `POST /api/auth/register` - Đăng ký tài khoản
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/logout` - Đăng xuất
- `GET /api/auth/verify-email?token={token}` - Xác nhận email

### Courses
- `GET /api/courses` - Lấy danh sách khóa học
- `GET /api/courses/{id}` - Chi tiết khóa học
- `GET /api/courses/search?q={keyword}` - Tìm kiếm khóa học
- `GET /api/courses/filter?topic={topic}&price={price}` - Lọc khóa học

### Cart & Payment
- `POST /api/cart/add` - Thêm vào giỏ hàng
- `GET /api/cart` - Xem giỏ hàng
- `DELETE /api/cart/{id}` - Xóa khỏi giỏ hàng
- `POST /api/payment/create` - Tạo thanh toán
- `GET /api/payment/callback` - Callback sau thanh toán

### Lecturer KYC
- `POST /api/lecturer/register` - Đăng ký làm giảng viên
- `POST /api/lecturer/kyc/upload` - Upload tài liệu KYC
- `GET /api/lecturer/kyc/status` - Kiểm tra trạng thái KYC

### Learning
- `GET /api/my-courses` - Khóa học đã mua
- `GET /api/my-courses/{id}/learn` - Học khóa học
- `POST /api/my-courses/{id}/complete-lecture` - Đánh dấu hoàn thành
- `GET /api/quiz/{id}` - Lấy bài quiz
- `POST /api/quiz/{id}/submit` - Nộp bài quiz
- `GET /api/certificate/{courseId}` - Tải chứng chỉ

### AI Support
- `POST /api/ai/chat` - Chat với AI
- `POST /api/ai/explain-code` - Giải thích code
- `POST /api/ai/quiz-hint` - Gợi ý quiz

### Admin
- `GET /api/admin/dashboard` - Dashboard stats
- `GET /api/admin/lecturers/pending` - Hồ sơ chờ duyệt
- `GET /api/admin/lecturers/{id}` - Chi tiết giảng viên
- `POST /api/admin/lecturers/{id}/approve` - Phê duyệt
- `POST /api/admin/lecturers/{id}/reject` - Từ chối
- `GET /api/admin/users` - Quản lý users
- `GET /api/admin/courses` - Quản lý courses

---

## 🐛 Troubleshooting

### Lỗi Database Connection
```
Error: Unable to connect to database
```
**Giải pháp**:
- Kiểm tra MySQL đã chạy chưa
- Xác nhận username/password trong `application.properties`
- Kiểm tra database `course_shop` đã tồn tại

### Lỗi Port 3979 đã được sử dụng
```
Error: Port 3979 is already in use
```
**Giải pháp**:
- Đổi port trong `application.properties`: `server.port=8080`
- Hoặc kill process đang dùng port 3979

### Lỗi Upload File
```
Error: File size exceeds maximum limit
```
**Giải pháp**:
- Kiểm tra `spring.servlet.multipart.max-file-size` trong config
- Tăng giới hạn nếu cần thiết

### Lỗi AI Chatbot không hoạt động
```
Error: Cerebras API key invalid
```
**Giải pháp**:
- Kiểm tra API key trong file `.env`
- Đảm bảo API key còn hạn và có quota

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- Cerebras AI for the AI integration
- Cloudinary for media storage
- All contributors and testers

---

## 📈 Roadmap

### Version 2.0 (Coming Soon)
- [ ] Mobile App (React Native)
- [ ] Live streaming classes
- [ ] Discussion forums
- [ ] Peer-to-peer learning
- [ ] Gamification (badges, leaderboard)
- [ ] Multi-language support
- [ ] Advanced analytics dashboard
- [ ] Integration with more payment gateways

---

**Made with ❤️ by Group 2**

*Last updated: February 2026*

