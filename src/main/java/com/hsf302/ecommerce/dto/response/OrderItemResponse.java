package com.hsf302.ecommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderItemResponse {
    Long id;
    Long productId;
    String productName;
    Integer quantity;
    Double price;
    Double totalPrice;
    String thumbnailProduct;
}
