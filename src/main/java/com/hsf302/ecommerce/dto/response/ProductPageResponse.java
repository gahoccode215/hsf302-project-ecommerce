package com.hsf302.ecommerce.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPageResponse extends AbstractPageResponse{
    List<ProductResponse> content;
}
