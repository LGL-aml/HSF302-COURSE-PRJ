-- ============================================================
-- SEED SAMPLE DATA - Tạo dữ liệu mẫu phong phú cho biểu đồ
-- Chạy script này trong MySQL để thêm đơn hàng + enrollment
-- Dữ liệu trải đều 30 ngày gần nhất để biểu đồ đẹp và trực quan
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- 1. THÊM ĐƠN HÀNG (PAID) - 60 đơn trải đều 30 ngày
-- ============================================================

-- Ngày 1-5 (cách đây 30-26 ngày) - Tuần đầu thấp
INSERT INTO orders (id, created_at, paid_at, payment_method, status, total_amount, transaction_id, user_id) VALUES
('seed-001', DATE_SUB(NOW(), INTERVAL 30 DAY), DATE_SUB(NOW(), INTERVAL 30 DAY), 'VNPAY', 'PAID', 199000, 'TXN-SEED-001', 9),
('seed-002', DATE_SUB(NOW(), INTERVAL 29 DAY), DATE_SUB(NOW(), INTERVAL 29 DAY), 'VNPAY', 'PAID', 250000, 'TXN-SEED-002', 5),
('seed-003', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY), 'VNPAY', 'PAID', 399000, 'TXN-SEED-003', 11),
('seed-004', DATE_SUB(NOW(), INTERVAL 28 DAY), DATE_SUB(NOW(), INTERVAL 28 DAY), 'VNPAY', 'PAID', 200000, 'TXN-SEED-004', 7),
('seed-005', DATE_SUB(NOW(), INTERVAL 27 DAY), DATE_SUB(NOW(), INTERVAL 27 DAY), 'VNPAY', 'PAID', 199000, 'TXN-SEED-005', 8),
('seed-006', DATE_SUB(NOW(), INTERVAL 26 DAY), DATE_SUB(NOW(), INTERVAL 26 DAY), 'VNPAY', 'PAID', 449000, 'TXN-SEED-006', 12);

-- Ngày 6-10 (cách đây 25-21 ngày) - Bắt đầu tăng
INSERT INTO orders (id, created_at, paid_at, payment_method, status, total_amount, transaction_id, user_id) VALUES
('seed-007', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY), 'VNPAY', 'PAID', 299000, 'TXN-SEED-007', 13),
('seed-008', DATE_SUB(NOW(), INTERVAL 25 DAY), DATE_SUB(NOW(), INTERVAL 25 DAY), 'VNPAY', 'PAID', 200000, 'TXN-SEED-008', 14),
('seed-009', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY), 'VNPAY', 'PAID', 949000, 'TXN-SEED-009', 6),
('seed-010', DATE_SUB(NOW(), INTERVAL 24 DAY), DATE_SUB(NOW(), INTERVAL 24 DAY), 'VNPAY', 'PAID', 199000, 'TXN-SEED-010', 17),
('seed-011', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY), 'VNPAY', 'PAID', 499000, 'TXN-SEED-011', 18),
('seed-012', DATE_SUB(NOW(), INTERVAL 23 DAY), DATE_SUB(NOW(), INTERVAL 23 DAY), 'VNPAY', 'PAID', 250000, 'TXN-SEED-012', 19),
('seed-013', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY), 'VNPAY', 'PAID', 399000, 'TXN-SEED-013', 20),
('seed-014', DATE_SUB(NOW(), INTERVAL 22 DAY), DATE_SUB(NOW(), INTERVAL 22 DAY), 'VNPAY', 'PAID', 200000, 'TXN-SEED-014', 21),
('seed-015', DATE_SUB(NOW(), INTERVAL 21 DAY), DATE_SUB(NOW(), INTERVAL 21 DAY), 'VNPAY', 'PAID', 299000, 'TXN-SEED-015', 22);

