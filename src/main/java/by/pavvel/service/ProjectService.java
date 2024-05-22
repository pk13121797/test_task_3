package by.pavvel.service;

import by.pavvel.model.Project;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProjectService {

    Page<Project> showProjects(int pageNo, String field, String direction);

    List<Project> getProjects();

    Project getProject(Long projectId);

    void addProject(Project project,List<Long> tasksIds);

    void updateProject(Project project,List<Long> tasksIds);

    void deleteProject(Long projectId);

    Project getProjectProxy(Long projectId);

    List<Long> getSelectedTaskIds(Long projectId);
}
