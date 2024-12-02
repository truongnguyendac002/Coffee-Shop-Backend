package com.ptit.coffee_shop.payload.response;

import com.ptit.coffee_shop.model.OrderItem;
import com.ptit.coffee_shop.model.Review;
import com.ptit.coffee_shop.model.ShippingAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderResponse {
    private long orderId;
    private String orderStatus;
    private Date orderDate;
    private List<OrderItemResponse> orderItems;
    private double total;
    private String paymentMethod;
    private ShippingAddressResponse shippingAddress;
//    private List<Review> listReview;
}
