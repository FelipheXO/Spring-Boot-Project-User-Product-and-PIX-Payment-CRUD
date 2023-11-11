package com.api.demo.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.demo.entity.Product;
import com.api.demo.models.ErrorResponse;
import com.api.demo.service.ProductService;
import com.api.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productService;
    @Autowired
    private UserService userService;

    @PostMapping("create")
    public ResponseEntity<?> create(HttpServletRequest request, @RequestBody Product p) {
        final var anUser = this.userService.getUser(request);
        p.setUser(anUser);

        if (p.getName() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("The 'name' field is required."));
        } else if (p.getPrice() == 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("The 'price' field is required."));
        } else if (p.getUser() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("The 'price' field is required."));
        }
        Product newProduct = this.productService.save(p);
        return ResponseEntity.ok(newProduct);
    }

    @GetMapping("")
    public ResponseEntity<?> all(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "price", required = false) Number price) {
        List<Product> list = this.productService.all(name, price);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        Optional<Product> product = this.productService.findOneById(id);
        if (!product.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Product does not exist"));
        }
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(HttpServletRequest request, @PathVariable Long id, @RequestBody Product p) {
        Optional<Product> product = this.productService.findOneById(id);
        final var anUser = this.userService.getUser(request);
        if (!product.isPresent() || product.get().getUser() != anUser) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Product does not exist"));
        }

        if (p.getName() != null) {
            product.get().setName(p.getName());
        }
        if (p.getDescription() != null) {
            product.get().setDescription(p.getDescription());
        }
        if (p.getPrice() != 0) {
            product.get().setPrice(p.getPrice());
        }

        product.get().setUser(anUser);
        Product editProduct = this.productService.save(product.get());
        return ResponseEntity.ok(editProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Product> product = this.productService.findOneById(id);
        if (!product.isPresent()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Product does not exist"));
        }
        this.productService.delete(product.get());
        return ResponseEntity.ok(new ErrorResponse("Product deleted"));
    }

}
