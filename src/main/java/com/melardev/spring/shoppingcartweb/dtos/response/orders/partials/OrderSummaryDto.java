package com.melardev.spring.shoppingcartweb.dtos.response.orders.partials;

import com.melardev.spring.shoppingcartweb.enums.OrderStatus;
import com.melardev.spring.shoppingcartweb.models.Order;

import java.time.ZonedDateTime;

public class OrderSummaryDto {

    private final OrderStatus orderStatus;

    private final String trackingNumber;
    private final Long orderItemsCount;
    private Long id;

    public String address;
    private String city;
    private String country;
    private Integer zipCode;

    private double total;
    private double totalAmount;


    private ZonedDateTime createdAt;


    public OrderSummaryDto(Long id, String trackingNumber, OrderStatus orderStatus, double totalAmount, Long orderItemsCount) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.orderItemsCount = orderItemsCount;
        if (trackingNumber == null)
            this.orderStatus = OrderStatus.PROCESSED;
        else
            this.orderStatus = orderStatus;

        if (trackingNumber == null)
            this.trackingNumber = "not defined yet";
        else
            this.trackingNumber = trackingNumber;


    }

    public static OrderSummaryDto build(Order order) {
        return new OrderSummaryDto(
                order.getId(),
                order.getTrackingNumber(),
                order.getOrderStatus(),
                order.getTotalAmount(),
                order.getOrderItemsCount()
        );
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }


    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }


    public double getTotalAmount() {
        return totalAmount;
    }


    public OrderStatus getOrderStatus() {
        return orderStatus;
    }


    public String getTrackingNumber() {
        return trackingNumber;
    }

    public Long getOrderItemsCount() {
        return orderItemsCount;
    }
}
