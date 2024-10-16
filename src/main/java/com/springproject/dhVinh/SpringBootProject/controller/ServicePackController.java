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
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ServicePack>> getAllServicePack() {
        List<ServicePack> servicePacks = service.getAllServicePack();
        if(servicePacks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(servicePacks, HttpStatus.OK);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, Object>> createServicePack(@RequestBody ServicePackResponse response) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            ServicePack servicePack = new ServicePack();
            service.createServicePack(response.getServiceName(), response.getPrice(), response.getQuantity(), response.getValidityPeriod(), response.getDescription());

            responseBody.put("message", "New service created successfully");
            responseBody.put("status", HttpStatus.OK.value());

            return ResponseEntity.ok(responseBody);
        } catch (ServicePackAlreadyExistsException sv) {
            responseBody.put("error", sv.getMessage());
            responseBody.put("status", HttpStatus.CONFLICT.value());

            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody);
        } catch (Exception e) {
            responseBody.put("error", "An error occurred while creating the Service.");
            responseBody.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());

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
                    response.getPrice(),response.getQuantity(),response.getValidityPeriod(),response.getDescription());
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

}
