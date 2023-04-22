package com.sparta.myselectshop.controller;

import com.sparta.myselectshop.security.UserDetailsImpl;
import com.sparta.myselectshop.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
    public String getUserInfo(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("folders", folderService.getFolders(userDetails.getUser()));
        return "/index :: #fragment";
    }

    // 로그인한 유저가 메인페이지를 요청할 때 유저의 이름 반환, "ㅇㅇㅇ님 환영합니다"
    @GetMapping("/user-info")
    @ResponseBody
    public String getUserName(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return userDetails.getUsername();
    }
}