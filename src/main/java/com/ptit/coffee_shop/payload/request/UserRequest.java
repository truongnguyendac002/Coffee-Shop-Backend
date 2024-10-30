package com.ptit.coffee_shop.payload.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Phone")
    private String phone;

    @JsonProperty("ProfileImg")
    private String profileImg;
}