-- Ngày 11-15 (cách đây 20-16 ngày) - Đỉnh tuần 2
INSERT INTO orders (id, created_at, paid_at, payment_method, status, total_amount, transaction_id, user_id) VALUES
('seed-016', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), 'VNPAY', 'PAID', 2500000, 'TXN-SEED-016', 24),
('seed-017', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY), 'VNPAY', 'PAID', 399000, 'TXN-SEED-017', 5),
('seed-018', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY), 'VNPAY', 'PAID', 949000, 'TXN-SEED-018', 7),
('seed-019', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY), 'VNPAY', 'PAID', 299000, 'TXN-SEED-019', 8),
('seed-020', DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY), 'VNPAY', 'PAID', 200000, 'TXN-SEED-020', 12),
('seed-021', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY), 'VNPAY', 'PAID', 499000, 'TXN-SEED-021', 13),
('seed-022', DATE_SUB(NOW(), INTERVAL 18 DAY), DATE_SUB(NOW(), INTERVAL 18 DAY), 'VNPAY', 'PAID', 399000, 'TXN-SEED-022', 14),
('seed-023', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY), 'VNPAY', 'PAID', 250000, 'TXN-SEED-023', 16),
('seed-024', DATE_SUB(NOW(), INTERVAL 17 DAY), DATE_SUB(NOW(), INTERVAL 17 DAY), 'VNPAY', 'PAID', 199000, 'TXN-SEED-024', 17),
('seed-025', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY), 'VNPAY', 'PAID', 949000, 'TXN-SEED-025', 9),
('seed-026', DATE_SUB(NOW(), INTERVAL 16 DAY), DATE_SUB(NOW(), INTERVAL 16 DAY), 'VNPAY', 'PAID', 200000, 'TXN-SEED-026', 11);

-- Ngày 16-20 (cách đây 15-11 ngày) - Giảm nhẹ
INSERT INTO orders (id, created_at, paid_at, payment_method, status, total_amount, transaction_id, user_id) VALUES
('seed-027', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), 'VNPAY', 'PAID', 599000, 'TXN-SEED-027', 18),
('seed-028', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), 'VNPAY', 'PAID', 199000, 'TXN-SEED-028', 19),
('seed-029', DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), 'VNPAY', 'PAID', 299000, 'TXN-SEED-029', 20),
('seed-030', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY), 'VNPAY', 'PAID', 399000, 'TXN-SEED-030', 21),
('seed-031', DATE_SUB(NOW(), INTERVAL 13 DAY), DATE_SUB(NOW(), INTERVAL 13 DAY), 'VNPAY', 'PAID', 250000, 'TXN-SEED-031', 22),
('seed-032', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY), 'VNPAY', 'PAID', 949000, 'TXN-SEED-032', 24),
('seed-033', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY), 'VNPAY', 'PAID', 200000, 'TXN-SEED-033', 6),
('seed-034', DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), 'VNPAY', 'PAID', 449000, 'TXN-SEED-034', 5);

-- Ngày 21-25 (cách đây 10-6 ngày) - Tăng mạnh trở lại
INSERT INTO orders (id, created_at, paid_at, payment_method, status, total_amount, transaction_id, user_id) VALUES
('seed-035', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 'VNPAY', 'PAID', 2500000, 'TXN-SEED-035', 7),
('seed-036', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), 'VNPAY', 'PAID', 399000, 'TXN-SEED-036', 8),
('seed-037', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), 'VNPAY', 'PAID', 299000, 'TXN-SEED-037', 12),
('seed-038', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), 'VNPAY', 'PAID', 949000, 'TXN-SEED-038', 13),
('seed-039', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), 'VNPAY', 'PAID', 200000, 'TXN-SEED-039', 14),
('seed-040', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), 'VNPAY', 'PAID', 499000, 'TXN-SEED-040', 16),
('seed-041', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), 'VNPAY', 'PAID', 250000, 'TXN-SEED-041', 17),
('seed-042', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), 'VNPAY', 'PAID', 399000, 'TXN-SEED-042', 18),
('seed-043', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 'VNPAY', 'PAID', 599000, 'TXN-SEED-043', 19),
('seed-044', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), 'VNPAY', 'PAID', 299000, 'TXN-SEED-044', 20),
('seed-045', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), 'VNPAY', 'PAID', 949000, 'TXN-SEED-045', 21);

