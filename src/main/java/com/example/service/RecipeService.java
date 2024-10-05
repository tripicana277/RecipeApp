package com.example.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.entity.RecipeMain;
import com.example.entity.RecipeMain.RecipeSubHowToMake;
import com.example.entity.RecipeMain.RecipeSubMaterial;

@Repository
public class RecipeService {

    // SQLクエリ
    private static final String SELECT_RECIPEMAIN = "SELECT * FROM RECIPEMAIN";
    private static final String SELECT_RECIPEMAIN_BY_NAME = "SELECT * FROM RECIPEMAIN WHERE RECIPENAME = ?";
    private static final String SELECT_MATERIAL_BY_NAME = "SELECT * FROM MATERIAL WHERE RECIPENAME = ?";
    private static final String SELECT_HOWTOMAKE_BY_NAME = "SELECT * FROM HOWTOMAKE WHERE RECIPENAME = ?";
    private static final String INSERT_RECIPEMAIN = "INSERT INTO RECIPEMAIN (RECIPENAME, FILENAME, COMMENT, NUMBER) VALUES (?, ?, ?, ?)";
    private static final String INSERT_MATERIAL = "INSERT INTO MATERIAL (RECIPENAME, MATERIAL, QUANTITY) VALUES (?, ?, ?)";
    private static final String INSERT_HOWTOMAKE = "INSERT INTO HOWTOMAKE (RECIPENAME, FILENAME2, HOWTOMAKE) VALUES (?, ?, ?)";
    private static final String DELETE_RECIPEMAIN_BY_NAME = "DELETE FROM RECIPEMAIN WHERE RECIPENAME = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // レシピを名前で取得するメソッド
    public List<RecipeMain> getOne(String name) {
    	
    	System.out.println("recipeName2" + name);

    	
        List<RecipeMain> recipeList = new ArrayList<>();
        RecipeMain recipeMain = jdbcTemplate.queryForObject(SELECT_RECIPEMAIN_BY_NAME, new Object[] { name }, recipeMainRowMapper());

        if (recipeMain != null) {
            recipeMain.setRecipeSubMaterials(fetchMaterials(name));
            recipeMain.setRecipeSubHowToMakes(fetchHowToMakes(name));
            recipeList.add(recipeMain);
        }

        return recipeList;
    }

    // 全てのレシピを取得するメソッド
    public List<RecipeMain> getAll() {
        return jdbcTemplate.query(SELECT_RECIPEMAIN, recipeMainRowMapper());
    }

    // 新しいレシピを挿入するメソッド
    @Transactional
    public List<RecipeMain> setOne(RecipeMain recipeMain) {
    	
    	System.out.println("recipeName3:" + recipeMain.getRecipeName());
        // レシピメイン情報を挿入
        jdbcTemplate.update(INSERT_RECIPEMAIN, recipeMain.getRecipeName(), recipeMain.getFileName(), recipeMain.getComment(), recipeMain.getNumber());

        // 材料と作り方を挿入
        insertMaterials(recipeMain);
        insertHowToMakes(recipeMain);

        return getOne(recipeMain.getRecipeName());
    }

    // レシピを削除するメソッド
    public void deleteOne(String name) {
        jdbcTemplate.update(DELETE_RECIPEMAIN_BY_NAME, name);
    }

    // 材料リストを取得するメソッド
    private List<RecipeSubMaterial> fetchMaterials(String name) {
        return jdbcTemplate.query(SELECT_MATERIAL_BY_NAME, new Object[] { name }, recipeSubMaterialRowMapper());
    }

    // 作り方リストを取得するメソッド
    private List<RecipeSubHowToMake> fetchHowToMakes(String name) {
        return jdbcTemplate.query(SELECT_HOWTOMAKE_BY_NAME, new Object[] { name }, recipeSubHowToMakeRowMapper());
    }

    // 材料を挿入するメソッド
    private void insertMaterials(RecipeMain recipeMain) {
        for (RecipeSubMaterial sub : recipeMain.getRecipeSubMaterials()) {
            jdbcTemplate.update(INSERT_MATERIAL, recipeMain.getRecipeName(), sub.getMaterial(), sub.getQuantity());
        }
    }

    // 作り方を挿入するメソッド
    private void insertHowToMakes(RecipeMain recipeMain) {
        for (RecipeSubHowToMake sub : recipeMain.getRecipeSubHowToMakes()) {
            jdbcTemplate.update(INSERT_HOWTOMAKE, recipeMain.getRecipeName(), sub.getFileName2(), sub.getHowToMake());
        }
    }

    // RecipeMainのRowMapper
    private RowMapper<RecipeMain> recipeMainRowMapper() {
        return (rs, rowNum) -> new RecipeMain(
            rs.getString("recipeName"),
            rs.getString("fileName"),
            rs.getString("comment"),
            rs.getString("number")
        );
    }

    // RecipeSubMaterialのRowMapper
    private RowMapper<RecipeSubMaterial> recipeSubMaterialRowMapper() {
        return (rs, rowNum) -> {
            RecipeSubMaterial sub = new RecipeMain().new RecipeSubMaterial();
            sub.setMaterial(rs.getString("material"));
            sub.setQuantity(rs.getString("quantity"));
            return sub;
        };
    }

    // RecipeSubHowToMakeのRowMapper
    private RowMapper<RecipeSubHowToMake> recipeSubHowToMakeRowMapper() {
        return (rs, rowNum) -> {
            RecipeSubHowToMake sub = new RecipeMain().new RecipeSubHowToMake();
            sub.setFileName2(rs.getString("fileName2"));
            sub.setHowToMake(rs.getString("howToMake"));
            return sub;
        };
    }
}
