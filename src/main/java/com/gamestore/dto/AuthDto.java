package com.gamestore.dto;

import com.gamestore.entity.User;
import jakarta.validation.constraints.*;
import lombok.Data;

public class AuthDto {

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "用户名不能为空")
        @Size(min = 3, max = 20, message = "用户名长度3-20")
        private String username;

        @NotBlank(message = "邮箱不能为空")
        @Email(message = "邮箱格式不正确")
        private String email;

        @NotBlank(message = "密码不能为空")
        @Size(min = 6, message = "密码至少6位")
        private String password;

        private User.Role role = User.Role.USER;  // USER or MERCHANT
        private String shopName;
        private String shopDescription;
        private String emailCode;  // 邮箱验证码
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @Data
    public static class LoginResponse {
        private String token;
        private UserInfo user;
    }

    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String avatar;
        private String role;
        private String shopName;
        private String merchantStatus;
        private java.math.BigDecimal balance;
    }
}