# RAG (Retrieval-Augmented Generation) Integration

## 📚 Tổng quan

Hệ thống RAG được tích hợp vào Course Shop để cung cấp chatbot AI thông minh có khả năng:
- Tìm kiếm và gợi ý khóa học phù hợp
- Trả lời câu hỏi về giá cả, nội dung khóa học
- Hỗ trợ thông tin đăng ký, thanh toán
- Tư vấn chính sách giảm giá

## 🏗️ Kiến trúc RAG

### 1. **Intent Classification** 
Sử dụng Google Gemini AI để phân loại ý định người dùng:
- `COURSE_SEARCH`: Tìm kiếm khóa học
- `COURSE_RECOMMEND`: Gợi ý khóa học
- `PRICING_INFO`: Thông tin giá
- `DISCOUNT_POLICY`: Chính sách giảm giá
- `ENROLLMENT_INFO`: Thông tin đăng ký
- `PLATFORM_INFO`: Thông tin nền tảng
- `GENERAL_CHAT`: Chat chung

### 2. **Vector Embeddings**
- **Embedding Model**: Transformers (all-MiniLM-L6-v2) - Local ONNX
- **Vector Store**: SimpleVectorStore (In-Memory)
- **Dimensions**: 384

### 3. **RAG Pipeline**
```
User Query → Intent Classification → Context Retrieval → Response Generation
                                          ↓
                            (Semantic Search + Database Query)
```

## 🚀 Cấu hình

### 1. Thêm GEMINI_API_KEY vào `.env`

```env
GEMINI_API_KEY=your_gemini_api_key_here
```

### 2. Database Migration

Các bảng mới sẽ được tự động tạo khi khởi động:
- `rag_chat_session`: Lưu session chat
- `rag_chat_message`: Lưu lịch sử chat

## 📦 Dependencies mới

Đã thêm vào `pom.xml`:
```xml
<spring-ai.version>1.1.2</spring-ai.version>

<!-- Spring AI Dependencies -->
- spring-ai-starter-model-google-genai
- spring-ai-starter-model-transformers
- spring-ai-vector-store-simple
- spring-ai-rag
```

## 🔧 Cấu trúc Code

### DTOs
- `RagChatRequest`: Request chat
- `RagChatResponse`: Response chat
- `IntentResult`: Kết quả phân loại intent

### Entities
- `RagChatSession`: Session chat
- `RagChatMessage`: Message chat

### Services
- `IntentClassifierService`: Phân loại intent
- `CourseEmbeddingService`: Quản lý embeddings
- `RagChatService`: Orchestrator RAG pipeline

### Controllers
- `RagChatController`: REST API endpoint

### Config
- `AiConfig`: Cấu hình Vector Store
- `RagDataInitializer`: Khởi tạo embeddings khi startup

## 🎯 API Endpoints

### 1. Chat với RAG
```http
POST /api/rag/chat
Content-Type: application/json

{
  "message": "Tìm khóa học Java cho người mới bắt đầu",
  "sessionId": null,  // optional
  "userId": "user123"  // optional
}
```

**Response:**
```json
{
  "success": true,
  "sessionId": 1,
  "response": "Dựa trên yêu cầu của bạn, tôi gợi ý...",
  "intent": "COURSE_SEARCH"
}
```

### 2. Lấy lịch sử chat
```http
GET /api/rag/chat/history/{sessionId}
```

**Response:**
```json
[
  {
    "id": 1,
    "role": "USER",
    "content": "Tìm khóa học Java",
    "createdAt": "2026-02-25T10:30:00"
  },
  {
    "id": 2,
    "role": "ASSISTANT",
    "content": "Dựa trên yêu cầu...",
    "createdAt": "2026-02-25T10:30:05"
  }
]
```

## 🧪 Testing

### Các truy vấn mẫu để test:

1. **Tìm khóa học:**
   - "Tìm khóa học Java"
   - "Có khóa học về React không?"
   - "Khóa học Spring Boot cho người mới"

2. **Gợi ý khóa học:**
   - "Tôi muốn học lập trình web, nên bắt đầu từ đâu?"
   - "Gợi ý khóa học cho người mới bắt đầu"

