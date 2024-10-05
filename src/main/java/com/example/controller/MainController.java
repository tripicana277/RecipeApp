package com.example.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(MainController.class);

	
	
	
	
	
	
	
	
	
	
    // ログインページのGETリクエストを処理するメソッド
    @GetMapping("/testpages2")
    public String showLoginPage() {
        // login.htmlを返す
        return "CookingAll";
    }
	
	
	
	
	
	
	// レシピリストの取得 (GET)
	@GetMapping("/recipes")
	public String getRecipes(Model model) {
	    try {
	        logger.debug("レシピの取得処理を開始");
//	        List<RecipeMain> recipeList = recipeLogic.getAllRecipes();
//	        logger.debug("取得したレシピ数: {}", recipeList.size());
//	        model.addAttribute("recipeList", recipeList);
//	    } catch (SQLException e) {
//	        logger.error("SQLエラーが発生しました: {}", e.getMessage(), e);
//	        return "error";
	    } catch (Exception e) {
	        logger.error("予期せぬエラーが発生しました: {}", e.getMessage(), e);
	        return "error";
	    }
	    return "CookingAll";
	}

	// 特定のレシピの取得 (GET)
	@GetMapping("/recipe")
	public String getRecipe(@RequestParam("button") String recipeName, Model model) {
		try {
			List<RecipeMain> recipeList = recipeLogic.getRecipeByName(recipeName);
			if (recipeList.isEmpty()) {
				logger.warn("指定されたレシピが見つかりません: {}", recipeName);
				return "error"; // レシピが見つからない場合
			}
			model.addAttribute("recipe", recipeList.get(0)); // 最初のレシピを表示
		} catch (SQLException e) {
			logger.error("レシピの取得中にSQLエラーが発生しました", e);
			return "error"; // エラーページにリダイレクト
		}
		return "Cooking-imageview"; // 特定のレシピ表示ページ
	}

	// レシピの削除 (POST)
	@PostMapping("/deleteRecipe")
	public String deleteRecipe(@RequestParam("delete") String recipeName, Model model) {
		try {
			recipeLogic.deleteRecipe(recipeName); // レシピ削除
			logger.debug("レシピを削除しました: {}", recipeName);
			List<RecipeMain> recipeList = recipeLogic.getAllRecipes(); // 更新されたリストを取得
			model.addAttribute("recipeList", recipeList);
		} catch (SQLException e) {
			logger.error("レシピの削除中にエラーが発生しました", e);
			return "error"; // エラーページにリダイレクト
		}
		return "Cooking-All"; // 更新されたレシピ一覧を表示
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
			if (uploadedFileName == null) {
				logger.warn("メインレシピ画像がアップロードされていません");
			}

			// 材料リスト作成
			List<RecipeSubMaterial> recipeSubMaterials = createRecipeSubMaterials(materials, quantities);

			// 作り方リスト作成
			List<RecipeSubHowToMake> recipeSubHowToMakes = createRecipeSubHowToMakes(howToMakeFiles, howToMakes);

			// RecipeMainオブジェクトを作成
			RecipeMain recipeMain = new RecipeMain(recipeName, uploadedFileName, comment, number, recipeSubMaterials,
					recipeSubHowToMakes);

			// レシピ保存
			recipeLogic.addRecipe(recipeMain); // 単一のオブジェクトとして保存
			model.addAttribute("recipe", recipeMain);
			logger.debug("新しいレシピが追加されました: {}", recipeName);

		} catch (IOException | SQLException e) {
			logger.error("レシピの追加中にエラーが発生しました", e);
			return "error"; // エラーページにリダイレクト
		}
		return "Cooking-imageview"; // 新しいレシピ表示ページ
	}

	// ファイルアップロード処理
	private String uploadFile(MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			logger.warn("アップロードされたファイルが空です");
			return null;
		}

		String fileName = file.getOriginalFilename();
		Path uploadDir = Path.of("uploads");

		if (Files.notExists(uploadDir)) {
			Files.createDirectories(uploadDir);
			logger.debug("アップロードディレクトリを作成しました: {}", uploadDir.toString());
		}

		Path filePath = uploadDir.resolve(fileName);
		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		logger.debug("ファイルをアップロードしました: {}", filePath.toString());

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
	private List<RecipeSubHowToMake> createRecipeSubHowToMakes(List<MultipartFile> files, String[] howToMakes)
			throws IOException {
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
