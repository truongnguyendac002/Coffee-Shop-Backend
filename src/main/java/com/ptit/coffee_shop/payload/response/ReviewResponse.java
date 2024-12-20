package com.ptit.coffee_shop.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ReviewResponse {
    private long id;
    private String userEmail;
    private long userId;
    private String Name;
    private String userAvatar;
    private double rating;
    private String comment;
    private Date createAt;
}
