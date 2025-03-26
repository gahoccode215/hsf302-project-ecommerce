package com.hsf302.ecommerce.dto.response;

import com.hsf302.ecommerce.entity.Role;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
    String token;
    boolean authenticated;
    Set<Role> roles;
}
