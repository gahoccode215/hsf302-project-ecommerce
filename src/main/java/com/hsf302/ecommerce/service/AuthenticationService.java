package com.hsf302.ecommerce.service;

import com.hsf302.ecommerce.constant.PredefinedRole;
import com.hsf302.ecommerce.dto.request.LoginRequest;
import com.hsf302.ecommerce.dto.request.LogoutRequest;
import com.hsf302.ecommerce.dto.request.RegisterRequest;
import com.hsf302.ecommerce.dto.response.LoginResponse;
import com.hsf302.ecommerce.entity.InvalidatedToken;
import com.hsf302.ecommerce.entity.Role;
import com.hsf302.ecommerce.entity.User;
import com.hsf302.ecommerce.exception.AppException;
import com.hsf302.ecommerce.exception.ErrorCode;
import com.hsf302.ecommerce.repository.InvalidatedTokenRepository;
import com.hsf302.ecommerce.repository.RoleRepository;
import com.hsf302.ecommerce.repository.UserRepository;
import com.hsf302.ecommerce.util.JwtUtil;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public interface AuthenticationService {
    void register(RegisterRequest request);
    LoginResponse login(LoginRequest request);
    void logout(LogoutRequest request);
}

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class AuthenticationServiceImpl implements AuthenticationService {

    JwtUtil jwtUtil;
    InvalidatedTokenRepository invalidatedTokenRepository;
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    @Override
    public void register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.getUsername())){
            throw new AppException(ErrorCode.USERNAME_EXSITED);
        }
        Set<Role> roles = new HashSet<>();
        roles.add(roleRepository.findByName(PredefinedRole.ROLE_CUSTOMER).orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND)));
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRoles(roles);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_LOGIN));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_LOGIN);
        }
        return LoginResponse.builder()
                .token(jwtUtil.generateToken(user))
                .authenticated(true)
                .roles(user.getRoles())
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {
        SignedJWT signedJWT;
        try {
            signedJWT = jwtUtil.verifyToken(request.getToken(), false);
        } catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        String tokenId = null;
        Date expiryTime = null;

        try {
            tokenId = signedJWT.getJWTClaimsSet().getJWTID();
            expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        } catch (ParseException e) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        if (expiryTime.before(new Date())) {
            throw new AppException(ErrorCode.INVALID_TOKEN);
        }
        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .token(tokenId)
                .expiryTime(expiryTime)
                .build();
        invalidatedTokenRepository.save(invalidatedToken);
    }
}
