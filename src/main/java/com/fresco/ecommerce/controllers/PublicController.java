package com.fresco.ecommerce.controllers;

import com.fresco.ecommerce.config.JwtUtil;
import com.fresco.ecommerce.models.Product;
import com.fresco.ecommerce.models.User;
import com.fresco.ecommerce.repo.ProductRepo;
import com.fresco.ecommerce.service.PublicService;
import com.fresco.ecommerce.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    PublicService publicService;

    // GET /api/public/product/search?keyword=tablet
    @GetMapping("/product/search")
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String keyword) {

        if(keyword == null || keyword.trim().isEmpty()){
            return ResponseEntity.badRequest().build();
        }
        List<Product> product = publicService.getProducts(keyword);

        return ResponseEntity.ok(product);
    }

    // POST /api/public/login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User requestBody) {

            return ResponseEntity.ok(publicService.login(requestBody));

    }
}