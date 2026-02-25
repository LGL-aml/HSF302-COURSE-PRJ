package com.jungle.courseshop.controller;

import com.jungle.courseshop.dto.response.AdminStatsResponse;
import com.jungle.courseshop.service.impl.AdminStatsExportService;
import com.jungle.courseshop.service.impl.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;
    private final AdminStatsExportService adminStatsExportService;

    @GetMapping
    public AdminStatsResponse getStats(@RequestParam(value = "period", defaultValue = "month") String period) {
        return adminStatsService.getDashboardStats(period);
    }

    @GetMapping(value = "/export.xlsx")
    public ResponseEntity<byte[]> exportExcel(@RequestParam(value = "period", defaultValue = "month") String period) {
        byte[] data = adminStatsExportService.exportExcel(adminStatsService.getDashboardStats(period), period);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=admin-stats-" + period + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(data);
    }

    @GetMapping(value = "/export.pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestParam(value = "period", defaultValue = "month") String period) {
        byte[] data = adminStatsExportService.exportPdf(adminStatsService.getDashboardStats(period), period);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=admin-stats-" + period + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }
}
