package com.springproject.dhVinh.SpringBootProject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springproject.dhVinh.SpringBootProject.model.Address;
import com.springproject.dhVinh.SpringBootProject.response.AddressResponse;
import com.springproject.dhVinh.SpringBootProject.service.IAddressService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/address")
public class AddressController {
    private final IAddressService addressService;
    private final ObjectMapper objectMapper;

    @GetMapping("/all")
    public ResponseEntity<List<Address>> getAllAddress(){
        List<Address> addresses = addressService.getAddress();
        if(addresses.isEmpty()){
            return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(addresses, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createCategory(@RequestBody AddressResponse response) {
        Map<String, Object> result = new HashMap<>();
        try {
            Address address = addressService.createAddress(response.getName());
            result.put("status", "success");
            result.put("message", "Address created successfully");
            result.put("category", address);
            return ResponseEntity.ok(result);
        } catch (RuntimeException cae) {
            result.put("status", "error");
            result.put("message", cae.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);
        }
    }
}
