package com.hsf302.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@ToString
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "cart")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<CartItem> items = new HashSet<>();

    Double totalPrice = 0.0;

    public void updateTotalPrice() {
        double totalPrice = 0;
        for (CartItem cartItem : items) {
            if (cartItem != null && cartItem.getProduct() != null) {
                totalPrice += cartItem.getPrice() * cartItem.getQuantity();
            }
        }
        this.totalPrice = totalPrice;
    }
}
