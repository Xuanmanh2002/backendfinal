package com.springproject.dhVinh.SpringBootProject.repository;

import com.springproject.dhVinh.SpringBootProject.model.ServicePack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServicePackRepository extends JpaRepository <ServicePack, Long> {

    Optional<ServicePack> findByServiceName(String serviceName);

    boolean existsByServiceName(String serviceName);
}
