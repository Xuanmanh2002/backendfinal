package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.ApplicationDocuments;
import com.springproject.dhVinh.SpringBootProject.repository.ApplicationDocumentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ADService implements IADService {

    private final ApplicationDocumentsRepository applicationDocumentsRepository;

    private final IAdminService adminService;

    @Override
    public ApplicationDocuments addAD(Admin admin, String fullname, String email, String telephone, String cv, String letter) {
        ApplicationDocuments applicationDocuments = new ApplicationDocuments();

        if (admin == null){
            throw new RuntimeException("Admin not fount");
        }

        applicationDocuments.setAdmins(admin);
        applicationDocuments.setFullName(fullname);
        applicationDocuments.setEmail(email);
        applicationDocuments.setTelephone(telephone);
        applicationDocuments.setCv(cv);
        applicationDocuments.setLetter(letter);
        return applicationDocumentsRepository.save(applicationDocuments);

    }
}
