package com.example.pizzastore.repository;

import com.example.pizzastore.model.Category;
import com.example.pizzastore.model.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Locale;

@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {
    List<Products> findByCategory(Category category);
}
