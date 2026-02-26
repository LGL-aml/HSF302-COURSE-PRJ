# RAG Integration Testing Guide

## 🎉 Hoàn thiện tích hợp RAG Chat Widget vào HSF302-COURSE-PRJ

### Các thay đổi đã thực hiện:

#### 1. **Loại bỏ AI Tools Page**
- ❌ Đã xóa `AIController.java`
- ❌ Loại bỏ link "AI Tools" từ navbar
- ❌ Loại bỏ link "AI Tools" từ user dropdown menu

#### 2. **Thêm RAG Chat Widget** (MỚI)
- Location: `templates/layout.html`
- Hiển thị: **Nút tròn nổi ở góc phải dưới cùng** trên mọi trang
- Chỉ hiện khi: User đã đăng nhập (`sec:authorize="isAuthenticated()"`)
- Tính năng:
  - Floating button (60x60) với gradient tím đẹp mắt
  - Chat window (380x600) với animations mượt mà
  - Session management tự động
  - Intent classification với badge icons
  - Typing indicator khi đang xử lý
  - Ctrl+Enter để gửi nhanh
  - Auto-resize textarea
  - Responsive design (mobile-friendly)

#### 3. **UI/UX Components**
- **Chat Button**: Nút tròn gradient tím, luôn hiển thị
- **Chat Window**: Cửa sổ chat hiện đại với:
  - Header: Avatar + tên bot + nút đóng
  - Messages Area: Tin nhắn bot/user với avatar
  - Input Area: Textarea + nút gửi
  - Footer: Session ID + nút làm mới

#### 4. **RAG Integration**
- Kết nối với API: `POST /api/rag/chat`
- Hiển thị Intent badge cho mỗi câu trả lời:
  - 🔍 Tìm kiếm khóa học
  - 💡 Gợi ý khóa học
  - 💰 Thông tin giá
  - 🎁 Chính sách giảm giá
  - 📝 Thông tin đăng ký
  - ℹ️ Thông tin nền tảng
  - 💬 Trò chuyện chung

---

## 🧪 Hướng dẫn kiểm tra

### Bước 1: Khởi động ứng dụng
```bash
cd HSF302-COURSE-PRJ
mvn spring-boot:run
```

### Bước 2: Kiểm tra Chat Widget xuất hiện
1. Mở trình duyệt: `http://localhost:8080`
2. **Chưa login**: Widget KHÔNG hiển thị
3. **Đăng nhập**: Widget xuất hiện ở góc phải dưới cùng (nút tròn 🤖)
4. **Click vào nút**: Chat window mở rộng

### Bước 3: Kiểm tra chức năng chat

#### Test Case 1: Tìm kiếm khóa học
```
Câu hỏi: "Tôi muốn học lập trình web, có khóa học nào phù hợp?"

Kết quả mong đợi:
✅ Hiển thị badge: 🔍 Tìm kiếm
✅ Trả lời với danh sách khóa học web phù hợp
✅ Tin nhắn user bên phải (gradient tím)
✅ Tin nhắn bot bên trái (background trắng)
```

#### Test Case 2: Thông tin giá cả
```
Câu hỏi: "Khóa học Spring Boot giá bao nhiêu?"

Kết quả mong đợi:
✅ Hiển thị badge: 💰 Giá cả
✅ Trả lời với thông tin giá cụ thể
```

#### Test Case 3: Gợi ý khóa học
```
Câu hỏi: "Tôi là người mới bắt đầu, nên học khóa học nào?"

Kết quả mong đợi:
✅ Hiển thị badge: 💡 Gợi ý
✅ Trả lời với gợi ý phù hợp cho người mới
```

#### Test Case 4: Session Management
```
1. Gửi vài câu hỏi
2. Click "🔄 Làm mới"
3. Kiểm tra:
   ✅ Session ID thay đổi
   ✅ Lịch sử chat reset
   ✅ Welcome message hiển thị lại
```

#### Test Case 5: UX Features
```
✅ Ctrl+Enter gửi tin nhắn
✅ Textarea tự động resize khi nhập nhiều dòng
✅ Scroll tự động đến tin nhắn mới nhất
✅ Typing indicator hiển thị khi đang xử lý
✅ Error message khi API lỗi
✅ Click ngoài widget không đóng (chỉ click [✕] hoặc nút chat)
```

