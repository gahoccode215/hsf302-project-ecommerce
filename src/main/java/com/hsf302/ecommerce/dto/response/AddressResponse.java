package com.hsf302.ecommerce.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressResponse {
    Long id;
    String name;
    String phone;
    String city;
    String district;
    String ward;
    String street;
    String addressLine;
    Boolean isDefault;
}
