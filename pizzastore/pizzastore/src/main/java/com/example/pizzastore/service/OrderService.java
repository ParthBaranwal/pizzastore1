package com.example.pizzastore.service;

import com.example.pizzastore.model.Cart;
import com.example.pizzastore.model.CartItem;
import com.example.pizzastore.model.OrderItem;
import com.example.pizzastore.model.Orders;
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

    public Orders checkout(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        BigDecimal totalAmount = cart.getCartItems().stream()
                .map(cartItem -> cartItem.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Orders order = new Orders();
        order.setUser(cart.getUser()); // Assuming user is the customer in this case
        order.setOrderDate(new Date());
        order.setTotalAmount(totalAmount);

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct()); // Assuming product is the item
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getTotalPrice());
                    return orderItem;
                }).collect(Collectors.toList());

        order.setItems(orderItems);

        cart.getCartItems().clear();

        cartRepository.save(cart); // Clear the cart
        return orderRepository.save(order); // Save the order
    }
}
