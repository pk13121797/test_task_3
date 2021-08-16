package by.pavvel.project.service.impl;

import by.pavvel.project.entity.Project;
import by.pavvel.project.entity.Task;
import by.pavvel.project.repository.ProjectRepository;
import by.pavvel.project.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public Page<Project> showProjects(int pageNo, int pageSize, String field, String direction){

        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(field).ascending() :
                Sort.by(field).descending();

        PageRequest pageRequest = PageRequest.of(pageNo-1,pageSize,sort);
        return projectRepository.findAll(pageRequest);
    }

    public List<Project> getProjects(){
        return projectRepository.findAll();
    }

    public Project getProject(Long id){
        return projectRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Project doesn't exists"));
    }

    @Transactional
    public void addProject(Project project){
        Optional<Project> projectByTitle = projectRepository.findProjectByTitle(project.getTitle());
        if (projectByTitle.isPresent()){
            throw new IllegalStateException(String.format("Project with title %s taken",
                    projectByTitle.get().getTitle()));
        }
        projectRepository.save(project);
    }

    @Transactional
    public void updateProject(Project project) {
        projectRepository.findById(project.getId())
                .orElseThrow(() -> new IllegalStateException(String.format("Project with id %s doesn't exists",project.getId())));
        projectRepository.updateProject(project.getTitle(), project.getAbbreviation(),
                project.getDescription(), project.getId());
    }

    @Transactional
    public void deleteProject(Long id){
        Optional<Project> projectById = projectRepository.findById(id);
        if (!projectById.isPresent()){
            throw new IllegalStateException(String.format("Project with id %s doesn't exists",id));
        }
        projectRepository.deleteProjectsById(id);
    }
}


