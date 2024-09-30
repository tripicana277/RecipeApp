package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/css/**","/images/**", "/login").permitAll() // CSSファイルやログインページへのアクセスを許可
                .anyRequest().authenticated() // 他のすべてのリクエストを認証が必要
            )
            .formLogin(form -> form
                .loginPage("/login") // カスタムログインページのパス
                .defaultSuccessUrl("/getIncome/2", true)  // ログイン成功後のリダイレクト先
                .permitAll() // 誰でもログインフォームにアクセス可能
            )
            .logout(logout -> logout
                .permitAll() // 誰でもログアウトが可能
            );
        return http.build();
    }
}