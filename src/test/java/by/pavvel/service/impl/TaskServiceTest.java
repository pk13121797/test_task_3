package by.pavvel.service.impl;

import by.pavvel.dto.TaskDto;
import by.pavvel.exception.TaskNotFoundException;
import by.pavvel.model.Employee;
import by.pavvel.model.Project;
import by.pavvel.model.Status;
import by.pavvel.model.Task;
import by.pavvel.repository.TaskRepository;
import by.pavvel.service.EmployeeService;
import by.pavvel.service.ProjectService;
import by.pavvel.service.TaskService;
import by.pavvel.utils.TaskDtoMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private ProjectService projectService;

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;

    private final TaskDtoMapper taskDtoMapper = new TaskDtoMapper();

    private AutoCloseable autoCloseable;

    private TaskService underTest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new TaskServiceImpl(taskRepository, taskDtoMapper, employeeService, projectService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldShowTasks() throws ClassNotFoundException, NoSuchFieldException {
        // given
        Page<Task> page = mock(Page.class);
        Page<TaskDto> tasksPage = mock(Page.class);
        when(taskRepository.findAllWithProjectPageable(any(Pageable.class))).thenReturn(page);
        when(taskRepository.findAllWithEmployeesPageable(any(Pageable.class))).thenReturn(page);
        when(page.map(taskDtoMapper)).thenReturn(tasksPage);

        // when
        Page<TaskDto> showTasks = underTest.showTasks(1, String.valueOf(Class.forName("by.pavvel.model.Task").getDeclaredField("title")), "desc");

        // then
        assertThat(showTasks).isNotNull().isEqualTo(tasksPage);
    }

    @Test
    void shouldGetTasks() {
        // given
        Task task1 = new Task("task1", null, null, null,null);
        Task task2 = new Task("task2", null, null, null,null);
        when(taskRepository.findAll()).thenReturn(List.of(task1,task2));

        // when
        List<Task> tasks = underTest.getTasks();

        // then
        assertThat(tasks).isNotNull().hasSize(2);
    }

    @Test
    void shouldGetTasksById() {
        // given
        Long taskId = 1L;
        Task task = new Task("task1", null, null, null,null);
        when(taskRepository.findAllById(List.of(taskId))).thenReturn(List.of(task));

        // when
        List<Task> tasksById = underTest.getTasksById(List.of(taskId));

        // then
        assertThat(tasksById)
                .isNotNull()
                .contains(task)
                .hasSize(1);
    }

    @Test
    void shouldGetTask() {
        // given
        Task task = new Task("task1", null, null, null,null);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        // when
        Task taskById = underTest.getTask(task.getId());

        // then
        assertThat(taskById).isNotNull().isEqualTo(task);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotExists() {
        // given
        Task task = new Task("task1", null, null, null,null);
        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getTask(task.getId()))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(String.format(
                        "Task with id %s not found", task.getId())
                );
    }

    @Test
    void shouldAddTask() {
        // given
        Project project = new Project("title1", "AA", "desc1");
        Long employeeId = 1L;
        Employee employee = new Employee("John", "123", "-", "-");

        Task task = new Task("task1", null, null, null,null);
        when(projectService.getProjectProxy(project.getId())).thenReturn(project);
        when(employeeService.getEmployeeProxy(employeeId)).thenReturn(employee);

        // when
        underTest.addTask(task, project.getId(), List.of(employeeId));

        // then
        then(taskRepository).should().save(taskArgumentCaptor.capture());
        Task taskArgumentCaptorValue = taskArgumentCaptor.getValue();
        assertThat(taskArgumentCaptorValue).isEqualTo(task);
        assertThat(taskArgumentCaptorValue.getEmployees().size()).isEqualTo(1);
        assertThat(taskArgumentCaptorValue.getProject()).isEqualTo(project);
    }

    @Test
    void shouldUpdateTask() {
        // given
        Project project = new Project("title1", "AA", "desc1");
        project.setId(1L);

        Employee employee = new Employee("John", "123", "-", "-");
        employee.setId(1L);

        Task task = new Task("task1", null, null, null,null);
        List<Long> employeeIds = List.of(1L);

        project.addTask(task);
        task.addEmployee(employee);

        ArgumentCaptor<Long> id = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> hours = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<LocalDate> startDate = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<LocalDate> endDate = ArgumentCaptor.forClass(LocalDate.class);
        ArgumentCaptor<Status> status = ArgumentCaptor.forClass(Status.class);
        ArgumentCaptor<Long> projectId = ArgumentCaptor.forClass(Long.class);

        when(projectService.getProjectProxy(project.getId())).thenReturn(project);
        when(employeeService.getEmployeeProxy(employee.getId())).thenReturn(employee);
        when(taskRepository.save(task)).thenReturn(task);
        doNothing().when(taskRepository).updateTask(
                title.capture(),hours.capture(),
                startDate.capture(), endDate.capture(),
                status.capture(), projectId.capture(), id.capture()
        );

        // when
        Task taskToUpdate = new Task("task2", 8, null, null, Status.completed);
        Long projectIdToUpdate = 2L;
        underTest.updateTask(taskToUpdate, projectIdToUpdate, employeeIds);

        // then
        verify(taskRepository, times(1)).updateTask(
                taskToUpdate.getTitle(),
                taskToUpdate.getHours(),
                taskToUpdate.getStartDate(),
                taskToUpdate.getEndDate(),
                taskToUpdate.getStatus(),
                projectIdToUpdate,
                taskToUpdate.getId()
        );

        assertThat(taskToUpdate.getTitle()).isEqualTo(title.getValue());
        assertThat(taskToUpdate.getHours()).isEqualTo(hours.getValue());
        assertThat(taskToUpdate.getStartDate()).isEqualTo(startDate.getValue());
        assertThat(taskToUpdate.getEndDate()).isEqualTo(endDate.getValue());
        assertThat(taskToUpdate.getStatus()).isEqualTo(status.getValue());
        assertThat(projectIdToUpdate).isEqualTo(projectId.getValue());
        assertThat(taskToUpdate.getId()).isEqualTo(id.getValue());
    }

    @Test
    void shouldDeleteTask() {
        // given
        Long taskId = 1L;
        Task task = new Task("task1", null, null, null,null);

        Employee employee = new Employee("John", "123", "-", "-");
        employee.setId(1L);
        task.addEmployee(employee);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).deleteTaskById(taskId);

        // when
        assertThat(task.getEmployees().size()).isEqualTo(1);
        underTest.deleteTask(taskId);

        // then
        verify(taskRepository,times(1)).deleteTaskById(taskId);
        assertThat(task.getEmployees().size()).isEqualTo(0);
    }

    @Test
    void shouldGetTaskProxy() {
        // given
        Long taskId = 1L;
        Task taskProxy = new Task(null, null, null, null,null);
        when(taskRepository.getReferenceById(taskId)).thenReturn(taskProxy);

        // when
        Task proxy = underTest.getTaskProxy(taskId);

        // then
        assertThat(proxy.getId()).isEqualTo(taskProxy.getId());
        assertThat(proxy.getTitle()).isNull();
    }

    @Test
    void shouldGetSelectedEmployeeIdsByTaskId() {
        // given
        Long taskId = 1L;
        Task task = new Task("task1", null, null, null,null);
        Employee employee1 = new Employee("John123", "123", "-", "-");
        Employee employee2 = new Employee("John456", "456", "-", "-");

        task.addEmployee(employee1);
        task.addEmployee(employee2);

        when(employeeService.getEmployees()).thenReturn(List.of(employee1,employee2));
        when(taskRepository.getReferenceById(taskId)).thenReturn(task);

        // when
        List<Long> selectedEmployeeIds = underTest.getSelectedEmployeeIds(taskId);

        // then
        assertThat(selectedEmployeeIds.size()).isEqualTo(2);
    }
}