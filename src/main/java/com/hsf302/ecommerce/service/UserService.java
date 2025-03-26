package com.hsf302.ecommerce.service;

import com.hsf302.ecommerce.dto.request.UserUpdateProfileRequest;
import com.hsf302.ecommerce.dto.response.UserResponse;
import com.hsf302.ecommerce.entity.User;
import com.hsf302.ecommerce.exception.AppException;
import com.hsf302.ecommerce.exception.ErrorCode;
import com.hsf302.ecommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {
    UserResponse getMyProfile();

    void updateProfile(UserUpdateProfileRequest request);
}

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    public UserResponse getMyProfile() {
        User user = getCurrentUser();
        return mapToUserResponse(user);
    }

    @Override
    public void updateProfile(UserUpdateProfileRequest request) {
        User user = getCurrentUser();
        if (request.getFirstName() != null)
            user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            user.setLastName(request.getLastName());
        userRepository.save(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}