package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // ログインページのGETリクエストを処理するメソッド
    @GetMapping("/login")
    public String showLoginPage() {
        // login.htmlを返す
        return "login";
    }
}