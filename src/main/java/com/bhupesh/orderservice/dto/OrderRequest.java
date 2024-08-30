package com.bhupesh.orderservice.dto;

import lombok.Builder;

@Builder
public record OrderRequest(
        Long id,
        String orderNumber,
        String skuCode,
        Double price,
        Integer quantity,
        UserDetails userDetails
) {
    public record UserDetails(
            String firstName,
            String lastName,
            String email
    ) {}
}
