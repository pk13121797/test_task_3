package by.pavvel.service.impl;

import by.pavvel.exception.ProjectNotFoundException;
import by.pavvel.model.Project;
import by.pavvel.model.Task;
import by.pavvel.repository.ProjectRepository;
import by.pavvel.service.ProjectService;
import by.pavvel.service.TaskService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskService taskService;

    @Captor
    private ArgumentCaptor<Project> projectArgumentCaptor;

    private AutoCloseable autoCloseable;

    private ProjectService underTest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new ProjectServiceImpl(projectRepository,taskService);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void shouldShowProjects() {
        // given
        Project project1 = new Project("title1", "AA", "desc1");
        Project project2 = new Project("title2", "BBB", "desc2");

        List<Project> projectList = List.of(project1, project2);
        Page<Project> page = mock(Page.class);
        when(page.getContent()).thenReturn(projectList);
        when(projectRepository.findAll(any(Pageable.class))).thenReturn(page);

        // when
        Page<Project> projects = underTest.showProjects(1, "title", "desc");

        // then
        assertThat(projects.getContent())
                .isEqualTo(projectList);
        assertThat(projects).isInstanceOf(Page.class);
    }

    @Test
    void shouldGetProjects() {
        // given
        Project project1 = new Project("title1", "AA", "desc1");
        Project project2 = new Project("title2", "AB", "desc2");

        List<Project> projectList = List.of(project1, project2);
        when(projectRepository.findAll()).thenReturn(projectList);

        // when
        List<Project> projects = underTest.getProjects();

        // then
        assertThat(projects)
                .isNotNull()
                .isEqualTo(projectList);
    }

    @Test
    void shouldGetProject() {
        // given
        Project project1 = new Project("project1","AAA","description1");
        Project project2 = new Project("project2","BBB","description2");
        when(projectRepository.findProjectById(project1.getId())).thenReturn(Optional.of(project1));

        // when
        Project project = underTest.getProject(project1.getId());

        // then
        assertThat(project)
                .isEqualTo(project1)
                .isNotEqualTo(project2);
    }

    @Test
    void shouldThrowExceptionWhenProjectNotExists() {
        // given
        Long projectId = 1L;
        when(projectRepository.findProjectById(projectId)).thenReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> underTest.getProject(projectId))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining(String.format(
                        "Project with id %s doesn't exists", projectId)
                );
    }

    @Test
    void shouldAddProject() {
        // given
        Long task1Id = 1L;
        Long task2Id = 2L;
        List<Long> taskIds = List.of(task1Id, task2Id);

        Project project = new Project("java", "AA1", "description1");
        Task task1 = new Task("task1", null, null, null,null);
        Task task2 = new Task("task2", null, null, null,null);

        when(taskService.getTaskProxy(task1Id)).thenReturn(task1);
        when(taskService.getTaskProxy(task2Id)).thenReturn(task2);

        // when
        underTest.addProject(project, taskIds);

        // then
        then(projectRepository).should().save(projectArgumentCaptor.capture());
        Project projectArgumentCaptorValue = projectArgumentCaptor.getValue();

        assertThat(projectArgumentCaptorValue).isEqualTo(project);
        assertThat(projectArgumentCaptorValue.getTasks().size()).isEqualTo(2);
    }

    @Test
    void shouldUpdateProject() {
        // given
        Project project = new Project("java", "AA1", "description1");
        Task task1 = new Task("task1", null, null, null,null);
        task1.setId(1L);
        Task task2 = new Task("task2", null, null, null,null);
        task2.setId(2L);

        List<Long> taskIds = List.of(task1.getId(), task2.getId());

        project.addTask(task1);
        project.addTask(task2);

        ArgumentCaptor<Long> id = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<String> title = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> abbreviation = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> description = ArgumentCaptor.forClass(String.class);

        when(taskService.getTaskProxy(task1.getId())).thenReturn(task1);
        when(taskService.getTaskProxy(task2.getId())).thenReturn(task1);
        doNothing().when(projectRepository).updateProject(
                title.capture(),
                abbreviation.capture(),
                description.capture(),
                id.capture()
        );

        // when
        Project projectToUpdate = new Project("project2","BBB","description");
        underTest.updateProject(projectToUpdate,taskIds);

        // then
        verify(projectRepository, times(1)).updateProject(
                projectToUpdate.getTitle(),
                projectToUpdate.getAbbreviation(),
                projectToUpdate.getDescription(),
                projectToUpdate.getId()
        );

        assertThat(projectToUpdate.getId()).isEqualTo(id.getValue());
        assertThat(projectToUpdate.getTitle()).isEqualTo(title.getValue());
        assertThat(projectToUpdate.getAbbreviation()).isEqualTo(abbreviation.getValue());
        assertThat(projectToUpdate.getDescription()).isEqualTo(description.getValue());
        assertThat(project.getTasks().size()).isEqualTo(2);
    }

    @Test
    void shouldDeleteProject() {
        // given
        Project project = new Project("project1","AAA","description1");

        Task task1 = new Task("task1", null, null, null,null);
        Task task2 = new Task("task2", null, null, null,null);

        project.addTask(task1);
        project.addTask(task2);

        when(projectRepository.findProjectById(project.getId())).thenReturn(Optional.of(project));
        doNothing().when(taskService).deleteTask(task1.getId());
        doNothing().when(taskService).deleteTask(task2.getId());
        doNothing().when(projectRepository).deleteProjectById(project.getId());

        assertThat(project.getTasks().size()).isEqualTo(2);

        // when
        underTest.deleteProject(project.getId());

        // then
        verify(projectRepository,times(1)).deleteProjectById(project.getId());
        assertThat(project.getTasks().size()).isEqualTo(0);
    }

    @Test
    void shouldGetProjectProxy() {
        // given
        Long projectId = 2L;
        Project projectProxy = new Project(null, null, null);
        when(projectRepository.getReferenceById(projectId)).thenReturn(projectProxy);

        // when
        Project proxy = underTest.getProjectProxy(projectId);

        // then
        assertThat(proxy.getId()).isEqualTo(projectProxy.getId());
        assertThat(proxy.getTitle()).isNull();
    }

    @Test
    void shouldGetSelectedTaskIdsByProjectId() {
        // given
        Project project = new Project("project1","AAA","description");

        Task task1 = new Task("task1", null, null, null,null);
        Task task2 = new Task("task2", null, null, null,null);

        project.addTask(task1);
        project.addTask(task2);

        when(projectRepository.findProjectById(project.getId())).thenReturn(Optional.of(project));
        when(taskService.getTasks()).thenReturn(List.of(task1, task2));

        // when
        List<Long> selectedTaskIds = underTest.getSelectedTaskIds(project.getId());

        // then
        assertThat(selectedTaskIds.size()).isEqualTo(2);
    }
}