package com.example.pizzastore.service;

import com.example.pizzastore.model.Category;
import com.example.pizzastore.model.Products;
import com.example.pizzastore.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductsService {

    @Autowired
    private ProductsRepository productsRepository;

    public List<Products> getAllProducts() {
        return productsRepository.findAll();
    }

    public Optional<Products> getProductById(Long id) {
        return productsRepository.findById(id);
    }

    public Products saveProduct(Products product) {
        return productsRepository.save(product);
    }

    public Products updateProduct(Long id, Products productDetails) {
        Products product = productsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setCategory(productDetails.getCategory());
        product.setSize(productDetails.getSize());
        product.setCrustType(productDetails.getCrustType());
        product.setDescription(productDetails.getDescription());
        return productsRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productsRepository.deleteById(id);
    }

    // Method to retrieve products by category
    public List<Products> getProductsByCategory(Category category) {
        return productsRepository.findByCategory(category);
    }
}
