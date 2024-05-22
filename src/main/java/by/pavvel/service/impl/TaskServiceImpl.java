package by.pavvel.service.impl;

import by.pavvel.aspect.Timed;
import by.pavvel.exception.TaskNotFoundException;
import by.pavvel.model.Employee;
import by.pavvel.model.Project;
import by.pavvel.model.Task;
import by.pavvel.repository.TaskRepository;
import by.pavvel.service.ProjectService;
import by.pavvel.service.TaskService;
import by.pavvel.dto.TaskDto;
import by.pavvel.service.EmployeeService;
import by.pavvel.utils.TaskDtoMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static by.pavvel.service.impl.EmployeeServiceImpl.PAGE_SIZE;

@Service
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LogManager.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    private final TaskDtoMapper taskDtoMapper;

    private final EmployeeService employeeService;

    private final ProjectService projectService;

    public TaskServiceImpl(TaskRepository taskRepository,
                           TaskDtoMapper taskDtoMapper,
                           EmployeeService employeeService,
                           @Lazy ProjectService projectService) {  // proxy projectService
        this.taskRepository = taskRepository;
        this.taskDtoMapper = taskDtoMapper;
        this.employeeService = employeeService;
        this.projectService = projectService;
    }

    @Timed
    public Page<TaskDto> showTasks(int pageNo, String field, String direction) {
        logger.info("showTasks was called with params: {},{},{}", pageNo, field, direction);
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(field).ascending() : Sort.by(field).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, PAGE_SIZE, sort);
        Page<Task> tasksPage = taskRepository.findAllWithProjectPageable(pageable);
        tasksPage = taskRepository.findAllWithEmployeesPageable(pageable);
        return tasksPage.map(taskDtoMapper);
    }

    public List<Task> getTasks() {
        logger.info("getTasks was called:");
        return taskRepository.findAll();
    }

    public List<Task> getTasksById(List<Long> taskId) {
        logger.info("getTasksById was called: {}", taskId);
        return taskRepository.findAllById(taskId);
    }

    public Task getTask(Long taskId) {
        logger.info("getTask was called: {}", taskId);
        return taskRepository.findById(taskId).orElseThrow(() -> {
            TaskNotFoundException taskNotFoundException = new TaskNotFoundException(
                    String.format("Task with id %s not found", taskId)
            );
            logger.error("error in getTask: {}", taskId, taskNotFoundException);
            return taskNotFoundException;
        });
    }

    @Transactional
    public void addTask(Task task, Long projectId, List<Long> employeesIds) {
        logger.info("addTask was called with params: {},{}", projectId, employeesIds);
        bindProjectAndEmployeesToTask(task, projectId, employeesIds);
        taskRepository.save(task);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public void updateTask(Task task, Long projectId, List<Long> employeesIds) {
        logger.info("updateTask was called for task {} with params: {},{}", task.getId(), projectId, employeesIds);
        bindProjectAndEmployeesToTask(task, projectId, employeesIds);
        taskRepository.save(task);
        taskRepository.updateTask(
                task.getTitle(), task.getHours(),
                task.getStartDate(), task.getEndDate(),
                task.getStatus(), projectId, task.getId()
        );
    }

    private void bindProjectAndEmployeesToTask(Task task, Long projectId, List<Long> employeesIds) {
        bindProjectToTask(task, projectId);
        if (employeesIds != null) {
            bindEmployeesToTask(task, employeesIds);
        }
    }

    private void bindProjectToTask(Task task, Long projectId) {
        Project project = projectService.getProjectProxy(projectId);
        task.setProject(project);
    }

    private void bindEmployeesToTask(Task task, List<Long> employeesIds) {
        employeesIds.forEach(employeeId -> {
            Employee employee = employeeService.getEmployeeProxy(employeeId);
            task.addEmployee(employee);
        });
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public void deleteTask(Long taskId) {
        logger.info("deleteTask was called: {}", taskId);
        unbindEmployeesFromTask(taskId);
        taskRepository.deleteTaskById(taskId);
    }

    private void unbindEmployeesFromTask(Long taskId) {
        Task task = getTask(taskId);
        Set<Employee> employees = task.getEmployees();
        for (Employee employee : new HashSet<>(employees)) {
            task.removeEmployee(employee);
        }
    }

    public Task getTaskProxy(Long taskId) {
        logger.info("getTaskProxy was called with params: {}", taskId);
        return taskRepository.getReferenceById(taskId);
    }

    public List<Long> getSelectedEmployeeIds(Long taskId) {
        logger.info("getSelectedEmployeeIds was called with params: {}", taskId);
        List<Long> selectedIds = new ArrayList<>();
        employeeService.getEmployees()
                .stream()
                .filter(e -> getTaskProxy(taskId).getEmployees().contains(e))
                .forEach(e -> selectedIds.add(e.getId()));
        return selectedIds;
    }
}
