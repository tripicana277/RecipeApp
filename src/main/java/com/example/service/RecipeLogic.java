package com.example.service;

import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.entity.RecipeMain;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecipeLogic {

    private final RecipeService recipeService;

    // 指定されたIDや文字列に基づいてRecipeオブジェクトを取得するメソッド
    public List<RecipeMain> getRecipeByName(String recipeName) throws SQLException {
        return recipeService.getOne(recipeName); // DAOを使用してデータベースから指定された項目を取得
    }

    // 全てのRecipeオブジェクトを取得するメソッド
    public List<RecipeMain> getAllRecipes() throws SQLException {
        return recipeService.getAll(); // DAOを使用してデータベースから全てのRecipeを取得
    }

    // 新しいレシピを保存するメソッド
    public List<RecipeMain> addRecipe(RecipeMain recipeMain) throws SQLException {
        return recipeService.setOne(recipeMain); // DAOを使用してデータベースにレシピを保存
    }

    // 指定されたレシピを削除するメソッド
    public void deleteRecipe(String recipeName) throws SQLException {
        recipeService.deleteOne(recipeName); // DAOを使用してデータベースから指定された項目を削除
    }
}