-- Ngày 26-30 (cách đây 5-1 ngày) - Đỉnh cao cuối tháng
INSERT INTO orders (id, created_at, paid_at, payment_method, status, total_amount, transaction_id, user_id) VALUES
('seed-046', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 'VNPAY', 'PAID', 2500000, 'TXN-SEED-046', 22),
('seed-047', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 'VNPAY', 'PAID', 399000, 'TXN-SEED-047', 24),
('seed-048', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 'VNPAY', 'PAID', 949000, 'TXN-SEED-048', 6),
('seed-049', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 'VNPAY', 'PAID', 299000, 'TXN-SEED-049', 5),
('seed-050', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 'VNPAY', 'PAID', 449000, 'TXN-SEED-050', 7),
('seed-051', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 'VNPAY', 'PAID', 599000, 'TXN-SEED-051', 8),
('seed-052', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 'VNPAY', 'PAID', 200000, 'TXN-SEED-052', 12),
('seed-053', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 'VNPAY', 'PAID', 949000, 'TXN-SEED-053', 13),
('seed-054', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 'VNPAY', 'PAID', 399000, 'TXN-SEED-054', 14),
('seed-055', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 'VNPAY', 'PAID', 250000, 'TXN-SEED-055', 16),
('seed-056', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), 'VNPAY', 'PAID', 199000, 'TXN-SEED-056', 17),
('seed-057', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 'VNPAY', 'PAID', 2500000, 'TXN-SEED-057', 18),
('seed-058', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 'VNPAY', 'PAID', 499000, 'TXN-SEED-058', 19),
('seed-059', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), 'VNPAY', 'PAID', 299000, 'TXN-SEED-059', 20),
('seed-060', NOW(), NOW(), 'VNPAY', 'PAID', 949000, 'TXN-SEED-060', 9);

-- ============================================================
-- 2. THÊM ORDER ITEMS cho các đơn hàng mới
-- ============================================================

INSERT INTO order_item (price, course_id, order_id) VALUES
-- Week 1
(199000, 1, 'seed-001'),
(250000, 2, 'seed-002'),
(399000, 3, 'seed-003'),
(200000, 4, 'seed-004'),
(199000, 15, 'seed-005'),
(250000, 2, 'seed-006'), (199000, 1, 'seed-006'),

-- Week 2
(299000, 16, 'seed-007'),
(200000, 7, 'seed-008'),
(949000, 9, 'seed-009'),
(199000, 13, 'seed-010'),
(299000, 16, 'seed-011'), (200000, 8, 'seed-011'),
(250000, 2, 'seed-012'),
(399000, 3, 'seed-013'),
(200000, 11, 'seed-014'),
(299000, 16, 'seed-015'),

-- Week 3
(2500000, 17, 'seed-016'),
(399000, 3, 'seed-017'),
(949000, 9, 'seed-018'),
(299000, 16, 'seed-019'),
(200000, 7, 'seed-020'),
(299000, 16, 'seed-021'), (200000, 4, 'seed-021'),
(399000, 3, 'seed-022'),
(250000, 2, 'seed-023'),
(199000, 15, 'seed-024'),
(949000, 9, 'seed-025'),
(200000, 11, 'seed-026'),

-- Week 4
(399000, 3, 'seed-027'), (200000, 8, 'seed-027'),
(199000, 1, 'seed-028'),
(299000, 16, 'seed-029'),
(399000, 3, 'seed-030'),
(250000, 2, 'seed-031'),
(949000, 9, 'seed-032'),
(200000, 12, 'seed-033'),
(249000, 5, 'seed-034'), (200000, 4, 'seed-034'),

-- Week 5
(2500000, 17, 'seed-035'),
(399000, 3, 'seed-036'),
(299000, 16, 'seed-037'),
(949000, 9, 'seed-038'),
(200000, 7, 'seed-039'),
(299000, 16, 'seed-040'), (200000, 11, 'seed-040'),
(250000, 2, 'seed-041'),
(399000, 3, 'seed-042'),
(399000, 3, 'seed-043'), (200000, 8, 'seed-043'),
(299000, 16, 'seed-044'),
(949000, 9, 'seed-045'),

-- Last days
(2500000, 17, 'seed-046'),
(399000, 3, 'seed-047'),
(949000, 9, 'seed-048'),
(299000, 16, 'seed-049'),
(249000, 5, 'seed-050'), (200000, 4, 'seed-050'),
(399000, 3, 'seed-051'), (200000, 7, 'seed-051'),
(200000, 12, 'seed-052'),
(949000, 9, 'seed-053'),
(399000, 3, 'seed-054'),
(250000, 2, 'seed-055'),
(199000, 15, 'seed-056'),
(2500000, 17, 'seed-057'),
(299000, 16, 'seed-058'), (200000, 11, 'seed-058'),
(299000, 16, 'seed-059'),
(949000, 9, 'seed-060');

