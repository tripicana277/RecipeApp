package com.example.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeMain {

	private String recipeName; // レシピ名
	private String fileName; // レシピ画像ファイル名
	private String comment; // コメント
	private String number; // 人数
	private List<RecipeSubMaterial> recipeSubMaterials; // RecipeSubオブジェクトのリスト
	private List<RecipeSubHowToMake> recipeSubHowToMakes; // RecipeSub2オブジェクトのリスト

	@Data
	public class RecipeSubMaterial {
		private String material; // 材料名
		private String quantity; // 数量
	}

	@Data
	// インナークラスRecipeSub2は、作り方とその画像ファイルを保持します。
	public class RecipeSubHowToMake {
		private String fileName2; // 作り方画像ファイル名
		private String howToMake; // 作り方の詳細
	}

	// レシピ名とファイル名を初期化するコンストラクタ
	public RecipeMain(String recipeName, String fileName) {
		this.setRecipeName(recipeName);
		this.setFileName(fileName);
	}

	// レシピ名、ファイル名、コメント、人数を初期化するコンストラクタ
	public RecipeMain(String recipeName, String fileName, String comment, String number) {
		this.setRecipeName(recipeName);
		this.setFileName(fileName);
		this.setComment(comment);
		this.setNumber(number);
	}
}
