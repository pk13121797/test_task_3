package by.pavvel.controller;

import by.pavvel.model.Status;
import by.pavvel.model.Task;
import by.pavvel.service.ProjectService;
import by.pavvel.service.TaskService;
import by.pavvel.dto.TaskDto;
import by.pavvel.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(path = "/tasks")
public class TaskController {

    private final TaskService taskService;
    private final EmployeeService employeeService;
    private final ProjectService projectService;

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

        Page<TaskDto> page = taskService.showTasks(pageNo,field,direction);
        List<TaskDto> tasks = page.getContent();

        model.addAttribute("page",page);
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("tasks",tasks);
        model.addAttribute("sortField",field);
        model.addAttribute("sortDirection",direction);
        model.addAttribute("sortReverse",direction.equals("asc") ? "desc" : "asc");
        return "tasks/tasks";
    }

    @GetMapping("/new")
    public String showTaskForm(@ModelAttribute("task") Task task, Model model){

        model.addAttribute("employees",employeeService.getEmployees());
        model.addAttribute("projects",projectService.getProjects());
        model.addAttribute("statuses", Status.values());
        return "tasks/add_form";
    }

    @PostMapping
    public String addTask(@ModelAttribute("task") @Valid Task task,
                          BindingResult bindingResult,
                          @RequestParam(value = "projectId") Long projectId,
                          @RequestParam(value = "employee") List<Long> employeesIds,
                          Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("tasks",taskService.getTasks());
            model.addAttribute("employees",employeeService.getEmployees());
            model.addAttribute("projects",projectService.getProjects());
            model.addAttribute("statuses",Status.values());
            return "tasks/add_form";
        }
        taskService.addTask(task,projectId,employeesIds);
        return "redirect:/tasks";
    }

    @GetMapping("/edit/{taskId}")
    public String showUpdateTaskForm(@PathVariable("taskId") Long taskId,Model model) {

        model.addAttribute("id",taskId);
        model.addAttribute("employees",employeeService.getEmployees());
        model.addAttribute("projects",projectService.getProjects());
        model.addAttribute("task",taskService.getTask(taskId));
        model.addAttribute("selectedIds",taskService.getSelectedEmployeeIds(taskId));
        model.addAttribute("statuses",Status.values());
        return "tasks/edit_form";
    }

    @PutMapping
    public String updateTask(@ModelAttribute("task") @Valid Task task,
                             BindingResult bindingResult,
                             @RequestParam("projectId") Long projectId,
                             @RequestParam(value = "employee") List<Long> employeesIds,
                             Model model) {
        if (bindingResult.hasErrors()){
            model.addAttribute("id",task.getId());
            model.addAttribute("employees",employeeService.getEmployees());
            model.addAttribute("projects",projectService.getProjects());
            model.addAttribute("task",task);
            model.addAttribute("selectedIds",taskService.getSelectedEmployeeIds(task.getId()));
            model.addAttribute("statuses",Status.values());
            return "tasks/edit_form";
        }
        taskService.updateTask(task,projectId,employeesIds);
        return "redirect:/tasks";
    }

    @DeleteMapping("/{taskId}")
    public String deleteTask(@PathVariable("taskId") Long taskId){
        taskService.deleteTask(taskId);
        return "redirect:/tasks";
    }
}
