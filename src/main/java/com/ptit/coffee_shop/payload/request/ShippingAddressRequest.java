package com.ptit.coffee_shop.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ShippingAddressRequest {
    @JsonProperty("RecieverName")
    private String recieverName;

    @JsonProperty("RecieverPhone")
    private String recieverPhone;

    @JsonProperty("Location")
    private String location;

}
