package by.pavvel.controller;

import by.pavvel.model.Project;
import by.pavvel.service.ProjectService;
import by.pavvel.service.TaskService;
import by.pavvel.utils.ProjectValidator;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(path = "/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final ProjectValidator projectValidator;

    public ProjectController(ProjectService projectService, TaskService taskService, ProjectValidator projectValidator) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.projectValidator = projectValidator;
    }

    @GetMapping
    public String projectsHomePage(Model model){
        return getProjects(1,"id","asc", model);
    }

    @GetMapping("/page/{pageNo}")
    public String getProjects(@PathVariable("pageNo") int pageNo,
                              @RequestParam("sortField") String field,
                              @RequestParam("sortDirection") String direction,
                              Model model){

        Page<Project> page = projectService.showProjects(pageNo,field,direction);
        List<Project> projects = page.getContent();

        model.addAttribute("page",page);
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("projects",projects);
        model.addAttribute("sortField",field);
        model.addAttribute("sortDirection",direction);
        model.addAttribute("sortReverse",direction.equals("asc") ? "desc" : "asc");
        return "projects/projects";
    }

    @GetMapping("/new")
    public String showProjectForm(@ModelAttribute("project") Project project, Model model){
        model.addAttribute("tasks",taskService.getTasks());
        return "projects/add_form";
    }

    @PostMapping
    public String addProject(@ModelAttribute("project") @Valid Project project,
                             BindingResult bindingResult,
                             @RequestParam(value = "task",required = false) List<Long> tasksIds,
                             Model model){

        projectValidator.validate(project, bindingResult);
        if (bindingResult.hasErrors()){
            model.addAttribute("projects",projectService.getProjects());
            model.addAttribute("tasks",taskService.getTasks());
            return "projects/add_form";
        }
        projectService.addProject(project,tasksIds);
        return "redirect:/projects";
    }

    @GetMapping("/edit/{projectId}")
    public String showUpdateProjectForm(@PathVariable("projectId") Long projectId, Model model) {
        model.addAttribute("id",projectId);
        model.addAttribute("tasks",taskService.getTasks());
        model.addAttribute("project",projectService.getProject(projectId));
        model.addAttribute("selectedIds",projectService.getSelectedTaskIds(projectId));
        return "projects/edit_form";
    }

    @PutMapping
    public String updateProject(@ModelAttribute("project") @Valid Project project,
                                BindingResult bindingResult,
                                @RequestParam(value = "task") List<Long> tasksIds,
                                Model model) {
        if (bindingResult.hasErrors()){
            model.addAttribute("id",project.getId());
            model.addAttribute("tasks",taskService.getTasks());
            model.addAttribute("project",project);
            model.addAttribute("selectedIds",projectService.getSelectedTaskIds(project.getId()));
            return "projects/edit_form";
        }
        projectService.updateProject(project,tasksIds);
        return "redirect:/projects";
    }

    @DeleteMapping("/{projectId}")
    public String deleteProject(@PathVariable("projectId") Long projectId){
        projectService.deleteProject(projectId);
        return "redirect:/projects";
    }
}
