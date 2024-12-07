package com.ptit.coffee_shop.service;

import com.ptit.coffee_shop.config.MessageBuilder;
import com.ptit.coffee_shop.model.Product;
import com.ptit.coffee_shop.model.User;
import com.ptit.coffee_shop.payload.response.ProductStatisticResponse;
import com.ptit.coffee_shop.payload.response.RespMessage;
import com.ptit.coffee_shop.payload.response.UserDTO;
import com.ptit.coffee_shop.payload.response.UserStatisticResponse;
import com.ptit.coffee_shop.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final OrderItemRepository orderItemRepository;
    private final MessageBuilder messageBuilder;

    public RespMessage getTop5MonthlySellingProduct(Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(0, 5);
        try {
            List<Object[]> results = orderItemRepository.findTop5MonthlySellingProducts(startDate,endDate,pageable);
            List<ProductStatisticResponse> productStatisticResponses = new ArrayList<>();
            for (Object[] result : results) {
                Product product = (Product) result[0];
                Long totalQuantity = (Long) result[1];
                double totalRevenue = (Double) result[2];
                ProductStatisticResponse productStatisticResponse = product.toStatisticResponse();
                productStatisticResponse.setQuantitySold(totalQuantity);
                productStatisticResponse.setTotalRevenue(totalRevenue);
                productStatisticResponses.add(productStatisticResponse);
            }
            return messageBuilder.buildSuccessMessage(productStatisticResponses);
        } catch (Exception e){
            throw new RuntimeException("Error getting top 5 monthly selling products");
        }
    }

    public RespMessage getTop5BestSellingProduct() {
        Pageable pageable = PageRequest.of(0, 5);
        try {
            List<Object[]> results = orderItemRepository.findTop5BestSellingProducts(pageable);
            List<ProductStatisticResponse> productStatisticResponses = new ArrayList<>();
            for (Object[] result : results) {
                Product product = (Product) result[0];
                Long totalQuantity = (Long) result[1];
                double totalRevenue = (Double) result[2];
                ProductStatisticResponse productStatisticResponse = product.toStatisticResponse();
                productStatisticResponse.setQuantitySold(totalQuantity);
                productStatisticResponse.setTotalRevenue(totalRevenue);
                productStatisticResponses.add(productStatisticResponse);
            }
            return messageBuilder.buildSuccessMessage(productStatisticResponses);
        } catch (Exception e){
            throw new RuntimeException("Error getting top 5 best selling products");
        }
    }

    public RespMessage getTop5BestCustomers() {
//        Pageable pageable = PageRequest.of(0, 5);
        try {
            List<Object[]> results = orderItemRepository.findTop5BestCustomers();
            List<UserStatisticResponse> userStatisticResponses = new ArrayList<>();
            for (Object[] result : results) {
                User user = (User) result[0];
                UserStatisticResponse userstatisticResponse = user.toStatistic();
                Double totalSold = (double) result[1];
                userstatisticResponse.setTotalSold(totalSold);
                userStatisticResponses.add(userstatisticResponse);
            }
            return messageBuilder.buildSuccessMessage(userStatisticResponses);
        } catch (Exception e){
            throw new RuntimeException("Error getting top 5 customers");
        }
    }

    public RespMessage getTop5MonthlyCustomers(int month, int year) {
        Pageable pageable = PageRequest.of(0, 5);
        try {
            List<Object[]> results = orderItemRepository.findTop5MonthlyCustomers(month,year,pageable);
            List<UserStatisticResponse> userStatisticResponses = new ArrayList<>();
            for (Object[] result : results) {
                User user = (User) result[0];
                UserStatisticResponse userstatisticResponse = user.toStatistic();
                double totalSold = (double) result[1];
                userstatisticResponse.setTotalSold(totalSold);
                userStatisticResponses.add(userstatisticResponse);
            }
            return messageBuilder.buildSuccessMessage(userStatisticResponses);
        } catch (Exception e){
            throw new RuntimeException("Error getting top 5 monthly customers");
        }
    }
}
