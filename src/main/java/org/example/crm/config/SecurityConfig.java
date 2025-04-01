package org.example.crm.config;

import org.example.crm.filter.JwtAuthenticationFilter;
import org.example.crm.handler.JwtAccessDeniedHandler;
import org.example.crm.handler.JwtAuthEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    CorsConfigurationSource corsConfigurationSource(){
        // 创建基于URL路径的CORS配置源
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        // 创建CORS配置对象
        CorsConfiguration config = new CorsConfiguration();
        //具体配置跨域
        config.setAllowedOrigins(Arrays.asList("*")); //允许任何来源  比如http://localhost:10492/
        config.setAllowedHeaders(Arrays.asList("*")); //允许任何请求头(jwt)
        config.setAllowedMethods(Arrays.asList("*")); //允许任何请求方法：get post put delete

        // 将CORS配置注册到所有路径（/** 表示通配所有接口）
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**",config);
        return urlBasedCorsConfigurationSource;
    }

    /**
     * 配置SecurityFilterChain：禁用CSRF、设置无状态Session、配置访问规则、异常处理、添加JWT过滤器
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用默认的表单登录（防止UsernamePasswordAuthenticationFilter拦截/login）
                .formLogin(form -> form.disable())
                .cors((cors)->{
                    cors.configurationSource(corsConfigurationSource());
                })
                // 1. 关闭CSRF，因为JWT不依赖cookie session
                .csrf(csrf -> csrf.disable())
                // 2. 设置Session为无状态，不使用HttpSession保存用户认证信息
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 3. 配置请求授权规则：开放登录、注册和验证码接口，其余请求需要认证
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register","/user/verifycode").permitAll()
                        .anyRequest().authenticated()
                )
                // 4. 配置异常处理：未认证、无权限时返回JSON提示
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new JwtAuthEntryPoint())   // 处理未认证请求
                        .accessDeniedHandler(new JwtAccessDeniedHandler())   // 处理权限不足请求
                )
                // 5. 将自定义的JWT过滤器添加到Spring Security的过滤器链中
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 构建SecurityFilterChain
        return http.build();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

}