-- ============================================================
-- 3. THÊM ENROLLMENT mới trải đều 30 ngày
-- ============================================================

INSERT INTO course_enrollment (enrollment_date, progress, status, course_id, user_id) VALUES
-- Tuần 1
(DATE_SUB(NOW(), INTERVAL 30 DAY), 15.0, 'IN_PROGRESS', 1, 6),
(DATE_SUB(NOW(), INTERVAL 29 DAY), 25.0, 'IN_PROGRESS', 2, 7),
(DATE_SUB(NOW(), INTERVAL 28 DAY), 0.0, 'IN_PROGRESS', 3, 8),
(DATE_SUB(NOW(), INTERVAL 28 DAY), 10.0, 'IN_PROGRESS', 15, 12),
(DATE_SUB(NOW(), INTERVAL 27 DAY), 0.0, 'IN_PROGRESS', 4, 13),
(DATE_SUB(NOW(), INTERVAL 26 DAY), 30.0, 'IN_PROGRESS', 2, 14),

-- Tuần 2
(DATE_SUB(NOW(), INTERVAL 25 DAY), 0.0, 'IN_PROGRESS', 16, 6),
(DATE_SUB(NOW(), INTERVAL 25 DAY), 20.0, 'IN_PROGRESS', 7, 17),
(DATE_SUB(NOW(), INTERVAL 24 DAY), 45.0, 'IN_PROGRESS', 9, 18),
(DATE_SUB(NOW(), INTERVAL 24 DAY), 0.0, 'IN_PROGRESS', 13, 19),
(DATE_SUB(NOW(), INTERVAL 23 DAY), 50.0, 'IN_PROGRESS', 16, 20),
(DATE_SUB(NOW(), INTERVAL 23 DAY), 0.0, 'IN_PROGRESS', 2, 21),
(DATE_SUB(NOW(), INTERVAL 22 DAY), 10.0, 'IN_PROGRESS', 3, 22),
(DATE_SUB(NOW(), INTERVAL 22 DAY), 0.0, 'IN_PROGRESS', 11, 24),
(DATE_SUB(NOW(), INTERVAL 21 DAY), 35.0, 'IN_PROGRESS', 16, 22),

-- Tuần 3
(DATE_SUB(NOW(), INTERVAL 20 DAY), 0.0, 'IN_PROGRESS', 17, 5),
(DATE_SUB(NOW(), INTERVAL 20 DAY), 60.0, 'IN_PROGRESS', 3, 7),
(DATE_SUB(NOW(), INTERVAL 19 DAY), 0.0, 'IN_PROGRESS', 9, 8),
(DATE_SUB(NOW(), INTERVAL 19 DAY), 15.0, 'IN_PROGRESS', 16, 12),
(DATE_SUB(NOW(), INTERVAL 19 DAY), 0.0, 'IN_PROGRESS', 7, 13),
(DATE_SUB(NOW(), INTERVAL 18 DAY), 0.0, 'IN_PROGRESS', 16, 14),
(DATE_SUB(NOW(), INTERVAL 18 DAY), 40.0, 'IN_PROGRESS', 4, 17),
(DATE_SUB(NOW(), INTERVAL 17 DAY), 0.0, 'IN_PROGRESS', 2, 18),
(DATE_SUB(NOW(), INTERVAL 17 DAY), 20.0, 'IN_PROGRESS', 15, 19),
(DATE_SUB(NOW(), INTERVAL 16 DAY), 0.0, 'IN_PROGRESS', 9, 20),
(DATE_SUB(NOW(), INTERVAL 16 DAY), 0.0, 'IN_PROGRESS', 11, 6),

-- Tuần 4
(DATE_SUB(NOW(), INTERVAL 15 DAY), 0.0, 'IN_PROGRESS', 3, 21),
(DATE_SUB(NOW(), INTERVAL 15 DAY), 30.0, 'IN_PROGRESS', 8, 22),
(DATE_SUB(NOW(), INTERVAL 14 DAY), 0.0, 'IN_PROGRESS', 16, 24),
(DATE_SUB(NOW(), INTERVAL 13 DAY), 50.0, 'IN_PROGRESS', 3, 6),
(DATE_SUB(NOW(), INTERVAL 13 DAY), 0.0, 'IN_PROGRESS', 2, 8),
(DATE_SUB(NOW(), INTERVAL 12 DAY), 0.0, 'IN_PROGRESS', 9, 12),
(DATE_SUB(NOW(), INTERVAL 12 DAY), 0.0, 'IN_PROGRESS', 12, 14),
(DATE_SUB(NOW(), INTERVAL 11 DAY), 25.0, 'IN_PROGRESS', 5, 17),