3. **Hỏi giá:**
   - "Khóa học Java giá bao nhiêu?"
   - "Có khóa học nào dưới 500k không?"

4. **Chính sách:**
   - "Có giảm giá không?"
   - "Chính sách hoàn tiền như thế nào?"

5. **Thông tin đăng ký:**
   - "Làm sao để đăng ký khóa học?"
   - "Có chứng chỉ sau khi học không?"

## ⚡ Performance

### Vector Store: SimpleVectorStore
- **Ưu điểm**: 
  - Dễ setup, không cần database riêng
  - Phù hợp cho small-medium datasets
  - Tốc độ nhanh (in-memory)

- **Hạn chế**: 
  - Data lưu trong RAM
  - Phải rebuild embeddings mỗi lần restart
  - Không scale cho datasets lớn (>10k courses)

### Khuyến nghị Production
Để scale lên production với dataset lớn, nên chuyển sang:
- **Pinecone**: Cloud vector database
- **Weaviate**: Open-source vector database
- **Elasticsearch**: Với vector search plugin

## 🔄 Workflow

### 1. Khởi động ứng dụng
```
Application Start
    ↓
RagDataInitializer.initRagData()
    ↓
CourseEmbeddingService.embedAllCourses()
    ↓
Generate embeddings cho tất cả courses
    ↓
Store trong SimpleVectorStore
    ↓
Ready to serve requests
```

### 2. Xử lý Request
```
User sends message
    ↓
IntentClassifierService.classifyIntent()
    ↓
RagChatService.retrieveContext()
    ↓
Combine: Semantic Search + Database Query
    ↓
RagChatService.generateResponse()
    ↓
Return to user
```

## 📊 Monitoring

Xem logs để theo dõi:
```
INFO  RagDataInitializer - Starting RAG Data Initialization
INFO  CourseEmbeddingService - Generated embeddings for 50 courses
INFO  RagChatService - Detected intent: COURSE_SEARCH
DEBUG IntentClassifierService - Intent classification response: {...}
```

## 🔐 Security

- API endpoint `/api/rag/chat` không yêu cầu authentication (có thể thêm sau)
- CORS đang mở `*` - nên giới hạn trong production
- Rate limiting nên được thêm để tránh abuse

## 🎓 Ví dụ tích hợp Frontend

```javascript
// Chat với RAG
async function chatWithRAG(message, sessionId = null) {
  const response = await fetch('http://localhost:3979/api/rag/chat', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      message: message,
      sessionId: sessionId,
      userId: 'user123'
    })
  });
  
  const data = await response.json();
  return data;
}

// Sử dụng
const result = await chatWithRAG("Tìm khóa học Java");
console.log(result.response);
console.log("Session ID:", result.sessionId);
```

## 🐛 Troubleshooting

### Issue: Embeddings không được tạo
**Solution**: Kiểm tra logs, đảm bảo có courses active trong database

### Issue: Response chậm
**Solution**: 
- Transformers model download lần đầu có thể mất vài phút
- Giảm `topK` trong semantic search
- Cache results

### Issue: Out of Memory
**Solution**: 
- Tăng heap size: `-Xmx2g`
- Giảm số lượng embeddings
- Chuyển sang external vector store

## 📝 Lưu ý

1. **Lần chạy đầu tiên**: Model transformers sẽ được download (~90MB), mất vài phút
2. **Gemini API Key**: Cần có API key hợp lệ
3. **MySQL**: Chỉ lưu chat history, không lưu vectors
4. **SimpleVectorStore**: Rebuild embeddings mỗi lần restart

## 🔄 Updates & Migrations

Khi thêm khóa học mới, cần update embeddings:
```java
@Autowired
private CourseEmbeddingService embeddingService;

// Sau khi tạo course mới
embeddingService.embedCourse(newCourse);
```

## 📚 Tài liệu tham khảo

- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [Google Gemini API](https://ai.google.dev/)
- [Sentence Transformers](https://www.sbert.net/)
- [RAG Architecture](https://www.promptingguide.ai/techniques/rag)

---

**Developed with ❤️ by Course Shop Team**
