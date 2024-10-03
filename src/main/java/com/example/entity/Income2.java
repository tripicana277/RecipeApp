package com.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 収入情報を保持するエンティティクラス
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Income2 {
    @Id
    private String incomeName; // 名称
    private String incomeDate;  // 日付
    private int incomeCount;    // 金額
}
