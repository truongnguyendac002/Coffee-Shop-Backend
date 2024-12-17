package com.ptit.coffee_shop.repository;

import com.ptit.coffee_shop.model.OrderItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") long orderId);

    @Query("SELECT oi.productItem.product, SUM(oi.amount) AS totalQuantity, SUM(oi.amount * (oi.price - oi.discount)) AS totalRevenue " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status = 'Completed' " +
            "AND o.orderDate BETWEEN :startDate AND :endDate " +
            "GROUP BY oi.productItem.product " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findTop5MonthlySellingProducts(@Param("startDate") Date startDate,
                                               @Param("endDate") Date endDate, Pageable pageable);

    @Query("SELECT oi.productItem.product, SUM(oi.amount) AS totalQuantity, SUM(oi.amount * (oi.price - oi.discount)) AS totalRevenue " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status = 'Completed' " +
            "GROUP BY oi.productItem.product " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findTop5BestSellingProducts(Pageable pageable);

    @Query("SELECT oi.order.shippingAddress.user, SUM(oi.amount * (oi.price - oi.discount)) AS total " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status = 'Completed'" +
            "GROUP BY oi.order.shippingAddress.user " +
            "ORDER BY total DESC")
    List<Object[]> findTop5BestCustomers();

    @Query("SELECT oi.order.shippingAddress.user, SUM(oi.amount * (oi.price - oi.discount)) AS totalQuantity " +
            "FROM OrderItem oi " +
            "JOIN oi.order o " +
            "WHERE o.status = 'Completed' AND MONTH(o.orderDate) = :month AND YEAR(o.orderDate) = :year " +
            "GROUP BY oi.order.shippingAddress.user " +
            "ORDER BY totalQuantity DESC")
    List<Object[]> findTop5MonthlyCustomers(@Param("month") int month,
                                                   @Param("year") int year,
                                                   Pageable pageable);

    @Query("SELECT SUM(oi.amount) FROM OrderItem oi WHERE oi.productItem.product.id = :productId")
    Optional<Integer> findTotalSold(long productId);

}
