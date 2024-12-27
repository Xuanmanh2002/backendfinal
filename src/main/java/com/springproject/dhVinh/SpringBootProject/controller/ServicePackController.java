package com.springproject.dhVinh.SpringBootProject.controller;

import com.springproject.dhVinh.SpringBootProject.exception.ServicePackAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.model.ServicePack;
import com.springproject.dhVinh.SpringBootProject.response.ServicePackResponse;
import com.springproject.dhVinh.SpringBootProject.service.IServicePackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/service")
public class ServicePackController {
    private final IServicePackService service;

    @GetMapping("/all")
    public ResponseEntity<List<ServicePack>> getAllServicePack() {
        List<ServicePack> servicePacks = service.getAllServicePack();
        if(servicePacks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(servicePacks, HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createServicePack(@RequestBody ServicePackResponse servicePackRequest) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            service.createServicePack(
                    servicePackRequest.getServiceName(),
                    servicePackRequest.getPrice(),
                    servicePackRequest.getValidityPeriod(),
                    servicePackRequest.getBenefit(),
                    servicePackRequest.getDisplayPosition(),
                    servicePackRequest.getDescription()
            );

            responseBody.put("message", "New service created successfully");
            responseBody.put("status", HttpStatus.OK.getReasonPhrase());
            return ResponseEntity.ok(responseBody);
        } catch (ServicePackAlreadyExistsException ex) {
            responseBody.put("error", ex.getMessage());
            responseBody.put("status", HttpStatus.CONFLICT.getReasonPhrase());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
        } catch (Exception ex) {
            responseBody.put("error", "An error occurred while creating the service.");
            responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    @DeleteMapping("/delete/{servicePackId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteServicePack(@PathVariable("servicePackId") Long servicePackId){
        service.deleteSericePack(servicePackId);
    }

    @PutMapping("/update/{servicePackId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateServicePack(
            @PathVariable("servicePackId") Long servicePackId,
            @RequestBody ServicePackResponse response) {
            Map<String, Object> result = new HashMap<>();
            try{
            ServicePack servicePack = service.updateServicePack(servicePackId, response.getServiceName(),
                    response.getPrice(),response.getValidityPeriod(),response.getBenefit(), response.getDisplayPosition(), response.getDescription());
            result.put("status", "success");
            result.put("message", "Service update successfully");
            result.put("service", servicePack);
            return ResponseEntity.ok(result);
        } catch (ServicePackAlreadyExistsException sv) {
            result.put("status", "error");
            result.put("message", sv.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
    }

    @GetMapping("/{servicePackId}")
    public ResponseEntity<Object> findById(@PathVariable("servicePackId") Long servicePackId) {
        try {
            ServicePack servicePack = service.findById(servicePackId);
            return new ResponseEntity<>(servicePack, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "ServicePack not found with ID: " + servicePackId);
            responseBody.put("status", HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
        }
    }

}
