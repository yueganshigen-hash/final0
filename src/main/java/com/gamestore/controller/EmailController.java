package com.gamestore.controller;

import com.gamestore.dto.Result;
import com.gamestore.repository.UserRepository;
import com.gamestore.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;
    private final UserRepository userRepository;

    // 简单限流：同一邮箱60秒内只能发一次
    private final Map<String, Long> rateLimitMap = new ConcurrentHashMap<>();

    @PostMapping("/code")
    public Result<String> sendCode(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || !email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            return Result.fail("邮箱格式不正确");
        }

        // 检查邮箱是否已注册
        if (userRepository.existsByEmail(email)) {
            return Result.fail("该邮箱已被注册");
        }

        // 限流检查
        Long lastSent = rateLimitMap.get(email);
        if (lastSent != null && System.currentTimeMillis() - lastSent < 60_000) {
            long wait = 60 - (System.currentTimeMillis() - lastSent) / 1000;
            return Result.fail("请等待 " + wait + " 秒后再试");
        }

        try {
            emailService.sendCode(email);
            rateLimitMap.put(email, System.currentTimeMillis());
            return Result.success("验证码已发送，请查收邮件");
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}