-- Last week
(DATE_SUB(NOW(), INTERVAL 10 DAY), 0.0, 'IN_PROGRESS', 17, 18),
(DATE_SUB(NOW(), INTERVAL 10 DAY), 0.0, 'IN_PROGRESS', 3, 19),
(DATE_SUB(NOW(), INTERVAL 9 DAY), 0.0, 'IN_PROGRESS', 16, 13),
(DATE_SUB(NOW(), INTERVAL 9 DAY), 70.0, 'IN_PROGRESS', 9, 21),
(DATE_SUB(NOW(), INTERVAL 9 DAY), 0.0, 'IN_PROGRESS', 7, 22),
(DATE_SUB(NOW(), INTERVAL 8 DAY), 0.0, 'IN_PROGRESS', 16, 5),
(DATE_SUB(NOW(), INTERVAL 8 DAY), 15.0, 'IN_PROGRESS', 2, 24),
(DATE_SUB(NOW(), INTERVAL 8 DAY), 0.0, 'IN_PROGRESS', 3, 17),
(DATE_SUB(NOW(), INTERVAL 7 DAY), 0.0, 'IN_PROGRESS', 3, 14),
(DATE_SUB(NOW(), INTERVAL 7 DAY), 0.0, 'IN_PROGRESS', 8, 6),
(DATE_SUB(NOW(), INTERVAL 6 DAY), 35.0, 'IN_PROGRESS', 9, 13),
(DATE_SUB(NOW(), INTERVAL 5 DAY), 0.0, 'IN_PROGRESS', 17, 7),
(DATE_SUB(NOW(), INTERVAL 5 DAY), 0.0, 'IN_PROGRESS', 3, 8),
(DATE_SUB(NOW(), INTERVAL 4 DAY), 0.0, 'IN_PROGRESS', 9, 14),
(DATE_SUB(NOW(), INTERVAL 4 DAY), 0.0, 'IN_PROGRESS', 16, 18),
(DATE_SUB(NOW(), INTERVAL 4 DAY), 80.0, 'IN_PROGRESS', 5, 7),
(DATE_SUB(NOW(), INTERVAL 3 DAY), 0.0, 'IN_PROGRESS', 3, 20),
(DATE_SUB(NOW(), INTERVAL 3 DAY), 0.0, 'IN_PROGRESS', 7, 24),
(DATE_SUB(NOW(), INTERVAL 3 DAY), 0.0, 'IN_PROGRESS', 9, 22),
(DATE_SUB(NOW(), INTERVAL 2 DAY), 0.0, 'IN_PROGRESS', 3, 16),
(DATE_SUB(NOW(), INTERVAL 2 DAY), 0.0, 'IN_PROGRESS', 2, 20),
(DATE_SUB(NOW(), INTERVAL 2 DAY), 0.0, 'IN_PROGRESS', 15, 6),
(DATE_SUB(NOW(), INTERVAL 1 DAY), 0.0, 'IN_PROGRESS', 17, 14),
(DATE_SUB(NOW(), INTERVAL 1 DAY), 0.0, 'IN_PROGRESS', 16, 17),
(DATE_SUB(NOW(), INTERVAL 1 DAY), 0.0, 'IN_PROGRESS', 11, 8),
(NOW(), 0.0, 'IN_PROGRESS', 9, 16);

-- ============================================================
-- 4. CẬP NHẬT enrolled_count cho các khóa học
-- ============================================================

UPDATE course c SET c.enrolled_count = (
    SELECT COUNT(*) FROM course_enrollment ce WHERE ce.course_id = c.id
) WHERE c.id IN (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17);

SET FOREIGN_KEY_CHECKS = 1;

-- Kiểm tra kết quả
SELECT '=== TỔNG KẾT ===' as info;
SELECT COUNT(*) as total_paid_orders FROM orders WHERE status = 'PAID';
SELECT SUM(total_amount) as total_revenue FROM orders WHERE status = 'PAID';
SELECT COUNT(*) as total_enrollments FROM course_enrollment;
SELECT 'OK - Data mẫu đã được thêm thành công!' as result;
