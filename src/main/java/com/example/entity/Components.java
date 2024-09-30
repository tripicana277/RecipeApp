package com.example.entity;

import java.time.LocalDate;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Components {
	private LocalDate date;
	private int standardAsset = 0;	// 基準資産(現在未使用)
}