package by.pavvel.project.service;

import by.pavvel.project.entity.Employee;
import org.springframework.data.domain.Page;

import java.util.List;

public interface EmployeeService {

    Page<Employee> showEmployees(int pageNo, int pageSize, String field, String direction);

    List<Employee> getEmployees();

    List<Employee> getEmployeesById(List<Long> id);

    Employee getEmployee(Long id);

    void addEmployee(Employee employee);

    void updateEmployee(Employee employee);

    void deleteEmployee(Long id);
}
