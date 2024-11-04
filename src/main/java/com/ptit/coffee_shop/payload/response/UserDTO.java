package com.ptit.coffee_shop.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private String roleName;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("name")
    private String name;

    @JsonProperty("profile_img")
    private String profile_img;

    @JsonProperty("status")
    private String status;
}
