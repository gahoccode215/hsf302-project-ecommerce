package com.hsf302.ecommerce.dto.response;

import com.hsf302.ecommerce.entity.Address;
import com.hsf302.ecommerce.enums.PaymentMethod;
import com.hsf302.ecommerce.enums.PaymentStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderResponse {
    Long orderId;
    Double totalAmount;
    String username;
    LocalDateTime orderDate;
    PaymentMethod paymentMethod;
    PaymentStatus paymentStatus;
    Address address;
    List<OrderItemResponse> orderResponseItemList;
}
