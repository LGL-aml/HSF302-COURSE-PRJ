# 🤖 RAG Chat Widget - Hướng dẫn sử dụng

## Tổng quan

RAG Chat Widget là chatbot AI thông minh được tích hợp trực tiếp vào mọi trang của Course Shop. Widget hiển thị dưới dạng **nút tròn nổi ở góc phải dưới cùng** màn hình, cho phép người dùng tương tác với AI bất cứ lúc nào.

---

## ✨ Tính năng chính

### 1. **Giao diện đẹp mắt, hiện đại**
- Nút chat nổi (floating button) với gradient màu tím đẹp mắt
- Cửa sổ chat (chat window) responsive, animations mượt mà
- Avatar cho bot và người dùng
- Typing indicator khi đang xử lý
- Badge thông báo (có thể tùy chỉnh)

### 2. **Chức năng RAG thông minh**
- Tự động phân loại ý định câu hỏi:
  - 🔍 Tìm kiếm khóa học
  - 💡 Gợi ý khóa học
  - 💰 Thông tin giá cả
  - 🎁 Chính sách giảm giá
  - 📝 Thông tin đăng ký
  - ℹ️ Thông tin nền tảng
  - 💬 Trò chuyện chung

- Vector Search tìm ngữ cảnh phù hợp
- Trả lời dựa trên dữ liệu thực tế từ database
- Hiển thị Intent badge cho mỗi câu trả lời

### 3. **Quản lý Session**
- Tự động tạo Session ID cho mỗi cuộc trò chuyện
- Lưu lịch sử tin nhắn vào database
- Nút "Làm mới" để bắt đầu cuộc trò chuyện mới

### 4. **UX tối ưu**
- Ctrl+Enter để gửi tin nhắn nhanh
- Auto-resize textarea khi nhập nhiều dòng
- Scroll tự động đến tin nhắn mới nhất
- Error handling với thông báo rõ ràng
- Chỉ hiển thị khi user đã đăng nhập

---

## 🎨 Giao diện

### Nút Chat (Minimal State)
```
┌──────────┐
│    🤖    │  ← Nút tròn gradient tím,
└──────────┘    width: 60px, height: 60px
   (1)          Góc phải dưới: right: 20px, bottom: 20px
```

### Cửa sổ Chat (Expanded State)
```
┌─────────────────────────────────────┐
│ 🤖 Course Shop AI      [✕]          │ ← Header gradient
│    Trợ lý thông minh                │
├─────────────────────────────────────┤
│ 🤖 Xin chào! Tôi có thể giúp bạn:   │
│    • Tìm khóa học phù hợp          │ ← Messages
│    • Thông tin giá cả, giảm giá    │   Area
│    • Hướng dẫn đăng ký             │   (scrollable)
│                                     │
│                        💡 Gợi ý     │
│            Tôi muốn học lập trình  👤│ ← User msg
│                                     │
│ 🤖 🔍 Tìm kiếm                      │
│    Dựa trên nhu cầu của bạn...     │ ← Bot reply
├─────────────────────────────────────┤
│ [Nhập câu hỏi...]            [➤]   │ ← Input area
├─────────────────────────────────────┤
│ Session: session-xxx  🔄 Làm mới   │ ← Footer
└─────────────────────────────────────┘

Kích thước: 380px x 600px
Animation: slideUp 0.3s
```

---

## 🔧 Cấu trúc Code

### HTML Structure (trong layout.html)
```html
<div id="ragChatWidget" sec:authorize="isAuthenticated()">
    <!-- Chat Button (60x60 floating) -->
    <button id="ragChatButton" class="rag-chat-button">
        <i class="fas fa-robot"></i>
        <span class="chat-badge">1</span>
    </button>

    <!-- Chat Window (380x600) -->
    <div id="ragChatWindow" class="rag-chat-window">
        <div class="rag-chat-header">...</div>      <!-- Header -->
        <div id="ragChatMessages">...</div>         <!-- Messages -->
        <div class="rag-chat-input">...</div>       <!-- Input -->
        <div class="rag-chat-footer">...</div>      <!-- Footer -->
    </div>
</div>
```

