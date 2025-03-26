package com.hsf302.ecommerce.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED(1001, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    USERNAME_EXISTED(1002, "Username already exists", HttpStatus.CONFLICT),
    ROLE_NOT_FOUND(1003, "Role not found", HttpStatus.NOT_FOUND),
    INVALID_TOKEN(1004, "Invalid token", HttpStatus.BAD_REQUEST),
    INVALID_LOGIN(1005, "Username or password not correct", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(1006, "Category not found", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_FOUND(1007, "Product not found", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1008, "User not found", HttpStatus.BAD_REQUEST),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}
