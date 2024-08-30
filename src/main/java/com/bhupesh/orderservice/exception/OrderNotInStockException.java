package com.bhupesh.orderservice.exception;

public class OrderNotInStockException extends RuntimeException {
    public OrderNotInStockException(String message) {
        super(message);
    }
}