### Bước 4: Kiểm tra trên nhiều trang
Widget phải hiển thị trên:
- ✅ Trang chủ `/`
- ✅ Trang khóa học `/courses`
- ✅ Chi tiết khóa học `/courses/{id}`
- ✅ Giỏ hàng `/cart`
- ✅ Profile `/profile`
- ✅ Mọi trang khác (khi đã login)

### Bước 5: Kiểm tra Responsive
1. **Desktop**: Widget 380px x 600px, vị trí cố định
2. **Mobile**: 
   - Resize trình duyệt xuống < 480px
   - Widget full width (trừ margin 20px mỗi bên)
   - Vẫn hiển thị đầy đủ chức năng

---

## 🎨 Giao diện Widget

### Minimal State (Chưa mở)
```
                              [🤖] ← Nút tròn gradient tím
                                     60x60px
                                     Góc phải dưới
```

### Expanded State (Đã mở)
```
                    ┌────────────────────────────┐
                    │ 🤖 Course Shop AI     [✕]  │
                    │    Trợ lý thông minh       │
                    ├────────────────────────────┤
                    │ 🤖 Xin chào! Tôi có thể... │
                    │                            │
                    │              💡 Gợi ý      │
                    │   Tôi muốn học web?     👤 │
                    │                            │
                    │ 🤖 🔍 Tìm kiếm             │
                    │    Dựa trên nhu cầu...     │
                    ├────────────────────────────┤
                    │ [Nhập câu hỏi...]     [➤]  │
                    ├────────────────────────────┤
                    │ Session: xxx  🔄 Làm mới   │
                    └────────────────────────────┘
                              [🤖]
```

---

## 🔍 API Endpoint

### POST `/api/rag/chat`
**Request:**
```json
{
  "sessionId": "session-1234567890-abc123",
  "userMessage": "Tôi muốn học lập trình web"
}
```

**Response:**
```json
{
  "sessionId": "session-1234567890-abc123",
  "userMessage": "Tôi muốn học lập trình web",
  "answer": "Dựa trên nhu cầu của bạn, tôi gợi ý...",
  "intent": "COURSE_SEARCH",
  "confidence": 0.95,
  "retrievedContext": [
    "Khóa học Spring Boot...",
    "Khóa học HTML/CSS..."
  ],
  "timestamp": "2025-01-15T10:30:00"
}
```

---

## 📊 Kiểm tra dữ liệu

### Database Tables
```sql
-- Check RAG sessions
SELECT * FROM rag_chat_sessions ORDER BY created_at DESC LIMIT 10;

-- Check RAG messages với intent
SELECT 
    session_id,
    role,
    intent,
    LEFT(content, 50) as message_preview,
    created_at
FROM rag_chat_messages 
ORDER BY created_at DESC 
LIMIT 20;

-- Intent distribution
SELECT intent, COUNT(*) as count 
FROM rag_chat_messages 
WHERE role = 'ASSISTANT' 
GROUP BY intent 
ORDER BY count DESC;
```

---

## ⚠️ Lưu ý quan trọng

### 1. Khởi động lần đầu
- RAG system sẽ tự động embed tất cả courses vào Vector Store (via `RagDataInitializer`)
- Kiểm tra log: `[RagDataInitializer] Embedded X courses into vector store`
- Lần đầu download ONNX model ~90MB (chậm), các lần sau nhanh hơn

### 2. Widget chỉ hiển thị khi login
- User chưa login → Widget KHÔNG hiển thị
- User đã login → Widget xuất hiện ở góc phải dưới
- Security: `sec:authorize="isAuthenticated()"`

### 3. Gemini API Key
- Đảm bảo `GEMINI_API_KEY` đã được set trong `application.properties`
- Nếu chưa có, lấy key miễn phí tại: https://aistudio.google.com/app/apikey

### 4. Browser Compatibility
- Yêu cầu modern browser (Chrome, Firefox, Edge, Safari mới nhất)
- CSS Grid, Flexbox, Animations
- Fetch API, ES6+ JavaScript

---

## 🎯 Kết quả mong đợi

