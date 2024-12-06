package com.ptit.coffee_shop.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserStatisticResponse {
    private long userId;
    private String userName;
    private String email;
    private Date creatAt;
    private double totalSold;
}