### CSS Classes
```css
.rag-chat-button       → Nút tròn floating
.rag-chat-window       → Cửa sổ chat
.rag-chat-header       → Header với gradient
.rag-chat-messages     → Container messages
.chat-message          → Một tin nhắn
  .bot-message         → Tin nhắn từ bot
  .user-message        → Tin nhắn từ user
.message-avatar        → Avatar tròn
.message-content       → Nội dung tin nhắn
.message-text          → Text bubble
.message-intent        → Badge intent (🔍, 💡, ...)
.typing-indicator      → Animation "typing..."
.rag-chat-input        → Input area
.rag-chat-footer       → Footer với session info
```

### JavaScript Functions
```javascript
generateSessionId()    → Tạo session ID mới
sendMessage()          → Gửi tin nhắn đến /api/rag/chat
addMessage()           → Thêm tin nhắn vào UI
showTyping()           → Hiển thị typing indicator
removeTyping()         → Xóa typing indicator
escapeHtml()           → Escape HTML để tránh XSS
```

---

## 🚀 Cách sử dụng

### Cho người dùng cuối:

1. **Mở chat widget:**
   - Click vào nút 🤖 ở góc phải dưới cùng
   - Widget sẽ mở rộng thành cửa sổ chat

2. **Hỏi câu hỏi:**
   - Nhập câu hỏi vào ô "Nhập câu hỏi của bạn..."
   - Click nút ➤ hoặc nhấn Ctrl+Enter
   - Ví dụ:
     - "Tôi muốn học lập trình web"
     - "Khóa học Spring Boot giá bao nhiêu?"
     - "Có chương trình giảm giá không?"

3. **Xem kết quả:**
   - Bot sẽ phân tích câu hỏi (hiển thị Intent badge)
   - Trả lời dựa trên dữ liệu thực tế
   - Typing indicator hiển thị khi đang xử lý

4. **Tạo cuộc trò chuyện mới:**
   - Click nút "🔄 Làm mới" ở footer
   - Session mới được tạo, lịch sử reset

5. **Đóng widget:**
   - Click nút [✕] ở header
   - Hoặc click lại nút 🤖

---

## 🔌 API Integration

Widget gọi đến REST API:

**Endpoint:** `POST /api/rag/chat`

**Request:**
```json
{
  "sessionId": "session-1234567890-abc",
  "userMessage": "Tôi muốn học lập trình web"
}
```

**Response:**
```json
{
  "sessionId": "session-1234567890-abc",
  "userMessage": "Tôi muốn học lập trình web",
  "answer": "Dựa trên nhu cầu của bạn, tôi gợi ý...",
  "intent": "COURSE_SEARCH",
  "confidence": 0.95,
  "retrievedContext": ["...", "..."],
  "timestamp": "2026-02-25T10:30:00"
}
```

---

## 📱 Responsive Design

### Desktop (width > 480px)
- Chat window: 380px x 600px
- Vị trí: bottom: 80px, right: 0

### Mobile (width ≤ 480px)
- Chat window: calc(100vw - 40px) x 600px
- Vị trí: bottom: 80px, right: -10px
- Full width trừ margin 20px mỗi bên

---

## 🎯 Intent Types & Examples

| Intent | Icon | Ví dụ câu hỏi |
|--------|------|---------------|
| COURSE_SEARCH | 🔍 Tìm kiếm | "Tôi muốn học Java", "Khóa học về AI có không?" |
| COURSE_RECOMMEND | 💡 Gợi ý | "Tôi là người mới, nên học gì?", "Gợi ý khóa học backend" |
| PRICING_INFO | 💰 Giá cả | "Khóa học này giá bao nhiêu?", "Chi phí học Spring Boot?" |
| DISCOUNT_POLICY | 🎁 Giảm giá | "Có giảm giá không?", "Chính sách ưu đãi thế nào?" |
| ENROLLMENT_INFO | 📝 Đăng ký | "Làm sao để đăng ký?", "Cách mua khóa học?" |
| PLATFORM_INFO | ℹ️ Thông tin | "Course Shop là gì?", "Nền tảng hoạt động thế nào?" |
| GENERAL_CHAT | 💬 Trò chuyện | "Xin chào", "Cảm ơn", "Tạm biệt" |

---

## 🛠️ Tùy chỉnh

### Thay đổi màu sắc:
```css
/* Gradient chính */
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);

/* Có thể đổi thành: */
background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); /* Pink */
background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); /* Blue */
background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); /* Green */
```

