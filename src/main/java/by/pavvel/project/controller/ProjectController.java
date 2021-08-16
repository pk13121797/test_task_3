package by.pavvel.project.controller;

import by.pavvel.project.entity.Project;
import by.pavvel.project.entity.Task;
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
@RequestMapping(path = "/project")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    @Autowired
    public ProjectController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @GetMapping
    public String projectsHomePage(Model model){
        return getProjects(1,"id","asc",model);
    }

    @GetMapping("/page/{pageNo}")
    public String getProjects(@PathVariable("pageNo") int pageNo,
                              @RequestParam("sortField") String field,
                              @RequestParam("sortDirection") String direction,
                              Model model){

        int pageSize = 4;
        Page<Project> page = projectService.showProjects(pageNo,pageSize,field,direction);
        List<Project> projects = page.getContent();

        model.addAttribute("page",page);
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalProjects",page.getTotalElements());
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("projects",projects);

        model.addAttribute("sortField",field);
        model.addAttribute("sortDirection",direction);
        model.addAttribute("sortReverse",direction.equals("asc") ? "desc" : "asc");
        return "project";
    }

    @GetMapping("/new")
    public String showProjectForm(@ModelAttribute("project") Project project,
                                  Model model){
        model.addAttribute("projects",projectService.getProjects());
        model.addAttribute("tasks",taskService.getTasks());
        return "project_form";
    }

    @PostMapping("/add")
    public String addProject(@ModelAttribute("project") @Valid Project project,
                             BindingResult bindingResult,
                             @RequestParam(value = "task",required = false) List<Long> tasks,
                             Model model){
        if (bindingResult.hasErrors()){
            model.addAttribute("projects",projectService.getProjects());
            model.addAttribute("tasks",taskService.getTasks());
            return "project_form";
        }

        projectService.addProject(project);

        if (tasks != null) {

            List<Task> chosenTasks = taskService.getTasksById(tasks);

            for(Task t : chosenTasks){
                t.setProject(project);
                taskService.addTask(t);
            }
        }

        return "redirect:/project";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateProjectForm(@PathVariable("id") Long id,
                                        Model model) {

        model.addAttribute("id",id);
        model.addAttribute("tasks",taskService.getTasks());
        model.addAttribute("project",projectService.getProject(id));
        return "project_update";
    }

    @PostMapping("/update")
    public String updateProject(@ModelAttribute("project") @Valid Project project,
                                BindingResult bindingResult,
                                @RequestParam(value = "task",required = false) List<Long> tasks,
                                Model model) {
        if (bindingResult.hasErrors()){
            model.addAttribute("id",project.getId());
            model.addAttribute("tasks",taskService.getTasks());
            model.addAttribute("project",project);
            return "project_update";
        }

        projectService.updateProject(project);

        if (tasks != null) {

            List<Task> chosenTasks = taskService.getTasksById(tasks);

            for(Task t : chosenTasks){

                t.setProject(project);
                taskService.addTask(t);
            }
        }
        return "redirect:/project";
    }

    @GetMapping("/remove/{id}")
    public String deleteProject(@PathVariable("id") Long id){

        Project project = projectService.getProject(id);
        List<Task> tasks = project.getTasks();

        for (Task t : tasks){
            taskService.deleteTask(t.getId());
            t.setProject(null);
        }

        projectService.deleteProject(id);
        return "redirect:/project";
    }
}
