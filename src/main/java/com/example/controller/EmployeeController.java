package com.example.controller;

import com.example.exception.ResourceNotFoundException;
import com.example.model.AuthenticationRequest;
import com.example.model.AuthenticationResponse;
import com.example.model.EmpValidationBean;
import com.example.model.Employee;
import com.example.repository.EmployeeRepo;
import com.example.service.EmployeeService;
import com.example.utility.JwtUtil;
import com.example.utility.Status;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

    Logger log = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    EmployeeRepo repository;

    @Autowired
    EmployeeService empService;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/employees")
    public List<Employee> getAllEmployees() {
        log.info("getAllEmployees method called ..");
        return repository.findAll();
    }

    @GetMapping("/employee/{id}")
    public ResponseEntity<Employee> getEmployee(@PathVariable Long id){
        log.info("getEmployee method called for id :- "+id);
        Employee emp = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Employee with EmpId : "+id +" do not exist"));
        return ResponseEntity.ok(emp);
    }

    @PostMapping("/employee")
    public ResponseEntity<Employee> saveEmployee(@RequestBody Employee emp){
        log.info("save Employee method called ");
        Employee e = repository.save(emp);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/{id}").buildAndExpand(e.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/checkEmailAvailability/{email}")
    public Employee checkEmailAvailability(@PathVariable String email) {
        log.info("checkEmailAvailability method called ");
        if (empService.checkIfEmailExist(email).size() > 0) {
            log.info("checkEmailAvailability method called -- condition-1");
            return empService.checkIfEmailExist(email).get(0);
        }
        else {
            log.info("checkEmailAvailability method called condition-2");
            return null;
        }

    }

    @PutMapping("/employee/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @RequestBody Employee emp){
        Employee employee = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Employee with EmpId : "+id +" do not exist"));
        employee.setFirstName(emp.getFirstName());
        employee.setLastName(emp.getLastName());
        employee.setEmailId(emp.getEmailId());

        Employee updatedEmp = repository.save(employee);
        return ResponseEntity.ok(updatedEmp);

    }

    @DeleteMapping("/employee/{id}")
    public ResponseEntity<Map<String,Boolean>> deleteEmployee(@PathVariable  Long id){
        log.info("Delete Employee method called for id : "+id);
        Employee employee = repository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Employee with EmpId : "+id +" do not exist"));
        repository.delete(employee);
        Map<String,Boolean> response = new HashMap<>();
        response.put("Employee Deleted with id : "+id, Boolean.TRUE);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/employee/login")
    public Employee loginUser(@RequestBody EmpValidationBean employee){
        Employee emp = empService.findByUserName(employee.getUsername());
        if(emp!=null){
            log.info("Emp Password :  "+emp.getPassword() + " Employee password : "+employee.getPassword());
            if(emp.getPassword().equals(employee.getPassword())){
                log.info("Authentication is successful ");
                return emp;
            }else{
                log.info("Authentication failed ");
                return null;
            }
        }
        return  null;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authRequest) throws Exception {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new Exception("Incorrect UserName or Password", ex);
        }
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        log.info("Created jwt token : "+jwt);
        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}
