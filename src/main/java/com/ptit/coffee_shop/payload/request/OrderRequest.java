package com.ptit.coffee_shop.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.coffee_shop.common.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderRequest {
    @JsonProperty("OrderItems")
    private List<OrderItemRequest> orderItems;

    @JsonProperty("ShippingAddressId")
    private long shippingAddressId;

    @JsonProperty("PaymentMethod")
    private PaymentMethod paymentMethod;
}
