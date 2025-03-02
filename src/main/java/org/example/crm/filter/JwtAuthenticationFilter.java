package org.example.crm.filter;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.crm.entity.User;
import org.example.crm.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.example.crm.constant.Constant;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private RedisTemplate<String ,Object> redisTemplate;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        // 从请求头中提取 token
        String header = request.getHeader(Constant.TOKEN_HEADER);
        if (header != null && header.startsWith(Constant.TOKEN_PREFIX)) {
            String token = header.substring(Constant.TOKEN_PREFIX.length());
            try {
                // 利用 Hutool 验证 token 签名是否有效
                if (JWTUtil.verify(token, Constant.SECRET.getBytes())) {
                    // 解析 token，获取载荷中的 username
                    JWT jwt = JWTUtil.parseToken(token);
                    String username = jwt.getPayload("username").toString();
                    // 如果 username 不为空且当前 SecurityContext 中无认证信息
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // 根据 username 加载用户详细信息（包括自定义 User 实现）
                        User user = (User) userServiceImpl.loadUserByUsername(username);

                        // 4. Redis 中获取存储的 token
                        // 此处，Redis存储的是一个 Hash，key 为 Constant.SECRET，
                        // field 为用户 id（String），value 为 token 字符串
                        String redisToken = (String) redisTemplate.opsForHash().get(Constant.SECRET, String.valueOf(user.getId()));
                        if(redisToken!=null&&token.equals(redisToken)){
                            // 构建 Authentication 对象，不需要密码（此时已通过 token 验证）
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            // 将 Authentication 设置到 SecurityContext 中，后续请求就能获取到用户信息
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }else {
                            // Redis 中未查到对应 token，说明 token 可能已失效或被注销
                            logger.warn("Token not found in Redis or does not match for user id: " + user.getId());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error("JWT解析或验证失败：{}"+ e.getMessage());
            }
        }
        // 继续执行过滤链
        filterChain.doFilter(request, response);
    }
}