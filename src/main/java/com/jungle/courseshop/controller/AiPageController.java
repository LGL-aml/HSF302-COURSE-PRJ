package com.jungle.courseshop.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ai")
@RequiredArgsConstructor
@Slf4j
public class AiPageController {

    /**
     * Trang AI Tools - Chat và Generate Quiz
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public String aiToolsPage(Model model) {
        model.addAttribute("title", "AI Tools - Course Shop");
        log.info("User accessed AI tools page");
        return "ai/index";
    }
}
