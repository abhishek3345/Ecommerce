package com.fresco.ecommerce.controllers;

import com.fresco.ecommerce.models.Product;
import com.fresco.ecommerce.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth/seller")
public class SellerController {

    @Autowired
    SellerService sellerService;

    // GET /api/auth/seller/product  — all products owned by this seller
    @GetMapping("/product")
    public ResponseEntity<List<Product>> getAllProducts(@RequestHeader("JWT") String jwt) {

        List<Product>products = sellerService.getAllProducts(jwt);
        return ResponseEntity.ok(products);
    }

    // GET /api/auth/seller/product/{productId}
    @GetMapping("/product/{productId}")
    public ResponseEntity<Product> getProduct(@RequestHeader("JWT") String jwt,
                                             @PathVariable Integer productId) {
        Product product = sellerService.getProduct(jwt, productId);
        return ResponseEntity.ok(product);
    }

    // POST /api/auth/seller/product  — add new product
    @PostMapping("/product")
    public ResponseEntity<String> postProduct(@RequestHeader("JWT") String jwt,
                                              @RequestBody Product product) {
        Product saved = sellerService.postProduct(jwt, product);
        String url = "http://localhost/api/auth/seller/product/" + saved.getProductId();
        return ResponseEntity.status(HttpStatus.CREATED).body(url);

    }

    // PUT /api/auth/seller/product  — update existing product
    @PutMapping("/product")
    public ResponseEntity<Object> putProduct(@RequestHeader("JWT") String jwt,
                                             @RequestBody Product product) {
        return ResponseEntity.ok(sellerService.putProduct(jwt,product));
    }

    // DELETE /api/auth/seller/product/{productId}
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Product> deleteProduct(@RequestHeader("JWT") String jwt,
                                                 @PathVariable Integer productId) {

        return ResponseEntity.ok(sellerService.deleteProduct(jwt,productId));
    }
}