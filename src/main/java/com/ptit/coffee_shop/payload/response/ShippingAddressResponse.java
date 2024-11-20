package com.ptit.coffee_shop.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.coffee_shop.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingAddressResponse {
    private long id;
    private String receiverName;
    private String receiverPhone;
    private String location;
    private Status status;
    private Long userId;
}
