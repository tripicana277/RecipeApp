package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MoveController {

    // ログインページのGETリクエストを処理するメソッド
    @GetMapping("/addRecipeForm")
    public String addRecipePage() {
        // Cooking.htmlを返す
        return "Cooking";
    }
}