package com.ptit.coffee_shop.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ptit.coffee_shop.common.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ShippingAddressRequest {
    @JsonProperty("Id")
    private long id;

    @JsonProperty("ReceiverName")
    private String receiverName;

    @JsonProperty("ReceiverPhone")
    private String receiverPhone;

    @JsonProperty("Location")
    private String location;

    @JsonProperty("Status")
    private Status status;

    @JsonProperty("UserId")
    private Long userId;

}
