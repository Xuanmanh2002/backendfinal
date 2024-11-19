package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Cart;

public interface ICartService {

    Cart createItemToCart(Long serviceId, Long quantity, Admin admin);

    Cart updateItemToCart(Long serviceId, Long Quantity, Admin admin);

    void deleteItemToCart(Long serviceId, Admin admin);

    Cart getCartByEmployer(Admin admin);

}
