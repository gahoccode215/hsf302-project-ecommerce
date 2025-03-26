package com.hsf302.ecommerce.controller;

import com.hsf302.ecommerce.dto.ApiResponse;
import com.hsf302.ecommerce.dto.response.CartResponse;
import com.hsf302.ecommerce.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carts")
@Tag(name = "Cart Controller")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CartController {

    CartService cartService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> addItemToCart(
            @RequestParam Long productId, @RequestParam(defaultValue = "1" ) @Min(1) int quantity) {
        cartService.addProductToCart(productId, quantity);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Thêm sản phẩm vào giỏ hàng thành công")
                .build();
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<CartResponse> getCart() {
        return ApiResponse.<CartResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Xem giỏ hàng thành công")
                .result(cartService.getCart())
                .build();
    }
    @DeleteMapping("/remove")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> removeProductFromCart(@RequestParam List<Long> productIds) {
        cartService.removeProductFromCart(productIds);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Xóa sản phẩm khỏi giỏ hàng thành công")
                .build();
    }
    @PatchMapping("/update-quantity")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> updateQuantityCartItem(@RequestParam Long productId, @RequestParam @Min(1) Integer quantity){
        cartService.updateProductQuantityInCart(productId, quantity);
        return ApiResponse.<Void>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật số lượng thành công")
                .build();
    }
}
