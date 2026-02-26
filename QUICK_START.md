# 🚀 Quick Start Guide - RAG Chat System

## 📋 Prerequisites

- ✅ Java 21+
- ✅ MySQL running
- ✅ Maven installed
- ✅ Google Gemini API Key

## ⚡ 5-Minute Setup

### Step 1: Get Gemini API Key
1. Visit: https://ai.google.dev/
2. Click "Get API Key"
3. Copy your API key

### Step 2: Configure Environment
Edit `.env` file and add:
```env
GEMINI_API_KEY=your_api_key_here
```

### Step 3: Start Application
```bash
# Clean and build
mvn clean install -DskipTests

# Run application
mvn spring-boot:run
```

### Step 4: Wait for Initialization
Look for this in logs:
```
INFO  RagDataInitializer - Starting RAG Data Initialization
INFO  CourseEmbeddingService - Generated embeddings for X courses
INFO  RagDataInitializer - RAG Data Initialization Completed
```

**First time**: Model download ~2-3 minutes (~90MB)

### Step 5: Test!

#### Option A: Web UI (Recommended)
1. Open `rag-chat-demo.html` in browser
2. Click quick questions or type your own
3. See RAG in action! 🎉

#### Option B: cURL
```bash
curl -X POST http://localhost:3979/api/rag/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Tìm khóa học Java",
    "userId": "test-user"
  }'
```

#### Option C: Postman
1. Import `RAG_Chat_API.postman_collection.json`
2. Run any request
3. See results!

## 🎯 Quick Test Queries

### 1. Course Search
```
"Tìm khóa học Java"
"Có khóa học React không?"
```

### 2. Recommendations
```
"Tôi muốn học lập trình web, nên bắt đầu từ đâu?"
"Gợi ý khóa học cho người mới"
```

### 3. Pricing
```
"Khóa học Java giá bao nhiêu?"
"Có khóa học nào dưới 500k?"
```

### 4. Policies
```
"Có giảm giá cho sinh viên không?"
"Chính sách hoàn tiền như thế nào?"
```

## 🔧 Troubleshooting

### Issue: "Model downloading..."
**Fix**: Wait 2-3 minutes (first run only)

### Issue: "Connection refused"
**Fix**: Check if app is running on port 3979
```bash
# Check if port is in use
netstat -an | findstr 3979
```

### Issue: "GEMINI_API_KEY not found"
**Fix**: 
1. Check `.env` file exists
2. Verify API key is correct
3. Restart application

### Issue: "No courses found"
**Fix**: Make sure you have active courses in MySQL database

### Issue: Out of Memory
**Fix**: Increase heap size
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xmx2g"
```

## 📊 Expected Response

### Request
```json
{
  "message": "Tìm khóa học Java",
  "userId": "test-user"
}
```

### Response
```json
{
  "success": true,
  "sessionId": 1,
  "response": "Dựa trên yêu cầu của bạn, tôi tìm thấy các khóa học Java sau:\n\n1. Java Cơ Bản - 299.000 VNĐ\n   - Phù hợp: Người mới bắt đầu\n   - Đã có 150 học viên\n\n2. Spring Boot Framework - 599.000 VNĐ\n   - Phù hợp: Người có kiến thức Java\n   - Đã có 89 học viên\n\nBạn có muốn biết thêm chi tiết về khóa học nào không?",
  "intent": "COURSE_SEARCH"
}
```

## 🎨 Demo UI Features

### Quick Questions
Click any quick question button to test:
- 🔍 Tìm khóa học Java
- 💡 Khóa học cho người mới
- 💰 Khóa học dưới 500k
- 🎁 Chính sách giảm giá

### Chat History
All messages are saved to database. View history:
```bash
curl http://localhost:3979/api/rag/chat/history/1
```

### Session Management
- Sessions auto-created
- Session ID shown in UI
- Can resume conversations

## 📱 Integration Example

### JavaScript
```javascript
async function askRAG(question) {
  const response = await fetch('http://localhost:3979/api/rag/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      message: question,
      userId: 'user123'
    })
  });
  
  const data = await response.json();
  console.log(data.response);
  return data;
}

// Usage
const result = await askRAG("Tìm khóa học Java");
```

### React Component
```jsx
function ChatBot() {
  const [response, setResponse] = useState('');
  
  const handleAsk = async (question) => {
    const res = await fetch('http://localhost:3979/api/rag/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: question, userId: 'user123' })
    });
    const data = await res.json();
    setResponse(data.response);
  };
  
  return (
    <div>
      <button onClick={() => handleAsk("Tìm khóa học Java")}>
        Ask
      </button>
      <p>{response}</p>
    </div>
  );
}
```

## 🔍 Monitoring

### Check Application Logs
```bash
tail -f logs/spring.log
```

### Key Log Messages
```
✅ "Generated embeddings for X courses" - Embeddings created
✅ "Detected intent: COURSE_SEARCH" - Intent classified
✅ "Processing RAG chat request" - Request received
❌ "Error processing RAG chat request" - Something failed
```

## 🎯 Performance Tips

### 1. Fast Response
- Keep questions concise
- Use specific keywords

### 2. Better Results
- Ask specific questions
- Mention preferences (price, level, topic)

### 3. Context Awareness
- Use same sessionId for related questions
- AI remembers conversation history

## 📈 Next Steps After Testing

1. **Customize System Prompt**
   - Edit `RagChatService.SYSTEM_PROMPT`
   - Add your brand voice

2. **Add Authentication**
   - Secure endpoints
   - Track user sessions

3. **Deploy to Production**
   - Use external Vector Store (Pinecone)
   - Add monitoring & analytics
   - Implement rate limiting

4. **Enhance UI**
   - Integrate into main website
   - Add voice input
   - Show course cards in chat

## 🎓 Learning Resources

- [Spring AI Docs](https://docs.spring.io/spring-ai/reference/)
- [RAG Architecture](https://www.promptingguide.ai/techniques/rag)
- [Google Gemini API](https://ai.google.dev/docs)

## 💬 Support

Having issues?
1. Check logs: `logs/spring.log`
2. Review documentation: `RAG_README.md`
3. Run tests: `mvn test`

## ✅ Success Checklist

- [ ] Gemini API key configured
- [ ] Application started successfully
- [ ] Embeddings generated
- [ ] Demo UI working
- [ ] API responds correctly
- [ ] Chat history saved

If all checked ✅ - **You're ready to go!** 🎉

---

**Time to First Chat**: ~5 minutes  
**Technology**: Spring AI + Google Gemini + MySQL  
**Status**: Ready for Production Testing
