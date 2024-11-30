package com.springproject.dhVinh.SpringBootProject.service;

import com.springproject.dhVinh.SpringBootProject.model.Admin;
import com.springproject.dhVinh.SpringBootProject.model.Notification;

import java.util.List;

public interface INotificationService {

    List<Notification> findNotificationsByRole(Admin admin);

}
