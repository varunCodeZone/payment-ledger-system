package com.varunkumar.payment_ledger.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.time.Duration;

@Component
public class IdempotencyInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (!request.getRequestURI().contains("/transfer")) {
            return true;
        }

        String idempotencyKey = request.getHeader("Idempotency-Key");

        if (idempotencyKey == null || idempotencyKey.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing Idempotency-Key header");
            return false;
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey(idempotencyKey))) {
            response.sendError(HttpServletResponse.SC_CONFLICT, "Request already processed");
            return false;
        }

        redisTemplate.opsForValue().set(idempotencyKey, "PROCESSING", Duration.ofMinutes(10));
        return true;
    }
}