### Thay đổi vị trí:
```css
#ragChatWidget {
    bottom: 20px;  /* Khoảng cách từ đáy */
    right: 20px;   /* Khoảng cách từ phải */
    
    /* Để chuyển sang góc trái: */
    /* left: 20px; */
    /* right: auto; */
}
```

### Thay đổi kích thước:
```css
.rag-chat-window {
    width: 380px;   /* Chiều rộng */
    height: 600px;  /* Chiều cao */
}
```

### Thêm welcome message mặc định:
```javascript
// Trong script, thay đổi nội dung ở chatMessages.innerHTML
<div class="message-text">
    👋 Xin chào! Tôi là AI Assistant...
    <!-- Nội dung tùy chỉnh -->
</div>
```

---

## 🔐 Bảo mật

### 1. Authentication
- Widget chỉ hiển thị với `sec:authorize="isAuthenticated()"`
- API `/api/rag/chat` yêu cầu user đã login

### 2. XSS Prevention
- Sử dụng `escapeHtml()` để escape user input
- Không render HTML trực tiếp từ user

### 3. Session Security
- Session ID chỉ dùng để tracking conversation
- Không chứa thông tin nhạy cảm
- Stored in database with user association

---

## 🐛 Debugging

### Widget không hiển thị?
1. Kiểm tra user đã login chưa (sec:authorize)
2. Kiểm tra console log lỗi JavaScript
3. Xem network tab có widget HTML được render không

### Không gửi được tin nhắn?
1. Kiểm tra API `/api/rag/chat` có hoạt động không (test qua Postman)
2. Xem console log lỗi fetch()
3. Kiểm tra CORS nếu deploy production

### Styling bị lỗi?
1. Kiểm tra Bootstrap 5 đã load chưa
2. Kiểm tra Font Awesome 6 cho icons
3. Clear cache trình duyệt

---

## 📊 Monitoring

### Metrics cần theo dõi:
- Số lượng sessions mỗi ngày
- Số tin nhắn trung bình mỗi session
- Intent distribution (intent nào được dùng nhiều nhất)
- Response time trung bình
- Error rate

### Database Queries:
```sql
-- Total sessions today
SELECT COUNT(*) FROM rag_chat_sessions 
WHERE DATE(created_at) = CURDATE();

-- Messages per session
SELECT session_id, COUNT(*) as msg_count 
FROM rag_chat_messages 
GROUP BY session_id 
ORDER BY msg_count DESC;

-- Intent distribution
SELECT intent, COUNT(*) as count 
FROM rag_chat_messages 
WHERE role = 'ASSISTANT' 
GROUP BY intent;
```

---

## ✅ Checklist triển khai

- [x] Xóa AIController.java
- [x] Loại bỏ AI Tools links từ navbar
- [x] Thêm chat widget HTML vào layout.html
- [x] Thêm CSS styling cho widget
- [x] Thêm JavaScript logic
- [x] Kết nối với /api/rag/chat API
- [x] Session management
- [x] Intent badge display
- [x] Typing indicator
- [x] Error handling
- [x] Responsive design
- [x] XSS prevention
- [x] Authentication check

---

## 🎉 Kết quả

**Chat widget đã được tích hợp hoàn chỉnh!**

✨ **Trải nghiệm người dùng:**
- Widget luôn sẵn sàng ở mọi trang
- Giao diện đẹp, hiện đại
- Tương tác nhanh, mượt mà
- RAG system thông minh, trả lời chính xác

🚀 **Lợi ích:**
- Tăng engagement với người dùng
- Hỗ trợ 24/7 tự động
- Giảm tải cho support team
- Cải thiện conversion rate

📈 **Mở rộng trong tương lai:**
- Thêm suggested questions
- Upload file/image để chat
- Voice input/output
- Multi-language support
- Analytics dashboard
- A/B testing variants
- Integration với CRM

---

## 📞 Hỗ trợ

Nếu gặp vấn đề, hãy:
1. Đọc lại RAG_README.md và RAG_SUMMARY.md
2. Kiểm tra console log và network tab
3. Test API trực tiếp qua Postman
4. Xem database cho session/message records
5. Check application logs cho RAG errors

**Happy Chatting! 🤖💬**
