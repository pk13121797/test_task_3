package by.pavvel.project.controller;

import by.pavvel.project.entity.Employee;
import by.pavvel.project.entity.Project;
import by.pavvel.project.entity.Status;
import by.pavvel.project.entity.Task;
import by.pavvel.project.service.EmployeeService;
import by.pavvel.project.service.ProjectService;
import by.pavvel.project.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(path = "/task")
public class TaskController {

    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final ProjectService projectService;

    @Autowired
    public TaskController(TaskService taskService, EmployeeService employeeService, ProjectService projectService) {
        this.taskService = taskService;
        this.employeeService = employeeService;
        this.projectService = projectService;
    }

    @GetMapping
    public String tasksHomePage(Model model){
        return getTasks(1,"id","asc",model);
    }

    @GetMapping("/page/{pageNo}")
    public String getTasks(@PathVariable("pageNo") int pageNo, @RequestParam("sortField") String field,
                              @RequestParam("sortDirection") String direction, Model model){

        int pageSize = 6;
        Page<Task> page = taskService.showTasks(pageNo,pageSize,field,direction);
        List<Task> tasks = page.getContent();

        model.addAttribute("page",page);
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalProjects",page.getTotalElements());
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("tasks",tasks);

        model.addAttribute("sortField",field);
        model.addAttribute("sortDirection",direction);
        model.addAttribute("sortReverse",direction.equals("asc") ? "desc" : "asc");
        return "task";
    }

    @GetMapping("/new")
    public String showTaskForm(@ModelAttribute("task") Task task,Model model){
        model.addAttribute("tasks",taskService.getTasks());
        model.addAttribute("employees",employeeService.getEmployees());
        model.addAttribute("projects",projectService.getProjects());
        model.addAttribute("statuses",Status.values());
        return "task_form";
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute("task") @Valid Task task,
                          BindingResult bindingResult,
                          @RequestParam("projectId") Long projectId,
                          @RequestParam("employee") List<Long> employees,
                          Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("tasks",taskService.getTasks());
            model.addAttribute("employees",employeeService.getEmployees());
            model.addAttribute("projects",projectService.getProjects());
            model.addAttribute("statuses",Status.values());
            return "task_form";
        }

        Project project = projectService.getProject(projectId);
        task.setProject(project);

        taskService.addTask(task);

        List<Employee> chosenEmployees = employeeService.getEmployeesById(employees);

        for (Employee e : chosenEmployees) {

            e.addTask(task);
            employeeService.addEmployee(e);
        }
        return "redirect:/task";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateTaskForm(@PathVariable("id") Long id,Model model) {

        model.addAttribute("id",id);
        model.addAttribute("projects",projectService.getProjects());
        model.addAttribute("employees",employeeService.getEmployees());
        model.addAttribute("task",taskService.getTask(id));
        model.addAttribute("statuses",Status.values());
        return "task_update";
    }

    @PostMapping("/update")
    public String updateTask(@ModelAttribute("task") @Valid Task task,
                             BindingResult bindingResult,
                             @RequestParam("projectId") Long projectId,
                             @RequestParam("employee") List<Long> employees,
                             Model model) {
        if (bindingResult.hasErrors()){
            model.addAttribute("id",task.getId());
            model.addAttribute("projects",projectService.getProjects());
            model.addAttribute("employees",employeeService.getEmployees());
            model.addAttribute("task",task);
            model.addAttribute("statuses",Status.values());
            return "task_update";
        }

        Project project = projectService.getProject(projectId);
        task.setProject(project);

        taskService.updateTask(task);

        List<Employee> chosenEmployees = employeeService.getEmployeesById(employees);

        for (Employee e : chosenEmployees) {

            e.addTask(task);
            employeeService.addEmployee(e);
        }
        return "redirect:/task";
    }

    @GetMapping("/remove/{id}")
    public String deleteTask(@PathVariable("id") Long id){

        taskService.deleteTask(id);
        return "redirect:/task";
    }
}
