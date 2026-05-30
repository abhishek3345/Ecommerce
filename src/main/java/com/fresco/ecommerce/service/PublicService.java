package com.fresco.ecommerce.service;

import com.fresco.ecommerce.config.JwtUtil;
import com.fresco.ecommerce.models.Product;
import com.fresco.ecommerce.models.User;
import com.fresco.ecommerce.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class PublicService {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // GET /api/public/product/search?keyword=tablet

    public List<Product> getProducts(String keyword) {

        List<Product> product =  productRepo
                .findByProductNameContainingIgnoreCaseOrCategoryCategoryNameContainingIgnoreCase(
                        keyword, keyword);
        return product;
    }

    // POST /api/public/login
    public String login(User requestBody) {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requestBody.getUsername(),
                            requestBody.getPassword()
                    )
            );
            User user = userAuthService.loadUserByUsername(requestBody.getUsername());
            return jwtUtil.generateToken(user);
        }
}
