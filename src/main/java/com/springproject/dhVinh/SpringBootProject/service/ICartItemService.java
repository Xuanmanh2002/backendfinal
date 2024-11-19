package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Cart;
import com.springproject.dhVinh.SpringBootProject.model.CartItem;

import java.util.List;

public interface ICartItemService {

    List<CartItem> getAllCartItemByCustomer(Long adminId);

}
