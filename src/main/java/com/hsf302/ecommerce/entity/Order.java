package com.hsf302.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hsf302.ecommerce.enums.PaymentMethod;
import com.hsf302.ecommerce.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tbl_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "total_amount")
    Double totalAmount;

    @Column(name = "order_date")
    LocalDateTime orderDate;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Column(name = "updated_by")
    String updatedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    @JsonIgnore
    Address address;

    @Enumerated(EnumType.STRING)
    PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    PaymentMethod paymentMethod;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;
}
