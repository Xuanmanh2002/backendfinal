package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.exception.ServicePackAlreadyExistsException;
import com.springproject.dhVinh.SpringBootProject.model.ServicePack;
import com.springproject.dhVinh.SpringBootProject.repository.ServicePackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ServicePackService implements IServicePackService {

    private final ServicePackRepository servicePackRepository;

    @Override
    public List<ServicePack> getAllServicePack() {
        return servicePackRepository.findAll();
    }

    @Override
    public ServicePack createServicePack(String serviceName, Double price,  Long validityPeriod, String description) {
        if(servicePackRepository.existsByServiceName(serviceName)) {
            throw new ServicePackAlreadyExistsException(serviceName + "serviceName already exists");
        }
        ServicePack servicePack = new ServicePack();
        servicePack.setServiceName(serviceName);
        servicePack.setPrice(price);
        servicePack.setDescription(description);
        servicePack.setValidityPeriod(validityPeriod);
        LocalDate createDate = LocalDate.now();
        servicePack.setCreateAt(createDate);
        return servicePackRepository.save(servicePack);
    }

    @Override
    public void deleteSericePack(Long servicePackId) {
        Optional<ServicePack> servicePack = servicePackRepository.findById(servicePackId);
        if(servicePack.isPresent()) {
            servicePackRepository.deleteById(servicePackId);
        }else {
            throw new ServicePackAlreadyExistsException("Service with ID " + servicePackId +" not found");
        }
    }

    @Override
    public ServicePack updateServicePack(Long servicePackId, String serviceName, Double price,  Long validityPeriod, String description) {
        Optional<ServicePack> optionalServicePack = servicePackRepository.findById(servicePackId);
        if(optionalServicePack.isPresent()) {
            ServicePack servicePack = optionalServicePack.get();
            servicePack.setServiceName(serviceName);
            servicePack.setPrice(price);
            servicePack.setValidityPeriod(validityPeriod);
            servicePack.setDescription(description);
            return servicePackRepository.save(servicePack);
        } else {
            throw new ServicePackAlreadyExistsException("Service with ID " + servicePackId + " not found.");
        }
    }

    @Override
    public ServicePack findById(Long servicePackId) {
        Optional<ServicePack> servicePack = servicePackRepository.findById(servicePackId);
        return servicePack.orElse(null);
    }
}
