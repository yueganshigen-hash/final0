package com.gamestore.service;

import com.gamestore.dto.AuthDto;
import com.gamestore.entity.User;
import com.gamestore.repository.UserRepository;
import com.gamestore.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    public AuthDto.LoginResponse register(AuthDto.RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new RuntimeException("用户名已存在");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new RuntimeException("邮箱已被注册");

        // 验证邮箱验证码
        if (req.getEmailCode() == null || req.getEmailCode().isBlank())
            throw new RuntimeException("请输入邮箱验证码");
        if (!emailService.verify(req.getEmail(), req.getEmailCode()))
            throw new RuntimeException("验证码错误或已过期");

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        if (req.getRole() == User.Role.MERCHANT) {
            user.setRole(User.Role.MERCHANT);
            user.setShopName(req.getShopName());
            user.setShopDescription(req.getShopDescription());
            user.setMerchantStatus(User.MerchantStatus.APPROVED);
        } else {
            user.setRole(User.Role.USER);
        }

        userRepository.save(user);
        return buildLoginResponse(user);
    }

    public AuthDto.LoginResponse login(AuthDto.LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (BadCredentialsException e) {
            throw new RuntimeException("用户名或密码错误");
        }
        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        return buildLoginResponse(user);
    }

    private AuthDto.LoginResponse buildLoginResponse(User user) {
        String token = jwtUtil.generateToken(user.getUsername());
        AuthDto.UserInfo info = new AuthDto.UserInfo();
        info.setId(user.getId());
        info.setUsername(user.getUsername());
        info.setEmail(user.getEmail());
        info.setAvatar(user.getAvatar());
        info.setRole(user.getRole().name());
        info.setShopName(user.getShopName());
        info.setMerchantStatus(user.getMerchantStatus() != null ? user.getMerchantStatus().name() : null);
        info.setBalance(user.getBalance());

        AuthDto.LoginResponse resp = new AuthDto.LoginResponse();
        resp.setToken(token);
        resp.setUser(info);
        return resp;
    }
}