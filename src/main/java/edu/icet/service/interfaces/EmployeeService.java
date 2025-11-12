package edu.icet.service.interfaces;

import edu.icet.model.EmployeeDTO;

import java.util.List;

public interface EmployeeService {
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO getEmployeeById(String employeeId);
    boolean addEmployee(EmployeeDTO employee);
    boolean updateEmployee(EmployeeDTO employee);
    boolean deleteEmployee(String employeeId);
    List<EmployeeDTO> searchEmployees(String keyword);

}