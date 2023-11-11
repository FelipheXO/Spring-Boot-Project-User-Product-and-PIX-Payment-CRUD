package com.api.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.api.demo.entity.Product;
import com.api.demo.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product save(Product product) {
        return this.productRepository.save(product);
    }

    public void delete(Product product) {
        this.productRepository.delete(product);
    }

    public Optional<Product> findOneById(Long id) {
        return this.productRepository.findById(id);
    }

    public List<Product> all(String name, Number price) {
        return this.productRepository.findByAll(name, price);
    }

}
