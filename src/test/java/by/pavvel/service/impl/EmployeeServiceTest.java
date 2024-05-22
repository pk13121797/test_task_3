package by.pavvel.service.impl;

import by.pavvel.model.Employee;
import by.pavvel.exception.EmployeeNotFoundException;
import by.pavvel.service.EmployeeService;
import by.pavvel.utils.SimpleFileLoader;
import by.pavvel.repository.EmployeeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private SimpleFileLoader fileLoader;

    @Captor
    private ArgumentCaptor<Employee> employeeArgumentCaptor;

    private AutoCloseable autoCloseable;

    private EmployeeService underTest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new EmployeeServiceImpl(employeeRepository, fileLoader);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldGetEmployees() {
        // given
        Employee employee = new Employee("John", "123", "-", "-");
        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        // when
        List<Employee> employees = underTest.getEmployees();

        // then
        assertThat(employees)
                .hasSize(1)
                .contains(employee);
    }

    @Test
    void shouldShowEmployeesWhenQueryEqualsNull() {
        // given
        Employee employee = new Employee("John", "123", "-", "-");

        List<Employee> employeeList = List.of(employee);
        Page<Employee> page = mock(Page.class);
        when(page.getContent()).thenReturn(employeeList);
        when(employeeRepository.findAllWithImagesPageable(any(Pageable.class))).thenReturn(page);

        // when
        Page<Employee> employees = underTest.showEmployees(1, "name", "asc",null);

        // then
        assertThat(employees.getContent())
                .isEqualTo(employeeList)
                .hasSize(1);
    }

    @Test
    void shouldShowEmployeesWhenQueryNotEqualsNull() {
        // given
        String query = "oh";
        Employee employee = new Employee("John", "123", "-", "-");

        List<Employee> employeeList = List.of(employee);
        Page<Employee> page = mock(Page.class);
        when(page.getContent()).thenReturn(employeeList);
        when(employeeRepository.findEmployees(eq(query),any(Pageable.class))).thenReturn(page);

        // when
        Page<Employee> employees = underTest.showEmployees(1, "name", "asc",query);

        // then
        assertThat(employees.getContent())
                .isEqualTo(employeeList)
                .hasSize(1);
    }

    @Test
    void shouldGetEmployeesById() {
        // given
        Long employee1Id = 1L;
        Employee employee = new Employee("John", "123", "-", "-");
        when(employeeRepository.findAllById(List.of(employee1Id))).thenReturn(List.of(employee));

        // when
        List<Employee> employeesById = underTest.getEmployeesById(List.of(1L));

        // then
        assertThat(employeesById)
                .isNotNull()
                .contains(employee);
    }

    @Test
    void shouldGetEmployee() {
        // given
        Employee employee = new Employee("John", "123", "-", "-");
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        // when
        Employee employeeById = underTest.getEmployee(employee.getId());

        // then
        assertThat(employee).isEqualTo(employeeById);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotExists() {
        // given
        Employee employee = new Employee("John", "123", "-", "-");
        Long employeeId = 1L;
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getEmployee(employeeId))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining(String.format("Employee with id %s doesn't exist", employeeId));
    }

    @Test
    void shouldAddEmployee() {
        // given
        Employee employee = new Employee("John", "123", "-", "-");
        MultipartFile file = mock(MultipartFile.class);

        String uuid = UUID.randomUUID().toString();
        String resultFile = uuid + "." + file.getOriginalFilename();
        when(fileLoader.attachFile(file)).thenReturn("/" + resultFile);

        // when
        underTest.addEmployee(employee, new MultipartFile[]{file});

        // then
        then(employeeRepository).should().save(employeeArgumentCaptor.capture());
        Employee employeeArgumentCaptorValue = employeeArgumentCaptor.getValue();
        assertThat(employeeArgumentCaptorValue).isEqualTo(employee);
    }

    @Test
    void shouldUpdateEmployee() {
        // given
        ArgumentCaptor<Long> id = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> name = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> surname = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> middleName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> post = ArgumentCaptor.forClass(String.class);

        doNothing()
                .when(employeeRepository)
                .updateEmployee(
                        name.capture(),
                        surname.capture(),
                        middleName.capture(),
                        post.capture(),
                        id.capture()
                );

        // when
        Employee employeeToUpdate = new Employee("Pavel1", "Pavel1", "Pavel1", "-");
        underTest.updateEmployee(employeeToUpdate);

        // then
        verify(employeeRepository, times(1)).updateEmployee(
                employeeToUpdate.getName(),
                employeeToUpdate.getSurname(),
                employeeToUpdate.getMiddleName(),
                employeeToUpdate.getPost(),
                employeeToUpdate.getId()
        );

        assertThat(employeeToUpdate.getId()).isEqualTo(id.getValue());
        assertThat(employeeToUpdate.getName()).isEqualTo(name.getValue());
        assertThat(employeeToUpdate.getSurname()).isEqualTo(surname.getValue());
        assertThat(employeeToUpdate.getMiddleName()).isEqualTo(middleName.getValue());
        assertThat(employeeToUpdate.getPost()).isEqualTo(post.getValue());
    }

    @Test
    void shouldDeleteEmployee() {
        // given
        Employee employee = new Employee("John", "123", "-", "-");
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).deleteEmployeeById(employeeId);

        // when
        underTest.deleteEmployee(employeeId);

        // then
        verify(employeeRepository, times(1)).deleteEmployeeById(employeeId);
    }

    @Test
    void shouldNotDeleteEmployeeByIdWhenEmployeeNotExists() {
        // given
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteEmployee(employeeId))
                .isInstanceOf(EmployeeNotFoundException.class)
                .hasMessageContaining(String.format("Employee with id %s doesn't exist",employeeId));
        then(employeeRepository).should(never()).deleteEmployeeById(any());
    }

    @Test
    void shouldGetEmployeeProxy() {
        // given
        Long employeeId = 1L;
        Employee employeeProxy = new Employee(null, null, null, null);
        when(employeeRepository.getReferenceById(employeeId)).thenReturn(employeeProxy);

        // when
        Employee proxy = underTest.getEmployeeProxy(employeeId);

        // then
        assertThat(proxy.getId()).isEqualTo(employeeProxy.getId());
        assertThat(proxy.getName()).isNull();
    }
}