package com.example.pizzastore.service;

import com.example.pizzastore.model.*;
import com.example.pizzastore.repository.CartRepository;
import com.example.pizzastore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;
    public Orders findById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
    public void updateOrderPaymentStatus(Long orderId, Payment.PaymentStatus paymentStatus) {
        Orders order = findById(orderId);
        if (order != null) {
            order.setPaymentStatus(paymentStatus); // Assuming you have a payment status field
            orderRepository.save(order);
        }
    }

    public Orders checkout(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Orders order = new Orders();
        order.setUser(cart.getUser());
        order.setOrderDate(new Date());
        order.setTotalAmount(totalAmount);

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getTotalPrice());
                    orderItem.setPizzaSize(cartItem.getPizzaSize());
                    orderItem.setCrustType(cartItem.getCrustType());
                    orderItem.setBeverageSize(cartItem.getBeverageSize());
                    return orderItem;
                }).collect(Collectors.toList());

        order.setItems(orderItems);

        cart.getCartItems().clear();

        cartRepository.save(cart);
        return orderRepository.save(order);
    }
}
