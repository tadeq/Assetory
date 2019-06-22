package pl.edu.agh.assetory.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.agh.assetory.Repository.EmployeeRepository;
import pl.edu.agh.assetory.model.Employee;


@Service
public class EmployeeService {

    @Autowired
    @Qualifier("employeeRepository")
    private EmployeeRepository employeeRepository;

    public List<Employee> findByDesignation(String designation){
        return employeeRepository.findByDesignation(designation);
    }

    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Iterable<Employee> getAllEmployee() {
        return employeeRepository.findAll();
    }

}