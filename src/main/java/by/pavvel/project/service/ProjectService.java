package by.pavvel.project.service;

import by.pavvel.project.entity.Project;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProjectService {

    Page<Project> showProjects(int pageNo, int pageSize, String field, String direction);

    List<Project> getProjects();

    Project getProject(Long id);

    void addProject(Project project);

    void updateProject(Project project);

    void deleteProject(Long id);
}
