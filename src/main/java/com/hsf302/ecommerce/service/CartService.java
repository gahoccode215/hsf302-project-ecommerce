package com.hsf302.ecommerce.service;

import com.hsf302.ecommerce.dto.response.CartItemResponse;
import com.hsf302.ecommerce.dto.response.CartResponse;
import com.hsf302.ecommerce.entity.Cart;
import com.hsf302.ecommerce.entity.CartItem;
import com.hsf302.ecommerce.entity.Product;
import com.hsf302.ecommerce.entity.User;
import com.hsf302.ecommerce.exception.AppException;
import com.hsf302.ecommerce.exception.ErrorCode;
import com.hsf302.ecommerce.repository.CartItemRepository;
import com.hsf302.ecommerce.repository.CartRepository;
import com.hsf302.ecommerce.repository.ProductRepository;
import com.hsf302.ecommerce.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface CartService {
    void addProductToCart(Long productId, Integer quantity);
    void removeProductFromCart(List<Long> productIds);
    CartResponse getCart();
    void updateProductQuantityInCart(Long productId, Integer quantity);
}

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional
class CartServiceImpl implements CartService{

    CartRepository cartRepository;
    ProductRepository productRepository;
    UserRepository userRepository;
    CartItemRepository cartItemRepository;

    @Override
    public void addProductToCart(Long productId, Integer quantity) {
        log.info("VO DAY");
        User user = getAuthenticatedUser();
        Product product = getProductById(productId);
        Cart cart = getOrCreateCartEntity(user);
        log.info("{}", cart);
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            addNewItemToCart(cart, product, quantity);
        }
        cart.updateTotalPrice();
        cartRepository.save(cart);
    }

    @Override
    public void removeProductFromCart(List<Long> productIds) {
        User user = getAuthenticatedUser();
        Cart cart = getOrCreateCartEntity(user);
        List<Product> products = productRepository.findAllById(productIds);
        if (products.size() != productIds.size()) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND);
        }
        List<CartItem> itemsToRemove = cart.getItems().stream()
                .filter(cartItem -> productIds.contains(cartItem.getProduct().getId()))
                .toList();
        cart.getItems().removeAll(itemsToRemove);
        cartItemRepository.deleteAll(itemsToRemove);
        cart.updateTotalPrice();
        cartRepository.save(cart);
    }

    @Override
    public CartResponse getCart() {
        User user = getAuthenticatedUser();
        Cart cart = getOrCreateCartEntity(user);
        Double totalPrice = calculateTotalPrice(cart);
        List<CartItemResponse> itemResponses = convertCartItemsToResponses(cart);
        return createCartResponse(cart, itemResponses, totalPrice);
    }

    @Override
    public void updateProductQuantityInCart(Long productId, Integer quantity) {
        User user = getAuthenticatedUser();
        Cart cart = getOrCreateCartEntity(user);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        if (quantity > 0) {
            cartItem.setQuantity(quantity);
        } else {
            throw new AppException(ErrorCode.INVALID_QUANTITY);
        }
        cart.updateTotalPrice();
        cartRepository.save(cart);
    }
    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private Cart getOrCreateCartEntity(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> createNewCart(user));
    }

    private Cart createNewCart(User user) {
        Cart newCart = Cart.builder().user(user).items(new HashSet<>()).build(); // Khởi tạo items là HashSet
        return cartRepository.save(newCart);
    }

    private void addNewItemToCart(Cart cart, Product product, Integer quantity) {
        CartItem newItem = CartItem.builder()
                .product(product)
                .cart(cart)
                .price(product.getPrice())
                .quantity(quantity)
                .build();
        cart.getItems().add(newItem);
    }

    private Double calculateTotalPrice(Cart cart) {
        return cart.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
    }

    private List<CartItemResponse> convertCartItemsToResponses(Cart cart) {
        return cart.getItems().stream()
                .map(item -> CartItemResponse.builder()
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .thumbnail(item.getProduct().getThumbnail())
                        .totalItemPrice(item.getPrice() * item.getQuantity())
                        .build())
                .collect(Collectors.toList());
    }

    private CartResponse createCartResponse(Cart cart, List<CartItemResponse> itemResponses, Double totalPrice) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .username(cart.getUser().getUsername())
                .items(itemResponses)
                .totalPrice(totalPrice)
                .build();
    }
}
