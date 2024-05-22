package by.pavvel.service;

import by.pavvel.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {

    Page<Employee> showEmployees(int pageNo, String field, String direction, String q);

    List<Employee> getEmployees();

    List<Employee> getEmployeesById(List<Long> employeeId);

    Employee getEmployee(Long employeeId);

    void addEmployee(Employee employee, MultipartFile[] files);

    void updateEmployee(Employee employee);

    void deleteEmployee(Long employeeId);

    Employee getEmployeeProxy(Long employeeId);
}
