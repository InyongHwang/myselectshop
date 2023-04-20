package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShopController {

    private final FolderService folderService;

    @GetMapping("/shop")
    public ModelAndView shop() {
        return new ModelAndView("index");
    }

    @GetMapping("/user-folder")
    public String getUserInfo(Model model, HttpServletRequest request) {
        model.addAttribute("folders", folderService.getFolders(request));
        return "/index :: #fragment";
    }
}