package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.CartItem;
import com.springproject.dhVinh.SpringBootProject.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService{

    private final CartItemRepository cartItemRepository;

    @Override
    public List<CartItem> getAllCartItemByCustomer(Long adminId) {
        return this.cartItemRepository.findByAdmin(adminId);
    }
}
