package com.ken.infinity.services;

import com.ken.infinity.models.Order;
import com.ken.infinity.models.Photo;
import com.ken.infinity.models.User;
import com.ken.infinity.repository.OrderRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {
    OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public void save(Order order, User user, Photo photo) {
        order.setStatus("Ordered");
        order.setUser(user);
        order.setPhoto(photo);
        orderRepository.save(order);
        System.out.println("Inside order service" + order);
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findOrdersByUser(int id) {
        return orderRepository.findByUserId(id);
    }

    @Override
    public void updateOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public Order findByOrderId(int id) {
        return orderRepository.findById(id).orElse(null);
    }
}
