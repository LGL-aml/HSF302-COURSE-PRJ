package com.jungle.courseshop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AiPageController {

    @GetMapping("/ai")
    public String aiPage(Model model) {
        model.addAttribute("title", "AI Tools");
        return "ai/index";
    }
}
