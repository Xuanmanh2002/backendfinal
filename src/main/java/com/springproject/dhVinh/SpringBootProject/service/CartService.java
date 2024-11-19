package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Cart;
import com.springproject.dhVinh.SpringBootProject.model.CartItem;
import com.springproject.dhVinh.SpringBootProject.model.ServicePack;
import com.springproject.dhVinh.SpringBootProject.repository.CartItemRepository;
import com.springproject.dhVinh.SpringBootProject.repository.CartRepository;
import com.springproject.dhVinh.SpringBootProject.repository.ServicePackRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService implements ICartService{

    private final CartItemRepository cartItemRepository;

    private final CartRepository cartRepository;

    private final ServicePackRepository servicePackRepository;

    @Override
    @Transactional
    public Cart createItemToCart(Long serviceId, Long quantity, Admin admin) {
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }

        Cart cart = cartRepository.findByAdmin(admin);
        if (cart == null) {
            cart = new Cart();
            cart.setAdmins(admin);
            cart.setTotalItems(0L);
            cart.setTotalAmounts(0.0);
            cart.setTotalValidityPeriod(0L);
            this.cartRepository.save(cart);
        }

        Optional<ServicePack> optionalServicePack = servicePackRepository.findById(serviceId);
        if (!optionalServicePack.isPresent()) {
            throw new RuntimeException("Service not found");
        }

        ServicePack servicePack = optionalServicePack.get();
        Cart finalCart = cart;
        List<CartItem> items = finalCart.getCartItems() != null ? finalCart.getCartItems() : new ArrayList<>();
        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getServices().getId().equals(serviceId))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newCartItem = new CartItem();
                    newCartItem.setCarts(finalCart);
                    newCartItem.setServices(servicePack);
                    newCartItem.setQuantity(quantity);
                    newCartItem.setTotalPrice(quantity * servicePack.getPrice());
                    newCartItem.setTotalValidityPeriod(servicePack.getValidityPeriod() * quantity);
                    cartItemRepository.save(newCartItem);
                    finalCart.getCartItems().add(newCartItem);
                    return newCartItem;
                });
        long newQuantity = cartItem.getQuantity();
        cartItem.setQuantity(newQuantity);
        cartItem.setTotalPrice(servicePack.getPrice() * newQuantity);
        cartItemRepository.save(cartItem);

        long totalQuantity = cartItemRepository.sumQuantityByCart(cart.getId());
        double totalPrice = cartItemRepository.sumTotalPriceByCart(cart.getId());
        long totalValidityPeriod = cartItemRepository.sumValidityPeriodByCart(cart.getId());
        cart.setTotalValidityPeriod(totalValidityPeriod);
        cart.setTotalItems(totalQuantity);
        cart.setTotalAmounts(totalPrice);

        return cartRepository.save(cart);
    }


    @Override
    @Transactional
    public Cart updateItemToCart(Long serviceId, Long quantity, Admin admin) {
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }
        Cart cart = cartRepository.findByAdmin(admin);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }
        Optional<ServicePack> optionalServicePack = servicePackRepository.findById(serviceId);
        if (!optionalServicePack.isPresent()) {
            throw new RuntimeException("Service not found");
        }

        ServicePack servicePack = optionalServicePack.get();
        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getServices().getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(servicePack.getPrice() * quantity);
        cartItem.setTotalValidityPeriod(servicePack.getValidityPeriod() * quantity);
        cartItemRepository.save(cartItem);
        long totalQuantity = cartItemRepository.sumQuantityByCart(cart.getId());
        double totalPrice = cartItemRepository.sumTotalPriceByCart(cart.getId());
        long totalValidityPeriod = cartItemRepository.sumValidityPeriodByCart(cart.getId());
        cart.setTotalValidityPeriod(totalValidityPeriod);
        cart.setTotalItems(totalQuantity);
        cart.setTotalAmounts(totalPrice);
        return cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void deleteItemToCart(Long serviceId, Admin admin) {
        if (admin == null) {
            throw new RuntimeException("Admin not found");
        }
        Cart cart = cartRepository.findByAdmin(admin);
        if (cart == null) {
            throw new RuntimeException("Cart not found");
        }
        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getServices().getId().equals(serviceId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("CartItem not found"));
        cart.getCartItems().remove(cartItem);
        cartItemRepository.delete(cartItem);
        long totalQuantity = cartItemRepository.sumQuantityByCart(cart.getId());
        double totalPrice = cartItemRepository.sumTotalPriceByCart(cart.getId());
        long totalValidityPeriod = cartItemRepository.sumValidityPeriodByCart(cart.getId());
        cart.setTotalValidityPeriod(totalValidityPeriod);
        cart.setTotalItems(totalQuantity);
        cart.setTotalAmounts(totalPrice);
        cartRepository.save(cart);
    }

    @Override
    public Cart getCartByEmployer(Admin admin) {
        return cartRepository.findByAdmin(admin);
    }
}