### Vị trí Widget
```
┌─────────────────────────────────────────┐
│ Navbar                                  │
├─────────────────────────────────────────┤
│                                         │
│         Page Content                    │
│                                         │
│                                         │
│                                         │
│                                         │
│                                  [🤖]   │ ← Chat widget
├─────────────────────────────────────────┤
│ Footer                                  │
└─────────────────────────────────────────┘
```

### Trải nghiệm người dùng
1. **Landing**: User thấy nút 🤖 ở góc phải dưới
2. **Hover**: Nút phóng to 1.1x, shadow tăng
3. **Click**: Chat window mở với animation slideUp
4. **Type**: Textarea tự resize, placeholder rõ ràng
5. **Send**: Typing indicator hiển thị → Bot reply
6. **Intent**: Badge màu sắc (🔍 💡 💰 🎁 📝 ℹ️ 💬)
7. **Scroll**: Tự động scroll đến tin nhắn mới
8. **Close**: Click [✕] hoặc nút 🤖 để đóng

---

## ✅ Checklist hoàn thành

- [x] Xóa AIController.java
- [x] Loại bỏ AI Tools links từ navbar
- [x] Loại bỏ AI Tools link từ user dropdown
- [x] Thêm chat widget HTML vào layout.html
- [x] CSS styling với gradient đẹp mắt
- [x] JavaScript logic đầy đủ
- [x] Kết nối /api/rag/chat API
- [x] Session management
- [x] Intent badge display (7 loại)
- [x] Typing indicator animation
- [x] Error handling
- [x] Responsive design (mobile)
- [x] XSS prevention (escapeHtml)
- [x] Authentication check (sec:authorize)
- [x] Ctrl+Enter shortcut
- [x] Auto-resize textarea
- [x] Auto-scroll messages
- [x] Welcome message
- [x] Avatar cho bot/user

---

## 🚀 Kết luận

RAG Chat Widget đã được **tích hợp hoàn chỉnh** vào HSF302-COURSE-PRJ!

**Điểm mạnh:**
- ✅ Luôn sẵn sàng trên mọi trang
- ✅ Giao diện đẹp, hiện đại, chuyên nghiệp
- ✅ UX mượt mà với animations
- ✅ Intent classification thông minh
- ✅ Vector search chính xác
- ✅ Session management đầy đủ
- ✅ Responsive mobile-friendly
- ✅ Secure & XSS-safe

**So sánh với phương án cũ (AI Tools Page):**
| Tiêu chí | AI Tools Page ❌ | Chat Widget ✅ |
|----------|------------------|----------------|
| Accessibility | Phải vào riêng trang /ai | Luôn sẵn sàng mọi trang |
| UX | Chuyển trang, reload | Instant, smooth |
| Context | Mất context khi chuyển trang | Giữ nguyên context |
| Engagement | Thấp (ẩn trong menu) | Cao (luôn hiển thị) |
| Mobile | Trang đầy đủ | Widget tối ưu |

**Các bước tiếp theo (tùy chọn):**
1. ✨ Thêm suggested questions (quick replies)
2. 📤 Upload file/image để chat
3. 🎤 Voice input/output
4. 🌐 Multi-language support
5. 📊 Analytics dashboard cho admin
6. 🧪 A/B testing widget variants
7. 🔔 Push notifications
8. 💾 Export chat history
9. 🤝 Human handoff khi cần
10. 🎨 Theme customization

**Hỗ trợ thêm:**
- Đọc [RAG_CHAT_WIDGET_GUIDE.md](RAG_CHAT_WIDGET_GUIDE.md) để hiểu chi tiết widget
- Đọc [RAG_README.md](src/main/java/com/jungle/courseshop/RAG_README.md) để hiểu kiến trúc RAG
- Đọc [RAG_SUMMARY.md](src/main/java/com/jungle/courseshop/RAG_SUMMARY.md) để xem tổng quan
- Đọc [QUICK_START.md](src/main/java/com/jungle/courseshop/QUICK_START.md) để biết cách sử dụng nhanh

**Enjoy your smart AI chatbot! 🤖💬✨**


---

## 🧪 Hướng dẫn kiểm tra

### Bước 1: Khởi động ứng dụng
```bash
cd HSF302-COURSE-PRJ
mvn spring-boot:run
```

### Bước 2: Truy cập trang AI Tools
1. Mở trình duyệt: `http://localhost:8080`
2. Đăng nhập với tài khoản bất kỳ
3. Click vào **"AI Tools"** trên navigation bar (hoặc trong user dropdown)
4. URL: `http://localhost:8080/ai`

