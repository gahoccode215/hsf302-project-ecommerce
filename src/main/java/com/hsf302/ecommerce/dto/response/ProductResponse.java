package com.hsf302.ecommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class ProductResponse {
    Long id;
    String name;
    String description;
    Double price;
    Long quantity;
    String thumbnail;
    String categoryName;
}
