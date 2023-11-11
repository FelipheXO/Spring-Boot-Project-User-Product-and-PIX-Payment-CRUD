package com.api.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api.demo.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE (:price IS NULL OR p.price = :price)" +
            " AND (:name IS NULL OR  LOWER(p.name) LIKE LOWER(concat('%', LOWER(:name), '%')))")
    List<Product> findByAll(String name, Number price);

}
