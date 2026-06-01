package com.fresco.ecommerce.service;

import com.fresco.ecommerce.config.JwtUtil;
import com.fresco.ecommerce.models.Cart;
import com.fresco.ecommerce.models.CartProduct;
import com.fresco.ecommerce.models.Product;
import com.fresco.ecommerce.models.User;
import com.fresco.ecommerce.repo.CartProductRepo;
import com.fresco.ecommerce.repo.CartRepo;
import com.fresco.ecommerce.repo.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Service
public class ConsumerService {
    @Autowired
    private CartRepo cartRepo;
    @Autowired private CartProductRepo cartProductRepo;
    @Autowired private ProductRepo productRepo;
    @Autowired private JwtUtil jwtUtil;


    // GET — return user's cart
    public Object getCart(String jwt) {
        String username = jwtUtil.getUser(jwt).getUsername();
        return cartRepo.findByUserUsername(username).get();
    }


    // POST — add a product to cart (409 if already in cart)
    public void postCart(String jwt, Product product) {
        User user = jwtUtil.getUser(jwt);

        // get or create cart
        Cart cart = cartRepo.findByUserUsername(user.getUsername())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepo.save(c);
                });

        Product existing = productRepo.findById(product.getProductId())
                .orElseThrow(()->
                        new IllegalArgumentException("Product not found"));

        // 409 if product already in cart
        if (cartProductRepo.findByCartUserUserIdAndProductProductId(
                user.getUserId(), existing.getProductId()).isPresent()) {
           throw new IllegalStateException("Product already in cart");
        }

        cartProductRepo.save(new CartProduct(cart, existing, 1));

        cart.updateTotalAmount(existing.getPrice());
        cartRepo.save(cart);
    }


    // PUT — update quantity; if quantity=0 remove; if not in cart add it
    public void putCart(String jwt, CartProduct cartProduct) {
        User user = jwtUtil.getUser(jwt);

        Cart cart = cartRepo.findByUserUsername(user.getUsername())
                .orElseThrow(()->
                        new IllegalArgumentException("Cart not found"));

        Product existing = productRepo.findById(cartProduct.getProduct().getProductId())
                        .orElseThrow(()->
                                new IllegalArgumentException("Product not found"));

        Optional<CartProduct>optExisting =
                cartProductRepo.findByCartUserUserIdAndProductProductId(
                        user.getUserId(), existing.getProductId());

        if (cartProduct.getQuantity() == 0) {
            // quantity 0 = remove from cart
            if (optExisting.isPresent()) {
                CartProduct cp = optExisting.get();
                cart.updateTotalAmount(-(cp.getProduct().getPrice() * cp.getQuantity()));
                cartProductRepo.deleteByCartUserUserIdAndProductProductId(
                        user.getUserId(), existing.getProductId());
            }
        } else if (optExisting.isPresent()) {
            // update quantity of existing cart item
            CartProduct cp = optExisting.get();
            cart.updateTotalAmount(-(cp.getProduct().getPrice() * cp.getQuantity()));
            cp.setQuantity(cartProduct.getQuantity());
            cart.updateTotalAmount(existing.getPrice() * cp.getQuantity());
            cartProductRepo.save(cp);
        } else {
            // product not in cart yet — add it
            CartProduct newCp = new CartProduct(cart, existing, cartProduct.getQuantity());
            cartProductRepo.save(newCp);
            cart.updateTotalAmount(existing.getPrice() * newCp.getQuantity());
            cartRepo.save(cart);
        }
    }

    // DELETE — remove a product from cart

    public void deleteCart(String jwt, Product product) {
        User user = jwtUtil.getUser(jwt);
        Cart cart = cartRepo.findByUserUsername(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));


        Product existing = productRepo.findById(product.getProductId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found"));

        Optional<CartProduct> optCp =
                cartProductRepo.findByCartUserUserIdAndProductProductId(
                        user.getUserId(), existing.getProductId());

        if (optCp.isPresent()) {
            CartProduct cp = optCp.get();
            cart.updateTotalAmount(-(cp.getProduct().getPrice() * cp.getQuantity()));
            cartProductRepo.delete(cp);
        }
        cartRepo.save(cart);
    }
}
