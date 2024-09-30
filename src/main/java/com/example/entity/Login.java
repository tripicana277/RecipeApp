package com.example.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "spring.datasource")
public class Login {

	private String url;
	private String username;
	private String password;
	private String driverClassName;
}
