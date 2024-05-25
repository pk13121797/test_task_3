package by.pavvel.integration;

import by.pavvel.config.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        WebConfig.class,
        TestConfig.class
})
@WebAppConfiguration
@Sql(value = {"/users.sql", "/projects.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ProjectControllerIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void shouldRedirectWhenUserIsAnonymous() throws Exception {
        this.mvc.perform(
                get("/projects"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser
    public void shouldReturnProjectsView() throws Exception {
        this.mvc.perform(
                get("/projects"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("projects/projects"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("sortField", "id"))
                .andExpect(model().attribute("sortDirection", "asc"))
                .andExpect(model().attribute("sortReverse", "desc"));
    }

    @Test
    @WithMockUser
    public void shouldAddNewProject() throws Exception {
        this.mvc.perform(
                post("/projects")
                        .with(csrf())
                        .param("title", "zzz5")
                        .param("abbreviation", "ABC")
                        .param("description", "test"))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    public void shouldAddNewProjectWithTasks() throws Exception {
        this.mvc.perform(
                post("/projects")
                        .with(csrf())
                        .param("title", "r13")
                        .param("abbreviation", "ABC")
                        .param("description", "test")
                        .param("task", "1","2"))
                .andDo(print())
                .andExpect(redirectedUrl("/projects"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateProjectByAdmin() throws Exception {
        this.mvc.perform(
                put("/projects")
                                .with(csrf())
                                .param("id", "101")
                                .param("title", "zzz5")
                                .param("abbreviation", "ABC")
                                .param("description", "test")
                                .param("task", "1","2"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/projects"));
    }

    @Test
    @WithAnonymousUser
    public void shouldNotDeleteProjectWhenNotAuthorized() throws Exception {
        this.mvc.perform(
                delete("/projects/101")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldNotDeleteProjectByUser() throws Exception {
        this.mvc.perform(
                delete("/projects/101")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("errors/error"))
                .andExpect(model().attribute("errorMessage", "Access Denied"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteProjectByAdmin() throws Exception {
        this.mvc.perform(
                delete("/projects/102")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/projects"));
    }
}
