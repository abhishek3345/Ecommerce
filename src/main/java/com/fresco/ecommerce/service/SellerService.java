package com.fresco.ecommerce.service;

import com.fresco.ecommerce.config.JwtUtil;
import com.fresco.ecommerce.models.Product;
import com.fresco.ecommerce.models.User;
import com.fresco.ecommerce.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import java.util.List;


@Transactional
@Service
public class SellerService {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ProductRepo productRepo;

    // GET /api/auth/seller/product  — all products owned by this seller

    public List<Product> getAllProducts( String jwt) {
        User seller = jwtUtil.getUser(jwt);
        return productRepo.findBySellerUserId(seller.getUserId());
    }


    // GET /api/auth/seller/product/{productId}
    public Product getProduct(String jwt, Integer productId) {
        Integer sellerId = jwtUtil.getUser(jwt).getUserId();

        Product product = productRepo.findById(productId)
                .orElseThrow(()->
                        new IllegalArgumentException("Product not found"));

        if (!product.getSeller().getUserId().equals(sellerId)) {
            throw new IllegalArgumentException("Product not found");
        }
        return product;
    }

    // POST /api/auth/seller/product  — add new product

    public Product postProduct(String jwt, Product product) {

        User seller = jwtUtil.getUser(jwt);
        product.setSeller(seller);

        Product saved = productRepo.saveAndFlush(product);

        return saved;
    }

    // PUT /api/auth/seller/product  — update existing product

    public Product putProduct(String jwt, Product product) {
        Product existing = productRepo.findById(product.getProductId())
                        .orElseThrow(()->
                                new IllegalArgumentException("Product not found"));

        existing.setProductName(product.getProductName());
        existing.setPrice(product.getPrice());
        existing.setCategory(product.getCategory());

        return productRepo.saveAndFlush(existing);
    }

    // DELETE /api/auth/seller/product/{productId}

    public Product deleteProduct( String jwt,Integer productId) {
        Integer sellerId = jwtUtil.getUser(jwt).getUserId();
        Product product = productRepo.findBySellerUserIdAndProductId(sellerId, productId)
                .orElseThrow(()->
                        new IllegalArgumentException("Product not found"));

        product.setSeller(null);
        productRepo.delete(product);
        return product ;
    }
}
