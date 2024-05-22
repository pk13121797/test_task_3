package by.pavvel.utils;

import by.pavvel.model.Project;
import by.pavvel.repository.ProjectRepository;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class ProjectValidator implements Validator {
    private final ProjectRepository projectRepository;

    public ProjectValidator(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Project.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Project project = (Project) target;
        if (projectRepository.findProjectByTitle(project.getTitle()).isPresent()) {
            errors.rejectValue(
                    "title",
                    "project.title",
                    new Object[]{project.getTitle()},
                    "This title is already taken"
            );
        }
    }
}
