package by.pavvel.service.impl;

import by.pavvel.aspect.Timed;
import by.pavvel.exception.EmployeeNotFoundException;
import by.pavvel.utils.FileLoader;
import by.pavvel.repository.EmployeeRepository;
import by.pavvel.model.Employee;
import by.pavvel.service.EmployeeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LogManager.getLogger(EmployeeServiceImpl.class);

    public static final int PAGE_SIZE = 5;

    private final EmployeeRepository employeeRepository;

    private final FileLoader fileLoader;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, @Qualifier("simpleLoader") FileLoader fileLoader) {
        this.employeeRepository = employeeRepository;
        this.fileLoader = fileLoader;
    }

    @Timed // custom annotation to measure time execution
    public Page<Employee> showEmployees(int pageNo, String field, String direction, String query) {
        logger.info("showEmployees was called with params: {},{},{}", pageNo, field, direction);
        PageRequest page = getPage(pageNo, field, direction);
        return query == null ? employeeRepository.findAllWithImagesPageable(page) :
                employeeRepository.findEmployees(query, page);
    }

    public static PageRequest getPage(int pageNo, String field, String direction) {
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(field).ascending() : Sort.by(field).descending();
        return PageRequest.of(pageNo -1, PAGE_SIZE, sort);
    }

    public List<Employee> getEmployees() {
        logger.info("getEmployees was called:");
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployeesById(List<Long> employeeId) {
        logger.info("getEmployeesById was called: {}", employeeId);
        return employeeRepository.findAllById(employeeId);
    }

    public Employee getEmployee(Long employeeId) {
        logger.info("getEmployee was called for employee: {}", employeeId);
        return employeeRepository.findById(employeeId).orElseThrow(() -> {
            EmployeeNotFoundException employeeNotFoundException = new EmployeeNotFoundException(
                    String.format("Employee with id %s doesn't exist", employeeId)
            );
            logger.error("error in getEmployee: {}", employeeId, employeeNotFoundException);
            return employeeNotFoundException;
        });
    }

    @Transactional
    public void addEmployee(Employee employee, MultipartFile[] files) {
        logger.info("addEmployee was called for employee: {}", employee.getId());
        Set<String> acceptedFiles = new HashSet<>();
        Arrays.stream(files).forEach(file -> {
            if (!file.isEmpty()) {
                String attachedFile = fileLoader.attachFile(file);
                acceptedFiles.add(attachedFile);
            }
        });
        employee.setImages(acceptedFiles);
        employeeRepository.save(employee);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public void updateEmployee(Employee employee) {
        logger.info("updateEmployee was called for employee: {}",employee.getId());
        employeeRepository.updateEmployee(
                employee.getName(), employee.getSurname(),
                employee.getMiddleName(), employee.getPost(),
                employee.getId()
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public void deleteEmployee(Long employeeId) {
        logger.info("deleteEmployee was called for employee: {}", employeeId);
        unbindImagesFromEmployee(employeeId);
        employeeRepository.deleteEmployeeById(employeeId);
    }

    private void unbindImagesFromEmployee(Long employeeId) {
        getEmployee(employeeId).setImages(null);
    }

    public Employee getEmployeeProxy(Long employeeId) {
        logger.info("getEmployeeProxy was called: {}", employeeId);
        return employeeRepository.getReferenceById(employeeId);
    }
}
