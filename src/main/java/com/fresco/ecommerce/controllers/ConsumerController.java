package com.fresco.ecommerce.controllers;

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
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/consumer")
@Transactional
public class ConsumerController {

    @Autowired private CartRepo cartRepo;
    @Autowired private CartProductRepo cartProductRepo;
    @Autowired private ProductRepo productRepo;
    @Autowired private JwtUtil jwtUtil;

    // GET — return user's cart
    @GetMapping("/cart")
    public ResponseEntity<Object> getCart(@RequestHeader("JWT") String jwt) {
        String username = jwtUtil.getUser(jwt).getUsername();
        return ResponseEntity.ok(cartRepo.findByUserUsername(username).get());
    }

    // POST — add a product to cart (409 if already in cart)
    @PostMapping("/cart")
    public ResponseEntity<Object> postCart(@RequestHeader("JWT") String jwt,
                                           @RequestBody Product product) {
        User user = jwtUtil.getUser(jwt);

        // get or create cart
        Cart cart = cartRepo.findByUserUsername(user.getUsername())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUser(user);
                    return cartRepo.save(c);
                });

        Product existing = productRepo.findById(product.getProductId()).get();

        // 409 if product already in cart
        if (cartProductRepo.findByCartUserUserIdAndProductProductId(
                user.getUserId(), existing.getProductId()).isPresent()) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        cartProductRepo.save(new CartProduct(cart, existing, 1));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // PUT — update quantity; if quantity=0 remove; if not in cart add it
    @PutMapping("/cart")
    public ResponseEntity<Object> putCart(@RequestHeader("JWT") String jwt,
                                          @RequestBody CartProduct cartProduct) {
        User user = jwtUtil.getUser(jwt);
        Cart cart = cartRepo.findByUserUsername(user.getUsername())
                .orElse(null);
        if (cart == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Optional<Product> optProduct =
                productRepo.findById(cartProduct.getProduct().getProductId());
        if (!optProduct.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        Product existing = optProduct.get();

        Optional<CartProduct> optExisting =
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
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // DELETE — remove a product from cart
    @DeleteMapping("/cart")
    public ResponseEntity<Object> deleteCart(@RequestHeader("JWT") String jwt,
                                             @RequestBody Product product) {
        User user = jwtUtil.getUser(jwt);
        Cart cart = cartRepo.findByUserUsername(user.getUsername())
                .orElse(null);
        if (cart == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Optional<Product> optProduct = productRepo.findById(product.getProductId());
        if (!optProduct.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Optional<CartProduct> optCp =
                cartProductRepo.findByCartUserUserIdAndProductProductId(
                        user.getUserId(), optProduct.get().getProductId());

        if (optCp.isPresent()) {
            CartProduct cp = optCp.get();
            cart.updateTotalAmount(-(cp.getProduct().getPrice() * cp.getQuantity()));
            cartProductRepo.delete(cp);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}