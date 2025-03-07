package com.example.pizzastore.service;

import com.example.pizzastore.dto.DeliveryRequest;
import com.example.pizzastore.model.*;

import com.example.pizzastore.repository.AddressRepository;
import com.example.pizzastore.repository.CartRepository;
import com.example.pizzastore.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private AddressRepository addressRepository;

    public Orders findById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }
    public Orders getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }


    public void updateOrderDeliveryDetails(Long orderId, DeliveryRequest deliveryRequest) {
        Orders order = findById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }

        Address address;
        if (deliveryRequest.getAddressId() != null) {
            // Use existing address
            address = addressRepository.findById(deliveryRequest.getAddressId())
                    .orElseThrow(() -> new RuntimeException("Address not found"));
        } else {
            // Create and save new address
            address = deliveryRequest.getAddress();
            addressRepository.save(address); // Persist the new Address
        }

        // Set the saved or existing address as the delivery address for the order
        order.setDeliveryAddress(address);

        // Save the updated order
        orderRepository.save(order);
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

        // Set the payment status before saving
        order.setPaymentStatus(Payment.PaymentStatus.PENDING); // Example default status

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
        cart.setTotalAmount(BigDecimal.ZERO);

        cartRepository.save(cart);
        return orderRepository.save(order);
    }


    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }
    public void updateOrderPaymentStatus(Long orderId, Payment.PaymentStatus paymentStatus) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPaymentStatus(paymentStatus);
        orderRepository.save(order);
    }
}
