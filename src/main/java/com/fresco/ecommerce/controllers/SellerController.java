package com.fresco.ecommerce.controllers;

import com.fresco.ecommerce.config.JwtUtil;
import com.fresco.ecommerce.models.Category;
import com.fresco.ecommerce.models.Product;
import com.fresco.ecommerce.repo.ProductRepo;
import com.fresco.ecommerce.repo.UserRepo;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/seller")
@Transactional
public class SellerController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProductRepo productRepo;

    // GET /api/auth/seller/product  — all products owned by this seller
    @GetMapping("/product")
    public ResponseEntity<Object> getAllProducts(@RequestHeader("JWT") String jwt) {
        Integer sellerId = jwtUtil.getUser(jwt).getUserId();
        return ResponseEntity.ok(productRepo.findBySellerUserId(sellerId));
    }

    // GET /api/auth/seller/product/{productId}
    @GetMapping("/product/{productId}")
    public ResponseEntity<Object> getProduct(@RequestHeader("JWT") String jwt,
                                             @PathVariable Integer productId) {
        Integer sellerId = jwtUtil.getUser(jwt).getUserId();
        List<Product> myProducts = productRepo.findBySellerUserId(sellerId);
        Optional<Product> product = productRepo.findById(productId);

        if (product.isPresent() && product.get().getSeller().getUserId().equals(sellerId)) {
            return ResponseEntity.ok(product.get());
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    // POST /api/auth/seller/product  — add new product
    @PostMapping("/product")
    public ResponseEntity<Object> postProduct(@RequestHeader("JWT") String jwt,
                                              @RequestBody Product product) {
        product.setSeller(jwtUtil.getUser(jwt));
        Product saved = productRepo.saveAndFlush(product);
        // returns 201 + the URL of the new product
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("http://localhost/api/auth/seller/product/" + saved.getProductId());
    }

    // PUT /api/auth/seller/product  — update existing product
    @PutMapping("/product")
    public ResponseEntity<Object> putProduct(@RequestHeader("JWT") String jwt,
                                             @RequestBody Product product) {
        Optional<Product> existing = productRepo.findById(product.getProductId());
        if (!existing.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        existing.get().setProductName(product.getProductName());
        existing.get().setPrice(product.getPrice());
        existing.get().setCategory(product.getCategory());
        productRepo.saveAndFlush(existing.get());
        return ResponseEntity.ok(existing.get());
    }

    // DELETE /api/auth/seller/product/{productId}
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Product> deleteProduct(@RequestHeader("JWT") String jwt,
                                                 @PathVariable Integer productId) {
        Integer sellerId = jwtUtil.getUser(jwt).getUserId();
        Optional<Product> product =
                productRepo.findBySellerUserIdAndProductId(sellerId, productId);
        if (!product.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // not your product
        }
        product.get().setSeller(null);
        productRepo.delete(product.get());
        return ResponseEntity.ok(product.get());
    }
}