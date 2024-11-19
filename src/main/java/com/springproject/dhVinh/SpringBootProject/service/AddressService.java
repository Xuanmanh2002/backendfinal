package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Address;
import com.springproject.dhVinh.SpringBootProject.repository.AddressRepository;
import com.springproject.dhVinh.SpringBootProject.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService implements IAddressService {

    private final AddressRepository addressRepository;

    @Override
    public List<Address> getAddress() {
        return addressRepository.findAll();
    }

    @Override
    public Address createAddress(String name) {
        Address newAress = new Address();
        newAress.setName(name);
        LocalDate createdDate = LocalDate.now();
        newAress.setCreateAt(createdDate);
        return addressRepository.save(newAress);
    }
}
