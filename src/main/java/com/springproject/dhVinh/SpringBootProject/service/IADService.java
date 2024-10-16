package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.ApplicationDocuments;

public interface IADService {
    ApplicationDocuments addAD(Admin admin, String fullname, String email, String telephone, String cv, String letter);
}
