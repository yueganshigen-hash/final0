package com.gamestore.controller;

import com.gamestore.dto.AuthDto;
import com.gamestore.dto.Result;
import com.gamestore.entity.User;
import com.gamestore.repository.UserRepository;
import com.gamestore.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public Result<AuthDto.LoginResponse> register(@Valid @RequestBody AuthDto.RegisterRequest req) {
        return Result.success(authService.register(req));
    }

    @PostMapping("/login")
    public Result<AuthDto.LoginResponse> login(@Valid @RequestBody AuthDto.LoginRequest req) {
        return Result.success(authService.login(req));
    }

    @GetMapping("/me")
    public Result<AuthDto.UserInfo> me(@AuthenticationPrincipal UserDetails ud) {
        User user = userRepository.findByUsername(ud.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        AuthDto.UserInfo info = new AuthDto.UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setEmail(user.getEmail());
        info.setAvatar(user.getAvatar());
        info.setRole(user.getRole().name());
        info.setShopName(user.getShopName());
        info.setBalance(user.getBalance());
        return Result.success(info);
    }
}
