package com.melardev.spring.shoppingcartweb.models.extensions;

import com.melardev.spring.shoppingcartweb.enums.OrderStatus;
import com.melardev.spring.shoppingcartweb.models.Address;
import com.melardev.spring.shoppingcartweb.models.Order;
import com.melardev.spring.shoppingcartweb.models.User;

import java.time.ZonedDateTime;

public class OrderExtension extends Order {

    public OrderExtension(Long orderId, String trackingNumber, OrderStatus orderStatus, ZonedDateTime createdAt, ZonedDateTime updatedAt,
                          Address address, Long userId, String username) {
        this.id = orderId;
        this.setTrackingNumber(trackingNumber);
        setOrderStatus(orderStatus);
        setCreatedAt(createdAt);
        setUpdatedAt(updatedAt);
        setAddress(address);
        User user = new User();
        user.setId(userId);
        user.setUsername(username);
        setUser(user);
    }
}