### Bước 3: Kiểm tra RAG Chat

#### Test Case 1: Tìm kiếm khóa học
```
Câu hỏi: "Tôi muốn học lập trình web, có khóa học nào phù hợp?"
Kết quả mong đợi:
- Intent: 🔍 Tìm kiếm khóa học (COURSE_SEARCH)
- Answer: Danh sách khóa học liên quan đến web development
- Context: Thông tin chi tiết về các khóa học
```

#### Test Case 2: Thông tin giá cả
```
Câu hỏi: "Khóa học Spring Boot giá bao nhiêu?"
Kết quả mong đợi:
- Intent: 💰 Thông tin giá (PRICING_INFO)
- Answer: Giá của khóa học Spring Boot
- Context: Thông tin giá và khóa học
```

#### Test Case 3: Gợi ý khóa học
```
Câu hỏi: "Tôi là người mới bắt đầu, nên học khóa học nào?"
Kết quả mong đợi:
- Intent: 💡 Gợi ý khóa học (COURSE_RECOMMEND)
- Answer: Danh sách khóa học dành cho người mới
- Context: Thông tin về các khóa học cơ bản
```

#### Test Case 4: Chính sách giảm giá
```
Câu hỏi: "Có chương trình giảm giá nào không?"
Kết quả mong đợi:
- Intent: 🎁 Chính sách giảm giá (DISCOUNT_POLICY)
- Answer: Thông tin về chính sách giảm giá
```

#### Test Case 5: Thông tin đăng ký
```
Câu hỏi: "Làm thế nào để đăng ký khóa học?"
Kết quả mong đợi:
- Intent: 📝 Thông tin đăng ký (ENROLLMENT_INFO)
- Answer: Hướng dẫn đăng ký khóa học
```

#### Test Case 6: Thông tin nền tảng
```
Câu hỏi: "Course Shop là gì?"
Kết quả mong đợi:
- Intent: ℹ️ Thông tin nền tảng (PLATFORM_INFO)
- Answer: Giới thiệu về nền tảng Course Shop
```

### Bước 4: Kiểm tra Session Management
1. Click nút **"Tạo Session Mới"** → Kiểm tra Session ID mới được tạo
2. Gửi nhiều câu hỏi với cùng session → Kiểm tra lịch sử chat được lưu
3. Tạo session mới → Kiểm tra lịch sử cũ được reset

### Bước 5: Kiểm tra UI/UX
- ✅ RAG Chat section hiển thị đẹp mắt
- ✅ Loading state khi đang xử lý
- ✅ Hiển thị đầy đủ: Intent, Answer, Context, Metadata
- ✅ Nút "Xóa kết quả" hoạt động
- ✅ Ctrl+Enter gửi câu hỏi
- ✅ AI Chat và AI Tạo Quiz cũ vẫn giữ nguyên (chưa có backend)

---

## 🔍 API Endpoint

### POST `/api/rag/chat`
**Request:**
```json
{
  "sessionId": "session-1234567890-abc123",
  "userMessage": "Tôi muốn học lập trình web"
}
```

**Response:**
```json
{
  "sessionId": "session-1234567890-abc123",
  "userMessage": "Tôi muốn học lập trình web",
  "answer": "Dựa trên nhu cầu của bạn, tôi gợi ý...",
  "intent": "COURSE_SEARCH",
  "confidence": 0.95,
  "retrievedContext": [
    "Khóa học Spring Boot...",
    "Khóa học HTML/CSS..."
  ],
  "timestamp": "2025-01-15T10:30:00"
}
```

---

## 📊 Kiểm tra dữ liệu

### Database Tables
```sql
-- Check RAG sessions
SELECT * FROM rag_chat_sessions ORDER BY created_at DESC LIMIT 10;

-- Check RAG messages
SELECT * FROM rag_chat_messages ORDER BY created_at DESC LIMIT 20;

-- Count sessions and messages
SELECT 
    COUNT(DISTINCT session_id) as total_sessions,
    COUNT(*) as total_messages
FROM rag_chat_messages;
```

---

## ⚠️ Lưu ý quan trọng

