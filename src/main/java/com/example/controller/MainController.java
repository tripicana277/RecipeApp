package com.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.entity.RecipeMain;
import com.example.entity.RecipeMain.RecipeSubHowToMake;
import com.example.entity.RecipeMain.RecipeSubMaterial;
import com.example.service.RecipeLogic;

@Controller
public class MainController {

    @Autowired
    private RecipeLogic recipeLogic;

    // レシピリストの取得 (GET)
    @GetMapping("/recipes")
    public String getRecipes(Model model) {
        try {
            List<RecipeMain> recipeList = recipeLogic.getAllRecipes();
            model.addAttribute("recipeList", recipeList);
        } catch (SQLException e) {
            e.printStackTrace();
            return "error";  // エラーページにリダイレクト
        }
        return "Cooking-All";  // レシピ一覧ページ
    }

    // 特定のレシピの取得 (GET)
    @GetMapping("/recipe")
    public String getRecipe(@RequestParam("button") String recipeName, Model model) {
        try {
            List<RecipeMain> recipeList = recipeLogic.getRecipeByName(recipeName);
            model.addAttribute("recipe", recipeList.get(0));  // 修正点: リストから最初の要素を取得する代わりに、直接オブジェクトを使う
        } catch (SQLException e) {
            e.printStackTrace();
            return "error";  // エラーページにリダイレクト
        }
        return "Cooking-imageview";  // 特定のレシピ表示ページ
    }

    // レシピの削除 (POST)
    @PostMapping("/deleteRecipe")
    public String deleteRecipe(@RequestParam("delete") String recipeName, Model model) {
        try {
            recipeLogic.deleteRecipe(recipeName);  // レシピ削除
            List<RecipeMain> recipeList = recipeLogic.getAllRecipes();  // 更新されたリストを取得
            model.addAttribute("recipeList", recipeList);
        } catch (SQLException e) {
            e.printStackTrace();
            return "error";  // エラーページにリダイレクト
        }
        return "Cooking-All";  // 更新されたレシピ一覧を表示
    }

    // 新しいレシピの追加 (POST)
    @PostMapping("/addRecipe")
    public String addRecipe(@RequestParam("fileName") MultipartFile file, 
                            @RequestParam("recipeName") String recipeName,
                            @RequestParam("comment") String comment,
                            @RequestParam("number") String number,
                            @RequestParam("material[]") String[] materials,
                            @RequestParam("quantity[]") String[] quantities,
                            @RequestParam("howToMake[]") String[] howToMakes,
                            @RequestParam("fileName2[]") List<MultipartFile> howToMakeFiles,
                            Model model) {
        try {
            // ファイルアップロード
            String uploadedFileName = uploadFile(file);

            // 材料リスト作成
            List<RecipeSubMaterial> recipeSubMaterials = createRecipeSubMaterials(materials, quantities);

            // 作り方リスト作成
            List<RecipeSubHowToMake> recipeSubHowToMakes = createRecipeSubHowToMakes(howToMakeFiles, howToMakes);

            // RecipeMainオブジェクトを作成
            RecipeMain recipeMain = new RecipeMain(recipeName, uploadedFileName, comment, number, recipeSubMaterials, recipeSubHowToMakes);

            // レシピ保存
            List<RecipeMain> addedRecipe = recipeLogic.addRecipe(recipeMain);  // 修正点: 単一のオブジェクトとして扱う
            model.addAttribute("recipe", addedRecipe.get(0));  // 修正点: リストから最初の要素を取得する代わりに、直接オブジェクトを使う
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return "error";  // エラーページにリダイレクト
        }
        return "Cooking-imageview";  // 新しいレシピ表示ページ
    }

    // ファイルアップロード処理
    private String uploadFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        // アップロードされたファイルのオリジナル名を取得
        String fileName = file.getOriginalFilename();
        // アップロード先ディレクトリのパスを指定
        Path uploadDir = Path.of("uploads");

        // ディレクトリが存在しない場合は作成
        if (Files.notExists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // ファイルを保存するパスを作成
        Path filePath = uploadDir.resolve(fileName);

        // ファイルを指定した場所に保存
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return fileName;
    }

    // 材料リストの作成
    private List<RecipeSubMaterial> createRecipeSubMaterials(String[] materials, String[] quantities) {
        List<RecipeSubMaterial> recipeSubMaterials = new ArrayList<>();
        for (int i = 0; i < materials.length; i++) {
            RecipeSubMaterial sub = new RecipeMain().new RecipeSubMaterial();
            sub.setMaterial(materials[i]);
            sub.setQuantity(quantities[i]);
            recipeSubMaterials.add(sub);
        }
        return recipeSubMaterials;
    }

    // 作り方リストの作成
    private List<RecipeSubHowToMake> createRecipeSubHowToMakes(List<MultipartFile> files, String[] howToMakes) throws IOException {
        List<RecipeSubHowToMake> recipeSubHowToMakes = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            String fileName = uploadFile(files.get(i));
            RecipeSubHowToMake sub = new RecipeMain().new RecipeSubHowToMake();
            sub.setFileName2(fileName);
            sub.setHowToMake(howToMakes[i]);
            recipeSubHowToMakes.add(sub);
        }
        return recipeSubHowToMakes;
    }
}
