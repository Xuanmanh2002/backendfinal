package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Address;

import java.util.List;

public interface IAddressService {
    List<Address> getAddress();

    Address createAddress(String name);
}