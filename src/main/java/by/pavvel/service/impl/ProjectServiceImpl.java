package by.pavvel.service.impl;

import by.pavvel.aspect.Timed;
import by.pavvel.model.Project;
import by.pavvel.model.Task;
import by.pavvel.exception.ProjectNotFoundException;
import by.pavvel.repository.ProjectRepository;
import by.pavvel.service.ProjectService;
import by.pavvel.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static by.pavvel.service.impl.EmployeeServiceImpl.getPage;

@Service
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;

    private final TaskService taskService;

    public ProjectServiceImpl(ProjectRepository projectRepository, TaskService taskService) {
        this.projectRepository = projectRepository;
        this.taskService = taskService;
    }

    @Timed
    public Page<Project> showProjects(int pageNo, String field, String direction) {
        logger.info("showProjects was called with params: {},{},{}", pageNo, field, direction);
        PageRequest page = getPage(pageNo, field, direction);
        return projectRepository.findAll(page);
    }

    public List<Project> getProjects() {
        logger.info("getProjects was called:");
        return projectRepository.findAll();
    }

    public Project getProject(Long projectId) {
        logger.info("getProject was called: {}", projectId);
        return projectRepository.findProjectById(projectId).orElseThrow(() -> {
            ProjectNotFoundException projectNotFoundException = new ProjectNotFoundException(
                    String.format("Project with id %s doesn't exists", projectId)
            );
            logger.error("error in getProject: {}", projectId, projectNotFoundException);
            return projectNotFoundException;
        });
    }

    @Transactional
    public void addProject(Project project,List<Long> tasksIds) {
        logger.info("addProject was called: {}", tasksIds);
        bindTasksToProject(project, tasksIds);
        projectRepository.save(project);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public void updateProject(Project project,List<Long> tasksIds) {
        logger.info("updateProject was called for project {} with params: {}", project.getId(), tasksIds);
        bindTasksToProject(project, tasksIds);
        projectRepository.updateProject(project.getTitle(), project.getAbbreviation(),
                project.getDescription(), project.getId());
    }

    private void bindTasksToProject(Project project, List<Long> tasksIds) {
        if (tasksIds != null) {
            tasksIds.forEach(taskId -> {
                Task task = taskService.getTaskProxy(taskId);
                project.addTask(task);
            });
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Transactional
    public void deleteProject(Long projectId) {
        logger.info("deleteProject was called: {}", projectId);
        unbindTasksFromProject(projectId);
        projectRepository.deleteProjectById(projectId);
    }

    private void unbindTasksFromProject(Long projectId) {
        Project project = getProject(projectId);
        List<Task> tasks = project.getTasks();
        for (Task task : new ArrayList<>(tasks)) {
            project.removeTask(task);
            taskService.deleteTask(task.getId());
        }
    }

    public Project getProjectProxy(Long projectId) {
        logger.info("getProjectProxy was called with params: {}", projectId);
        return projectRepository.getReferenceById(projectId);
    }

    public List<Long> getSelectedTaskIds(Long projectId) {
        logger.info("getSelectedTaskIds was called with params: {}", projectId);
        List<Long> selectedIds = new ArrayList<>();
        taskService.getTasks()
                .stream()
                .filter(t -> getProject(projectId).getTasks().contains(t))
                .forEach(t -> selectedIds.add(t.getId()));
        return selectedIds;
    }
}
