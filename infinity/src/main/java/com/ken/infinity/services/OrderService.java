package com.ken.infinity.services;

import com.ken.infinity.models.Order;
import com.ken.infinity.models.Photo;
import com.ken.infinity.models.User;
import java.util.List;

public interface OrderService {
    void save(Order order, User user, Photo photo);
    List<Order> getOrders();
    List<Order> findOrdersByUser(int id);
    void updateOrder(Order order);
    Order findByOrderId(int id);
}
