package com.bhupesh.orderservice;

import com.bhupesh.orderservice.dto.OrderRequest;
import com.bhupesh.orderservice.repository.OrderRepository;
import com.bhupesh.orderservice.stub.InventoryClientStub;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
class OrderServiceApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:9.0.1");

    @DynamicPropertySource
    static void setMySQLProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        dynamicPropertyRegistry.add("spring.datasource.username", mySQLContainer::getUsername);
        dynamicPropertyRegistry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Test
    void shouldPlaceOrder() throws Exception {
        OrderRequest orderRequest = getOrderRequest(2);
        String requestString = objectMapper.writeValueAsString(orderRequest);
        InventoryClientStub.isInStockStub(orderRequest.skuCode(), orderRequest.quantity());

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestString)
        ).andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals("Order created successfully", response);
        assertEquals(1, orderRepository.findAll().size());
    }

    @Test
    void shouldFailWhenOrderNotInStock() throws Exception {
        OrderRequest orderRequest = getOrderRequest(1000);
        String requestString = objectMapper.writeValueAsString(orderRequest);
        InventoryClientStub.isInStockStub(orderRequest.skuCode(),orderRequest.quantity());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestString)
        ).andExpect(status().isNotFound())
                .andDo(print());
    }

    private OrderRequest getOrderRequest(int quantity) {
        return OrderRequest.builder()
                .skuCode("iPhone-15")
                .price(140000.0)
                .quantity(quantity)
                .build();
    }

}
