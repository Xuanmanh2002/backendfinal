package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.ServicePack;

import java.util.List;

public interface IServicePackService {
    List<ServicePack> getAllServicePack();

    ServicePack createServicePack(String serviceName, Double price, Long quantity, Long validityPeriod, String description);

    void deleteSericePack(Long servicePackId);

    ServicePack updateServicePack(Long servicePackId, String serviceName, Double price, Long quantity, Long validityPeriod, String description);
}
