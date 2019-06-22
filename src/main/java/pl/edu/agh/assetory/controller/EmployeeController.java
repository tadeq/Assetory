package pl.edu.agh.assetory.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.edu.agh.assetory.model.Employee;
import pl.edu.agh.assetory.service.EmployeeService;

@RestController
@RequestMapping(value = "/employee")
public class EmployeeController {

    private EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(value = "/add")
    public String addEmployee(@RequestBody Employee myEmployee) {
        employeeService.addEmployee(myEmployee);
        return "Records saved in the db.";
    }

    @GetMapping(value = "/all")
    public Iterable<Employee> getAllEmployee() {
        return employeeService.getAllEmployee();
    }
}