-- =========================================
-- Seed Course Reviews / Ratings
-- =========================================

-- Clear existing reviews (safe reset)
DELETE FROM course_review;

-- Insert sample reviews for courses
-- Course 1
INSERT INTO course_review (user_id, course_id, rating, comment, created_at, updated_at) VALUES
(2, 1, 5, 'Khóa học rất hay, giảng viên dễ hiểu. Rất phù hợp cho người mới bắt đầu!', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
(3, 1, 4, 'Nội dung khá phong phú, tuy nhiên cần thêm bài tập thực hành', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
(5, 1, 5, 'Tuyệt vời! Học xong tôi đã tự tin hơn rất nhiều', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(6, 1, 4, 'Khóa học chất lượng, giá hợp lý', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(7, 1, 5, 'Recommend cho tất cả mọi người!', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY));

-- Course 2
INSERT INTO course_review (user_id, course_id, rating, comment, created_at, updated_at) VALUES
(2, 2, 4, 'Khóa học tốt, kiến thức nâng cao phù hợp', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY)),
(4, 2, 5, 'Giảng viên nhiệt tình, slide đẹp', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
(8, 2, 3, 'Nội dung ổn nhưng cần cập nhật thêm', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(9, 2, 4, 'Đáng học, đặc biệt phần project cuối', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

-- Course 3
INSERT INTO course_review (user_id, course_id, rating, comment, created_at, updated_at) VALUES
(3, 3, 5, 'Khóa học xuất sắc! Được thực hành rất nhiều', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY)),
(5, 3, 5, 'Rất chi tiết, từ cơ bản đến nâng cao', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY)),
(6, 3, 4, 'Hay nhưng hơi dài, cần chia nhỏ hơn', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
(10, 3, 5, 'Một trong những khóa học tốt nhất tôi từng đăng ký', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY)),
(11, 3, 4, 'Giảng viên giỏi, nội dung cập nhật', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Course 4
INSERT INTO course_review (user_id, course_id, rating, comment, created_at, updated_at) VALUES
(2, 4, 3, 'Ổn, nhưng video hơi ngắn', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY)),
(7, 4, 4, 'Kiến thức tốt, rất thực tế', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
(12, 4, 5, 'Mình rất thích khóa này, đã apply vào dự án thực tế', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY));

-- Course 5
INSERT INTO course_review (user_id, course_id, rating, comment, created_at, updated_at) VALUES
(3, 5, 4, 'Khóa học thiết kế rất đẹp, học được nhiều', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY)),
(8, 5, 5, 'Nội dung chuyên sâu, đáng tiền', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY)),
(13, 5, 4, 'Tốt, nhưng nên thêm phần responsive design', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY));

-- Course 6  
INSERT INTO course_review (user_id, course_id, rating, comment, created_at, updated_at) VALUES
(2, 6, 5, 'Khóa học data science tuyệt vời!', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY)),
(5, 6, 4, 'Hay nhưng cần kiến thức toán trước', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY)),
(9, 6, 5, 'Giảng viên giải thích rõ ràng, dễ hiểu', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(14, 6, 3, 'Ổn, cần thêm dataset thực tế', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY));

-- Verify
SELECT '=== REVIEW SUMMARY ===' as info;
SELECT c.id, c.title, COUNT(r.id) as reviews, ROUND(AVG(r.rating), 1) as avg_rating
FROM course c
LEFT JOIN course_review r ON r.course_id = c.id
GROUP BY c.id, c.title
HAVING COUNT(r.id) > 0
ORDER BY avg_rating DESC;

SELECT 'OK - Review data seeded successfully!' as result;
