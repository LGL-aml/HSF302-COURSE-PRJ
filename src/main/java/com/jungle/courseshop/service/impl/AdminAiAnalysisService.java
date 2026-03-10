package com.jungle.courseshop.service.impl;

import com.jungle.courseshop.dto.response.AdminStatsResponse;
import com.jungle.courseshop.service.rag.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * AI Analysis Service for Admin Dashboard
 * Analyzes business stats and provides intelligent insights
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAiAnalysisService {

    private final GeminiService geminiService;

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    /**
     * Generate AI analysis from admin stats data
     */
    public String analyzeStats(AdminStatsResponse stats) {
        try {
            String systemPrompt = buildSystemPrompt();
            String dataPrompt = buildDataPrompt(stats);
            
            String aiResponse = geminiService.generateResponse(systemPrompt, dataPrompt, geminiApiKey);
            
            if (aiResponse != null && !aiResponse.isBlank()) {
                return markdownToHtml(aiResponse);
            }
            
            return buildFallbackAnalysis(stats);
        } catch (Exception e) {
            log.error("AI Analysis failed: {}", e.getMessage());
            return buildFallbackAnalysis(stats);
        }
    }

    private String buildSystemPrompt() {
        return """
            Bạn là AI Business Analyst chuyên nghiệp cho nền tảng bán khóa học trực tuyến.
            Nhiệm vụ: Phân tích dữ liệu kinh doanh và đưa ra nhận xét thông minh bằng tiếng Việt.
            
            QUY TẮC:
            1. Trả lời bằng tiếng Việt, ngắn gọn, chuyên nghiệp
            2. Chia thành các mục rõ ràng với emoji phù hợp
            3. Đưa ra 3-5 nhận xét quan trọng nhất
            4. Đề xuất 2-3 hành động cụ thể có thể thực hiện ngay
            5. Tập trung vào xu hướng và insight hữu ích
            6. KHÔNG dùng markdown heading (#), chỉ dùng bold (**text**) và bullet points
            7. Giữ tổng chiều dài dưới 500 từ
            8. Phân tích sâu, không lặp lại số liệu đơn thuần
            """;
    }

    private String buildDataPrompt(AdminStatsResponse stats) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        
        StringBuilder sb = new StringBuilder();
        sb.append("Hãy phân tích dữ liệu kinh doanh sau của nền tảng bán khóa học:\n\n");
        
        // Overview metrics
        sb.append("📊 TỔNG QUAN:\n");
        sb.append("- Tổng doanh thu: ").append(formatCurrency(stats.getTotalRevenue())).append("\n");
        sb.append("- Tổng đơn hàng đã thanh toán: ").append(stats.getTotalPaidOrders()).append("\n");
        sb.append("- Trung bình mỗi đơn: ").append(formatCurrency(stats.getAvgRevenuePerOrder())).append("\n");
        sb.append("- Tổng người dùng: ").append(stats.getTotalUsers()).append("\n");
        sb.append("- Tổng khóa học: ").append(stats.getTotalCourses()).append("\n");
        sb.append("- Tổng giảng viên: ").append(stats.getTotalLecturers()).append("\n");
        sb.append("- Tổng đăng ký: ").append(stats.getTotalEnrollments()).append("\n");
        sb.append("- Đăng ký mới trong kỳ: ").append(stats.getNewEnrollmentsThisPeriod()).append("\n");
        sb.append("- Giai đoạn: ").append(stats.getPeriodType()).append("\n\n");
        
        // Revenue trend
        if (stats.getLabels() != null && !stats.getLabels().isEmpty()) {
            sb.append("📈 DOANH THU THEO THỜI GIAN:\n");
            int size = Math.min(stats.getLabels().size(), 30);
            for (int i = 0; i < size; i++) {
                sb.append("  ").append(stats.getLabels().get(i)).append(": ");
                sb.append(formatCurrency(stats.getRevenueSeries().get(i)));
                sb.append(" (").append(stats.getPaidOrdersSeries().get(i)).append(" đơn, ");
                sb.append(stats.getEnrollmentSeries().get(i)).append(" ĐK)\n");
            }
            sb.append("\n");
        }
        
        // Top courses
        if (stats.getTopCourses() != null && !stats.getTopCourses().isEmpty()) {
            sb.append("🏆 TOP KHÓA HỌC ĐĂNG KÝ NHIỀU:\n");
            for (var c : stats.getTopCourses()) {
                sb.append("  - ").append(c.getTitle()).append(": ").append(c.getEnrollments()).append(" học viên\n");
            }
            sb.append("\n");
        }
        
        // Best selling courses
        if (stats.getBestSellingCourses() != null && !stats.getBestSellingCourses().isEmpty()) {
            sb.append("🔥 KHÓA HỌC BÁN CHẠY:\n");
            for (var c : stats.getBestSellingCourses()) {
                sb.append("  - ").append(c.getTitle()).append(": ").append(c.getSoldCount())
                        .append(" bán, doanh thu ").append(formatCurrency(c.getTotalSales())).append("\n");
            }
            sb.append("\n");
        }
        
        // Topic distribution
        if (stats.getTopicDistributions() != null && !stats.getTopicDistributions().isEmpty()) {
            sb.append("📂 PHÂN BỐ CHỦ ĐỀ:\n");
            for (var t : stats.getTopicDistributions()) {
                sb.append("  - ").append(t.getTopicName()).append(": ")
                        .append(t.getCourseCount()).append(" khóa, ")
                        .append(t.getEnrollmentCount()).append(" đăng ký\n");
            }
        }
        
        sb.append("\nHãy phân tích và đưa ra nhận xét + đề xuất.");
        return sb.toString();
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0 đ";
        return NumberFormat.getInstance(new Locale("vi", "VN")).format(amount) + " đ";
    }

    /**
     * Fallback analysis when AI is unavailable
     */
    private String buildFallbackAnalysis(AdminStatsResponse stats) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='ai-analysis-content'>");
        
        // Revenue insight
        html.append("<div class='ai-insight-item'>");
        html.append("<span class='ai-emoji'>💰</span>");
        html.append("<strong>Doanh thu:</strong> Tổng doanh thu đạt <strong>")
                .append(formatCurrency(stats.getTotalRevenue()))
                .append("</strong> từ <strong>").append(stats.getTotalPaidOrders())
                .append("</strong> đơn hàng.");
        
        if (stats.getAvgRevenuePerOrder() != null && stats.getAvgRevenuePerOrder().compareTo(BigDecimal.ZERO) > 0) {
            html.append(" Trung bình mỗi đơn: <strong>").append(formatCurrency(stats.getAvgRevenuePerOrder())).append("</strong>.");
        }
        html.append("</div>");
        
        // Enrollment insight
        html.append("<div class='ai-insight-item'>");
        html.append("<span class='ai-emoji'>📚</span>");
        html.append("<strong>Đăng ký:</strong> Có <strong>").append(stats.getNewEnrollmentsThisPeriod())
                .append("</strong> đăng ký mới trong kỳ trên tổng <strong>")
                .append(stats.getTotalEnrollments()).append("</strong> đăng ký.");
        html.append("</div>");
        
        // Course insight
        if (stats.getTopCourses() != null && !stats.getTopCourses().isEmpty()) {
            var top = stats.getTopCourses().get(0);
            html.append("<div class='ai-insight-item'>");
            html.append("<span class='ai-emoji'>🏆</span>");
            html.append("<strong>Khóa học nổi bật:</strong> \"<em>").append(top.getTitle())
                    .append("</em>\" đang dẫn đầu với <strong>").append(top.getEnrollments())
                    .append("</strong> học viên.");
            html.append("</div>");
        }
        
        // Recommendation
        html.append("<div class='ai-insight-item'>");
        html.append("<span class='ai-emoji'>💡</span>");
        html.append("<strong>Đề xuất:</strong> Cân nhắc tạo chương trình khuyến mãi cho các khóa học ít đăng ký, ");
        html.append("đồng thời mở rộng nội dung các khóa bán chạy nhất.");
        html.append("</div>");
        
        html.append("</div>");
        return html.toString();
    }

    /**
     * Basic markdown to HTML conversion
     */
    private String markdownToHtml(String text) {
        if (text == null) return "";
        
        String html = text
                // Bold
                .replaceAll("\\*\\*(.+?)\\*\\*", "<strong>$1</strong>")
                // Italic
                .replaceAll("\\*(.+?)\\*", "<em>$1</em>")
                // Line breaks
                .replaceAll("\n\n", "</p><p>")
                .replaceAll("\n- ", "<br>• ")
                .replaceAll("\n\\* ", "<br>• ")
                .replaceAll("\n(\\d+)\\. ", "<br>$1. ")
                .replaceAll("\n", "<br>");
        
        return "<p>" + html + "</p>";
    }
}
