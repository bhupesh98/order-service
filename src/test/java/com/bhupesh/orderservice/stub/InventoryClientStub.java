package com.bhupesh.orderservice.stub;


import java.text.MessageFormat;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class InventoryClientStub {
    public static void isInStockStub(String skuCode, Integer quantity) {
        if (quantity <= 100) {
            stubFor(get(urlEqualTo(String.format("/api/inventory?skuCode=%s&quantity=%d", skuCode, quantity)))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withStatus(200)
                            .withBody("true")));
        } else {
            stubFor(get(urlEqualTo(String.format("/api/inventory?skuCode=%s&quantity=%d", skuCode, quantity)))
                    .willReturn(aResponse()
                            .withHeader("Content-Type", "application/json")
                            .withStatus(200)
                            .withBody("false")));
        }
    }
}
