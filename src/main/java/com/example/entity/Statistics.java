package com.example.entity;

import java.util.Map;
import java.util.TreeMap;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Statistics {

//	@Id
	private int allAsset;		// 総資産
	private int averageIncome;	// 平均収支
	private Map<String, Integer> treeMapMonthCount = new TreeMap<>(); // 月額リスト
}