package by.pavvel.project.service.impl;

import by.pavvel.project.entity.Employee;
import by.pavvel.project.repository.EmployeeRepository;
import by.pavvel.project.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Page<Employee> showEmployees(int pageNo, int pageSize, String field, String direction){

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(field).ascending() :
                Sort.by(field).descending();

        PageRequest pageRequest = PageRequest.of(pageNo-1,pageSize,sort);
        return employeeRepository.findAll(pageRequest);
    }

    public List<Employee> getEmployees(){
        return employeeRepository.findAll();
    }

    @Override
    public List<Employee> getEmployeesById(List<Long> id) {
        return employeeRepository.findAllById(id);
    }

    public Employee getEmployee(Long id){
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Employee doesn't exists"));
    }

    @Transactional
    public void addEmployee(Employee employee){
        employeeRepository.save(employee);
    }

    @Transactional
    public void updateEmployee(Employee employee) {
        employeeRepository.findById(employee.getId())
                .orElseThrow(() -> new IllegalStateException(String.format("Employee with id %s doesn't exists",employee.getId())));
        employeeRepository.updateEmployee(employee.getName(), employee.getSurname(),
                employee.getMiddleName(), employee.getPost(), employee.getId());
    }

    @Transactional
    public void deleteEmployee(Long id){
        Optional<Employee> byId = employeeRepository.findById(id);
        if (!byId.isPresent()){
            throw new IllegalStateException(String.format("Employee with id %s doesn't exist",id));
        }
        employeeRepository.deleteEmployeeById(id);
    }
}
