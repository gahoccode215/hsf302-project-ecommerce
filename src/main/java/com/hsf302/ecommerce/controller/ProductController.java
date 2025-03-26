package com.hsf302.ecommerce.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
@Tag(name = "Category Controller")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {
}