### 1. Khởi động lần đầu
- RAG system sẽ tự động embed tất cả courses vào Vector Store (via `RagDataInitializer`)
- Kiểm tra log: `[RagDataInitializer] Embedded X courses into vector store`

### 2. AI Chat và AI Tạo Quiz cũ
- 2 tính năng này **CHƯA có backend** (API `/api/ai/chat` và `/api/ai/quiz/generate` chưa implement)
- Khi click "Gửi" sẽ báo lỗi 404 - Điều này là BÌNH THƯỜNG
- Chỉ RAG Chat mới hoạt động

### 3. Gemini API Key
- Đảm bảo `GEMINI_API_KEY` đã được set trong `application.properties`
- Nếu chưa có, lấy key miễn phí tại: https://aistudio.google.com/app/apikey

### 4. Performance
- Lần đầu tiên gọi API sẽ lâu hơn (do download ONNX model ~90MB)
- Các lần sau sẽ nhanh hơn (model được cache)

---

## 🎯 Kết quả mong đợi

### Navigation Bar
```
[Home] [Khóa học] [AI Tools 🤖] [Trở thành Giảng viên] | [Giỏ hàng] [User ▼]
                   ^^^^^^^^^^^^
                   (Màu xanh dương, chỉ hiện khi login)
```

### AI Tools Page
```
+------------------------------------------+
| AI Tools                  Yêu cầu đăng nhập |
+------------------------------------------+
| ┌─────────────┐ ┌─────────────┐         |
| │ AI Chat     │ │ AI Tạo Quiz │         |
| │ (Chưa hoạt  │ │ (Chưa hoạt  │         |
| │  động)      │ │  động)      │         |
| └─────────────┘ └─────────────┘         |
+------------------------------------------+
| ┌────────────────────────────────────┐  |
| │ 🤖 RAG Smart Chat                  │  |
| │ [Tìm kiếm thông minh với Vector    │  |
| │  Store]                            │  |
| │                                    │  |
| │ [Câu hỏi]                          │  |
| │ [Session ID: session-xxx]          │  |
| │ [Gửi câu hỏi] [Xóa kết quả]       │  |
| │                                    │  |
| │ Kết quả:                           │  |
| │ - Intent: 🔍 Tìm kiếm khóa học     │  |
| │ - Answer: ...                      │  |
| │ - Context: ...                     │  |
| │ - Metadata: ...                    │  |
| └────────────────────────────────────┘  |
+------------------------------------------+
```

---

## ✅ Checklist hoàn thành

- [x] Tạo AIController để serve /ai page
- [x] Thêm RAG Chat section vào ai/index.html
- [x] Tích hợp JavaScript để call /api/rag/chat
- [x] Cập nhật navigation menu (navbar + dropdown)
- [x] Giữ nguyên AI Chat và AI Tạo Quiz cũ
- [x] Session management với auto-generated ID
- [x] Hiển thị Intent, Answer, Context, Metadata
- [x] UI/UX đẹp mắt với Bootstrap 5
- [x] Loading state và error handling
- [x] Keyboard shortcut (Ctrl+Enter)

---

## 🚀 Kết luận

RAG system đã được **tích hợp hoàn chỉnh** vào HSF302-COURSE-PRJ!

**Điểm mạnh:**
- ✅ Tích hợp mượt mà với UI hiện có
- ✅ Session management đầy đủ
- ✅ Intent classification thông minh (7 loại)
- ✅ Vector search với Transformers embedding
- ✅ UI/UX trực quan, dễ sử dụng
- ✅ Error handling tốt

**Các bước tiếp theo (tùy chọn):**
1. Implement `/api/ai/chat` và `/api/ai/quiz/generate` cho 2 tính năng còn lại
2. Thêm chức năng export chat history
3. Thêm file upload để chat về document
4. Fine-tune intent classification model
5. Thêm analytics/metrics cho RAG usage

**Hỗ trợ thêm:**
- Đọc [RAG_README.md](../java/com/jungle/courseshop/RAG_README.md) để hiểu kiến trúc
- Đọc [RAG_SUMMARY.md](../java/com/jungle/courseshop/RAG_SUMMARY.md) để xem tổng quan
- Đọc [QUICK_START.md](../java/com/jungle/courseshop/QUICK_START.md) để biết cách sử dụng nhanh
