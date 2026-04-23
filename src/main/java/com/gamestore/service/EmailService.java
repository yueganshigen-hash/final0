package com.gamestore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${email.code.expire:300}")
    private int expireSeconds;

    private final Map<String, long[]> codeStore = new ConcurrentHashMap<>();

    public void sendCode(String email) {
        String code = String.format("%06d", new Random().nextInt(1000000));
        long expireAt = System.currentTimeMillis() + expireSeconds * 1000L;
        codeStore.put(email, new long[]{Long.parseLong(code), expireAt});

        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(email);
            helper.setSubject("【GameNexus】邮箱验证码");
            helper.setText(buildHtml(code), true);
            mailSender.send(msg);
        } catch (Exception e) {
            codeStore.remove(email);
            throw new RuntimeException("邮件发送失败：" + e.getMessage());
        }
    }

    public boolean verify(String email, String code) {
        long[] stored = codeStore.get(email);
        if (stored == null) return false;
        boolean expired = System.currentTimeMillis() > stored[1];
        boolean match   = String.valueOf((long) stored[0]).equals(code.trim());
        if (match && !expired) {
            codeStore.remove(email);
            return true;
        }
        if (expired) codeStore.remove(email);
        return false;
    }

    private String buildHtml(String code) {
        return """
            <div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;
                        background:#0a0a12;color:#e0e0e0;border-radius:12px;overflow:hidden;">
              <div style="background:linear-gradient(135deg,#4d7fff,#8b5cf6);padding:24px;text-align:center;">
                <h1 style="margin:0;font-size:24px;letter-spacing:3px;color:white;">GAME NEXUS</h1>
              </div>
              <div style="padding:32px;text-align:center;">
                <p style="color:#aaa;margin-bottom:24px;">您正在注册 GameNexus 账号，验证码为：</p>
                <div style="background:#1a1a2e;border:1px solid rgba(77,127,255,0.3);
                            border-radius:8px;padding:20px;display:inline-block;">
                  <span style="font-size:36px;font-weight:bold;letter-spacing:8px;color:#4d7fff;">%s</span>
                </div>
                <p style="color:#666;font-size:13px;margin-top:20px;">
                  验证码有效期 <strong style="color:#aaa;">5分钟</strong>，请勿泄露给他人
                </p>
              </div>
            </div>
            """.formatted(code);
    }
}