package com.fresco.ecommerce.controllers;

import com.fresco.ecommerce.config.JwtUtil;
import com.fresco.ecommerce.models.Cart;
import com.fresco.ecommerce.models.CartProduct;
import com.fresco.ecommerce.models.Product;
import com.fresco.ecommerce.models.User;
import com.fresco.ecommerce.repo.CartProductRepo;
import com.fresco.ecommerce.repo.CartRepo;
import com.fresco.ecommerce.repo.ProductRepo;
import com.fresco.ecommerce.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/consumer")
@Transactional
public class ConsumerController {

    @Autowired
    ConsumerService consumerService;

    // GET — return user's cart
    @GetMapping("/cart")
    public ResponseEntity<Object> getCart(@RequestHeader("JWT") String jwt) {

        return ResponseEntity.ok(consumerService.getCart(jwt));

    }

    // POST — add a product to cart (409 if already in cart)
    @PostMapping("/cart")
    public ResponseEntity<Object> postCart(@RequestHeader("JWT") String jwt,
                                           @RequestBody Product product) {

        consumerService.postCart(jwt,product);
        return ResponseEntity.ok().build();
    }

    // PUT — update quantity; if quantity=0 remove; if not in cart add it
    @PutMapping("/cart")
    public ResponseEntity<Object> putCart(@RequestHeader("JWT") String jwt,
                                          @RequestBody CartProduct cartProduct) {

        consumerService.putCart(jwt, cartProduct);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    // DELETE — remove a product from cart
    @DeleteMapping("/cart")
    public ResponseEntity<Object> deleteCart(@RequestHeader("JWT") String jwt,
                                             @RequestBody Product product) {

        consumerService.deleteCart(jwt, product);
        return ResponseEntity.ok().build();
    }
}