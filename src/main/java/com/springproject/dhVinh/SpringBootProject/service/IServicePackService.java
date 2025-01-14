package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.ServicePack;

import java.util.List;

public interface IServicePackService {
    List<ServicePack> getAllServicePack();

    ServicePack createServicePack(String serviceName, Double price,  Long validityPeriod, Long benefit, String displayPosition, String description);

    void deleteSericePack(Long servicePackId);

    ServicePack updateServicePack(Long servicePackId, String serviceName, Double price,  Long validityPeriod, Long benefit, String displayPosition, String description);

    ServicePack findById(Long servicePackId);
}
