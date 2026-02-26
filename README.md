# 🎓 Course Shop - Hệ Thống Bán Khóa Học Lập Trình

## 📋 Mục Lục
- [Giới Thiệu](#giới-thiệu)
- [Tính Năng Chính](#tính-năng-chính)
  - [AI RAG System](#4-🤖-ai-rag-retrieval-augmented-generation-hỗ-trợ-học-tập)
- [Công Nghệ Sử Dụng](#công-nghệ-sử-dụng)
- [Yêu Cầu Hệ Thống](#yêu-cầu-hệ-thống)
- [Hướng Dẫn Cài Đặt](#hướng-dẫn-cài-đặt)
- [Cấu Hình](#cấu-hình)
  - [RAG System](#rag-system-ai-chat-widget)
- [Hướng Dẫn Sử Dụng](#hướng-dẫn-sử-dụng)
  - [Sử Dụng RAG Chat Widget](#3-sử-dụng-ai-rag-chat-widget)
- [Cấu Trúc Dự Án](#cấu-trúc-dự-án)
- [API Endpoints](#api-endpoints)
  - [RAG AI Chat](#rag-ai-chat-widget)

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

### 4. 🤖 AI RAG (Retrieval-Augmented Generation) Hỗ Trợ Học Tập

Hệ thống AI thông minh sử dụng công nghệ RAG kết hợp semantic search và rule-based intent classification để hỗ trợ học viên 24/7.

#### 🎯 Tính Năng RAG Chat Widget
- **Chat Widget Tương Tác**:
    - Giao diện chat đẹp mắt ở góc dưới phải màn hình (480x680px)
    - Gradient purple theme với hiệu ứng smooth
    - Responsive trên mọi thiết bị
    - Scroll tự động đến tin nhắn mới

- **Tìm Kiếm & Gợi Ý Khóa Học Thông Minh**:
    - Tìm khóa học theo tên, chủ đề, ngôn ngữ lập trình
    - Lọc theo giá: "Tìm khóa học dưới 200k", "Khóa học từ 100k đến 500k"
    - Gợi ý khóa học phù hợp dựa trên nhu cầu
    - Hiển thị kết quả dạng **HTML Course Cards** tương tác:
        - Hình ảnh khóa học với badge chủ đề
        - Tên khóa học, giảng viên, số học viên
        - **Giá tiền** định dạng VNĐ rõ ràng
        - Nút **"Chi tiết"** và **"Mua ngay"** trực tiếp trong chat

- **Intent Classification Thông Minh** (7 loại):
    - `COURSE_SEARCH`: Tìm kiếm khóa học theo keyword
    - `COURSE_RECOMMEND`: Gợi ý khóa học phù hợp
    - `PRICING_INFO`: Thông tin giá và lọc theo giá
    - `ENROLLMENT_INFO`: Hướng dẫn đăng ký và thanh toán
    - `PLATFORM_INFO`: Thông tin về nền tảng và giảng viên
    - `GENERAL_CHAT`: Chào hỏi và giới thiệu tính năng
    - Hệ thống tự động phân loại ý định người dùng để đưa ra câu trả lời chính xác

- **Semantic Search Vector Store**:
    - Sử dụng Transformers Embedding Model (all-MiniLM-L6-v2)
    - Vector dimension: 384
    - SimpleVectorStore lưu trữ in-memory cho tốc độ cao
    - Tìm kiếm ngữ nghĩa thông minh, không chỉ khớp từ khóa

- **Lọc Giá Thông Minh**:
    - Tự động trích xuất khoảng giá từ câu hỏi:
        - "dưới 200k" → maxPrice = 200,000 VNĐ
        - "trên 500k" → minPrice = 500,000 VNĐ
        - "từ 100k đến 500k" → minPrice & maxPrice
    - Lọc chính xác khóa học theo budget người dùng

- **Quản Lý Session**:
    - Mỗi người dùng có sessionId riêng
    - Lưu lịch sử chat để theo dõi hội thoại
    - Tự động lấy userId từ SecurityContext

- **Phản Hồi Cấu Trúc HTML**:
    - Course cards với hover effects đẹp mắt
    - Promo boxes, info boxes với icon và màu sắc
    - Numbered steps cho hướng dẫn từng bước
    - Support footer với hotline và email

#### 🔧 Công Nghệ RAG Stack
- **Spring AI 1.1.2**: Framework RAG chính
- **Transformers Embedding**: all-MiniLM-L6-v2 (384 dim)
- **SimpleVectorStore**: In-memory vector storage
- **Rule-based Intent Classifier**: Keyword matching không cần LLM API
- **MySQL Integration**: Truy vấn course data real-time

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
- **Email Service**: SMTP (Gmail/SendGrid)
- **eKYC Service**: Xác thực danh tính

### AI & RAG Stack
- **Spring AI 1.1.2**: RAG framework và orchestration
- **Transformers Embedding Model**: all-MiniLM-L6-v2 (Hugging Face)
- **Vector Store**: SimpleVectorStore (in-memory)
- **Intent Classifier**: Rule-based keyword matching
- **Response Generator**: Template-based với HTML formatting
- **Session Management**: Backend-managed chat sessions

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
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
VNPAY_TMN_CODE=your_vnpay_code
VNPAY_HASH_SECRET=your_vnpay_secret
SMTP_USERNAME=your_email@gmail.com
SMTP_PASSWORD=your_app_password
```

> **📝 Lưu ý:** RAG system không cần API key bên ngoài. Chỉ cấu hình các service cần thiết cho upload ảnh, thanh toán và email.

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

### RAG System (AI Chat Widget)

**Course Shop** sử dụng RAG (Retrieval-Augmented Generation) với các thành phần offline, không cần API key bên ngoài:

#### ✅ Đã Tự Động Cấu Hình
- **Spring AI 1.1.2**: Đã được cấu hình trong `pom.xml`
- **Transformers Embedding**: Model tự động download từ Hugging Face lần đầu chạy
- **Vector Store**: SimpleVectorStore chạy in-memory (không cần database riêng)
- **Intent Classifier**: Rule-based, không cần training
- **MySQL**: Sử dụng database chính của hệ thống

#### 🎯 Tính Năng Chính
- **Semantic Search**: Tìm kiếm khóa học thông minh theo ngữ nghĩa
- **Price Filtering**: Lọc khóa học theo khoảng giá tự động
- **Intent Classification**: 7 loại intent (COURSE_SEARCH, PRICING_INFO, COURSE_RECOMMEND, ENROLLMENT_INFO, PLATFORM_INFO, GENERAL_CHAT)
- **HTML Response**: Hiển thị course cards với giá, ảnh, và action buttons
- **Session Management**: Lưu lịch sử chat theo user

#### 🔧 Không Cần Cấu Hình Thêm
RAG system hoạt động ngay khi chạy ứng dụng. Không cần API key hay cấu hình phức tạp.

#### 📍 API Endpoint
- **POST** `/api/rag/chat`: Gửi tin nhắn chat
- Request body:
```json
{
  "message": "Tìm khóa học Java dưới 200k",
  "sessionId": "auto-generated-if-null"
}
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

3. **Sử dụng AI RAG Chat Widget**:
    - **Mở chat widget**: Click icon chatbot màu tím ở góc dưới phải
    - **Tìm khóa học**:
        - Gõ: "Tìm khóa học Java", "Có khóa học Python nào không?"
        - "Tìm khóa học dưới 200k", "Khóa học từ 100k đến 500k"
    - **Gợi ý khóa học**:
        - "Gợi ý khóa học cho người mới bắt đầu"
        - "Top khóa học phổ biến nhất"
    - **Xem giá và so sánh**:
        - "Giá khóa học React là bao nhiêu?"
        - "Khóa học nào rẻ nhất?"
    - **Hướng dẫn đăng ký**:
        - "Làm sao để đăng ký khóa học?"
        - "Thanh toán như thế nào?"
    - **Thông tin nền tảng**:
        - "Course Shop có những tính năng gì?"
        - "Liên hệ hỗ trợ"
    - **Tương tác với kết quả**:
        - Xem course cards với hình ảnh và giá tiền
        - Click nút **"Chi tiết"** để xem thông tin đầy đủ
        - Click nút **"Mua ngay"** để thêm vào giỏ hàng
    - **Tips**: 
        - Enter để gửi tin nhắn, Shift+Enter để xuống dòng
        - Chat widget tự động scroll đến tin nhắn mới
        - Lịch sử chat được lưu theo session

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
│   │   │   │   ├── RagConfig.java             # RAG system configuration
│   │   │   │   ├── SecurityConfig.java        # CSRF bypass cho /api/rag/**
│   │   │   │   └── ...
│   │   │   ├── controller/          # Controllers (MVC)
│   │   │   │   ├── admin/          # Admin controllers
│   │   │   │   ├── lecturer/       # Lecturer controllers
│   │   │   │   ├── user/           # User controllers
│   │   │   │   └── rag/            # 🆕 RAG Controllers
│   │   │   │       └── RagChatController.java  # Chat API endpoint
│   │   │   ├── dto/                # Data Transfer Objects
│   │   │   │   └── rag/            # 🆕 RAG DTOs
│   │   │   │       ├── IntentResult.java       # Intent classification result
│   │   │   │       ├── RagChatRequest.java     # Chat request DTO
│   │   │   │       └── RagChatResponse.java    # Chat response DTO
│   │   │   ├── entity/             # JPA Entities
│   │   │   │   └── rag/            # 🆕 RAG Entities
│   │   │   │       ├── RagChatSession.java     # Chat session entity
│   │   │   │       └── RagChatMessage.java     # Chat message entity
│   │   │   ├── repository/         # Spring Data JPA Repositories
│   │   │   │   └── rag/            # 🆕 RAG Repositories
│   │   │   │       ├── RagChatSessionRepository.java
│   │   │   │       └── RagChatMessageRepository.java
│   │   │   ├── service/            # Business Logic
│   │   │   │   └── rag/            # 🆕 RAG Services
│   │   │   │       ├── RagChatService.java     # Core RAG orchestration
│   │   │   │       ├── IntentClassifierService.java  # Intent classification
│   │   │   │       ├── VectorStoreService.java # Semantic search
│   │   │   │       └── CourseEmbeddingService.java   # Embedding generation
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
│   │           ├── chat/           # 🆕 RAG chat widget (in layout.html)
│   │           └── email/          # Email templates
│   │
│   └── test/                       # Unit & Integration tests
│
├── target/                         # Build output
├── .env                           # Environment variables (API keys)
├── pom.xml                        # Maven dependencies (Spring AI 1.1.2)
├── Dockerfile                     # Docker configuration
├── RAG_README.md                  # 🆕 RAG system documentation
├── RAG_SUMMARY.md                 # 🆕 RAG implementation summary
└── README.md                      # This file
```

### 🆕 RAG System Files (14 files created)

**Backend Components:**
1. `RagConfig.java` - Cấu hình Transformers embedding & vector store
2. `RagChatController.java` - REST API endpoint cho chat
3. `RagChatService.java` - Core RAG orchestration logic
4. `IntentClassifierService.java` - Rule-based intent classification
5. `VectorStoreService.java` - Semantic search với SimpleVectorStore
6. `CourseEmbeddingService.java` - Generate embeddings cho khóa học
7. `RagChatSession.java` - Entity lưu chat session
8. `RagChatMessage.java` - Entity lưu chat messages
9. `RagChatSessionRepository.java` - JPA repository cho sessions
10. `RagChatMessageRepository.java` - JPA repository cho messages
11. `IntentResult.java` - DTO chứa intent classification result
12. `RagChatRequest.java` - DTO cho chat request
13. `RagChatResponse.java` - DTO cho chat response

**Frontend Components:**
14. `layout.html` - Chat widget UI (480x680px gradient purple theme)

**Dependencies Added:**
- Spring AI 1.1.2 (spring-ai-bom, spring-ai-core, spring-ai-transformers)
- ONNX Runtime 1.18.0 (cho Transformers embedding)

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

### RAG AI Chat Widget
- `POST /api/rag/chat` - Gửi tin nhắn chat với RAG AI
    - Request body:
    ```json
    {
      "message": "Tìm khóa học Java dưới 200k",
      "sessionId": "optional-session-id"
    }
    ```
    - Response:
    ```json
    {
      "response": "<HTML course cards or text>",
      "sessionId": "generated-or-existing-session-id",
      "timestamp": "2024-01-01T12:00:00"
    }
    ```
- **Tính năng**:
    - Tìm kiếm khóa học theo keyword
    - Lọc theo giá (dưới/trên X VNĐ)
    - Gợi ý khóa học
    - Thông tin giá, đăng ký, nền tảng
    - Hiển thị HTML course cards với action buttons

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

### Lỗi RAG Chat Widget không hoạt động
```
Error: Failed to process chat message
```
**Giải pháp**:
- Kiểm tra MySQL database đang chạy và có dữ liệu khóa học
- Kiểm tra lỗi console: `java.lang.OutOfMemoryError` → tăng heap size: `-Xmx2g`
- Transformers model đang download lần đầu → chờ 1-2 phút
- Kiểm tra CSRF token nếu gặp 403 Forbidden
- Xóa cache và restart ứng dụng

### Lỗi Course Cards không hiển thị đúng
```
Error: BigDecimal format exception
```
**Giải pháp**:
- Đảm bảo course.price là BigDecimal trong database
- Kiểm tra formatCourseCard() method trong RagChatService
- Thử tìm kiếm với câu hỏi khác: "Tìm khóa học Python"

---

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

- **Spring Boot team** for the amazing framework
- **Spring AI** for RAG orchestration capabilities
- **Hugging Face** for Transformers embedding models
- **Cloudinary** for media storage
- **VNPay, PayPal, Stripe** for payment integration
- **MySQL** for robust database system
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
