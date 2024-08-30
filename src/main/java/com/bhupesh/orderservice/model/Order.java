package com.bhupesh.orderservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.lang.invoke.TypeDescriptor;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "t_orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    private String skuCode;
    private Double price;
    private Integer quantity;
}
