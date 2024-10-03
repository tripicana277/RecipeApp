package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Recipeクラスは、投稿内容を保持するためのモデルクラスです。
// このクラスはSerializableインターフェースを実装しており、セッションに保存されたり、外部に保存されたりする際に使用されます。
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Income2 {
	private String incomeDate; // 日付
	@Id
	private String incomeName; // 名称
	private int incomeCount; // 金額
}
