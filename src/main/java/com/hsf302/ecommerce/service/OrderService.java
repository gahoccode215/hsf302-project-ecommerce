package com.hsf302.ecommerce.service;

import com.hsf302.ecommerce.dto.response.OrderItemResponse;
import com.hsf302.ecommerce.dto.response.OrderPageResponse;
import com.hsf302.ecommerce.dto.response.OrderResponse;
import com.hsf302.ecommerce.entity.*;
import com.hsf302.ecommerce.enums.PaymentMethod;
import com.hsf302.ecommerce.enums.PaymentStatus;
import com.hsf302.ecommerce.exception.AppException;
import com.hsf302.ecommerce.exception.ErrorCode;
import com.hsf302.ecommerce.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public interface OrderService {
    OrderResponse createOrder(Long cartId, Long addressId, PaymentMethod paymentMethod);
    OrderPageResponse getOrdersByCustomer(int page, int size);
    void updateOrderStatus(Long orderId, boolean isPaid);
    void deleteOrder(Long id);
    OrderResponse getOrderById(Long id);
}

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class OrderServiceImpl implements OrderService{
    OrderRepository orderRepository;
    UserRepository userRepository;
    CartItemRepository cartItemRepository;
    CartRepository cartRepository;
    OrderItemRepository orderItemRepository;
    AddressRepository addressRepository;
    ProductRepository productRepository;

    @Override
    public OrderResponse createOrder(Long cartId, Long addressId, PaymentMethod paymentMethod) {
        User user = getAuthenticatedUser();
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        if (paymentMethod == null) {
            throw new AppException(ErrorCode.INVALID_PAYMENT_METHOD);
        }
        Order order = buildOrder(cart, address, paymentMethod);
        order = orderRepository.save(order);
        List<OrderItem> orderItems = createOrderItemsFromCart(cart, order);
        orderItems.forEach(orderItem -> {
            productRepository.findById(orderItem.getProduct().getId()).orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND));
        });
        orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);
        orderRepository.save(order);
        if (order.getPaymentMethod() == PaymentMethod.COD) {
            clearCart(cart);
        }
        return mapToOrderResponse(order);
    }

    @Override
    public OrderPageResponse getOrdersByCustomer(int page, int size) {
        User user = getAuthenticatedUser();
        if (page > 0) page -= 1;
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orders = orderRepository.findAllByUsername(user.getUsername(), pageable);
        List<OrderResponse> orderResponses = orders.getContent().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
        OrderPageResponse response = new OrderPageResponse();
        response.setOrderResponseList(orderResponses);
        response.setTotalElements(orders.getTotalElements());
        response.setTotalPages(orders.getTotalPages());
        response.setPageNumber(orders.getNumber());
        response.setPageSize(orders.getSize());

        return response;
    }

    @Override
    public void updateOrderStatus(Long orderId, boolean isPaid) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        order.setPaymentStatus(isPaid ? PaymentStatus.PAID : PaymentStatus.NOT_PAID);
        orderRepository.save(order);

        if (isPaid) {
            Cart cart = cartRepository.findByUser(order.getAddress().getUser())
                    .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND));
            clearCart(cart);
        }
    }

    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));
        return mapToOrderResponse(order);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .totalPrice(item.calculateTotalPrice())
                        .thumbnailProduct(item.getProduct().getThumbnail())
                        .build())
                .collect(Collectors.toList());
        return OrderResponse.builder()
                .orderId(order.getId())
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .paymentMethod(order.getPaymentMethod())
                .orderResponseItemList(itemResponses)
                .address(order.getAddress())
                .paymentStatus(order.getPaymentStatus())
                .build();
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));
    }
    private Order buildOrder(Cart cart, Address address, PaymentMethod paymentMethod) {
        return Order.builder()
                .totalAmount(cart.getTotalPrice())
                .user(cart.getUser())
                .orderDate(LocalDateTime.now())
                .paymentMethod(paymentMethod)
                .address(address)
                .paymentStatus(PaymentStatus.NOT_PAID)
                .build();
    }

    private void clearCart(Cart cart) {
        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }
    private List<OrderItem> createOrderItemsFromCart(Cart cart, Order order) {
        return cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getPrice());
                    orderItem.setTotalPrice(cartItem.getPrice() * cartItem.getQuantity());
                    return orderItem;
                })
                .collect(Collectors.toList());
    }
}
