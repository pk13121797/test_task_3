package by.pavvel.integration;

import by.pavvel.config.TestConfig;
import by.pavvel.config.WebConfig;
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
@Sql(value = {"/users.sql", "/tasks.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class TaskControllerIT {

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
                get("/tasks"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser
    public void shouldReturnProjectsView() throws Exception {
        this.mvc.perform(
                get("/tasks"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/tasks"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("sortField", "id"))
                .andExpect(model().attribute("sortDirection", "asc"))
                .andExpect(model().attribute("sortReverse", "desc"));
    }

    @Test
    @WithMockUser
    public void shouldAddNewTask() throws Exception {
        this.mvc.perform(
                post("/tasks")
                        .with(csrf())
                        .param("title", "sleep")
                        .param("projectId", "1")
                        .param("hours", "3")
                        .param("startDate", "2025-12-01")
                        .param("endDate", "2025-12-02")
                        .param("status", "not_started")
                        .param("employee", "1","2","3"))
                .andDo(print())
                .andExpect(redirectedUrl("/tasks"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateTaskByAdmin() throws Exception {
        this.mvc.perform(
                put("/tasks")
                        .with(csrf())
                        .param("id", "101")
                        .param("title", "sleep")
                        .param("projectId", "1")
                        .param("hours", "3")
                        .param("startDate", "2025-12-01")
                        .param("endDate", "2025-12-02")
                        .param("status", "not_started")
                        .param("employee", "1","2","3"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));
    }

    @Test
    @WithAnonymousUser
    public void shouldNotDeleteTaskWhenNotAuthorized() throws Exception {
        this.mvc.perform(
                delete("/tasks/101")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldNotDeleteTaskByUser() throws Exception {
        this.mvc.perform(
                delete("/tasks/102")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("errors/error"))
                .andExpect(model().attribute("errorMessage", "Access Denied"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteTaskByAdmin() throws Exception {
        this.mvc.perform(
                delete("/tasks/103")
                        .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/tasks"));
    }
}
