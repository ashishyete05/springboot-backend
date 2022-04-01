package com.example.service;

import com.example.model.Employee;
import com.example.repository.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    EmployeeRepo repo;

    public List<Employee> checkIfEmailExist(String email){
        System.out.println("Passed email : "+email);
        return repo.findByEmailId(email);

    }

    public Employee findByUserName(String userName){
        return repo.findByUserName(userName);
    }

}
