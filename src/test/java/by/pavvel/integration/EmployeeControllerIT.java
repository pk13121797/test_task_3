package by.pavvel.integration;

import by.pavvel.config.AbstractTestcontainers;
import by.pavvel.config.TestConfig;
import by.pavvel.config.WebConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
@Sql(value = {"/users.sql", "/employees.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class EmployeeControllerIT extends AbstractTestcontainers {

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
        this.mvc.perform(get("/employees"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser
    public void shouldReturnProjectsView() throws Exception {
        this.mvc.perform(get("/employees"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("employees/employees"))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("sortField", "id"))
                .andExpect(model().attribute("sortDirection", "asc"))
                .andExpect(model().attribute("sortReverse", "desc"));
    }

    @Test
    @WithMockUser
    public void shouldAddNewEmployeeWithDefaultImage() throws Exception {

        this.mvc.perform(multipart("/employees")
                        .file("files", "".getBytes())
                        .with(csrf())
                        .param("name", "zzz5")
                        .param("surname", "ABC")
                        .param("middleName", "test")
                        .param("post", "test"))
                .andDo(print())
                .andExpect(redirectedUrl("/employees"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    public void shouldAddNewEmployeeWithCustomImage() throws Exception {

        MockMultipartFile multipartFile = new MockMultipartFile(
                "hello",
                "hello.jpg",
                String.valueOf(MediaType.IMAGE_JPEG),
                "".getBytes()
        );

        this.mvc.perform(multipart("/employees")
                        .file("files", multipartFile.getBytes())
                        .with(csrf())
                        .param("name", "zzz5")
                        .param("surname", "ABC")
                        .param("middleName", "test")
                        .param("post", "test"))
                .andDo(print())
                .andExpect(redirectedUrl("/employees"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldNotUpdateEmployeeByUser() throws Exception {
        this.mvc.perform(
                        put("/employees")
                                .with(csrf())
                                .param("id", "101")
                                .param("name", "zzz5")
                                .param("surname", "ABC")
                                .param("middleName", "test")
                                .param("post", "test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("errors/error"))
                .andExpect(model().attribute("errorMessage", "Access Denied"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldUpdateEmployeeByAdmin() throws Exception {
        this.mvc.perform(
                        put("/employees")
                                .with(csrf())
                                .param("id", "102")
                                .param("name", "zzz5")
                                .param("surname", "ABC")
                                .param("middleName", "test")
                                .param("post", "test"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employees"));
    }

    @Test
    @WithAnonymousUser
    public void shouldNotDeleteEmployeeWhenNotAuthorized() throws Exception {
        this.mvc.perform(
                        delete("/employees/101")
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void shouldNotDeleteEmployeeByUser() throws Exception {
        this.mvc.perform(
                        delete("/employees/102")
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("errors/error"))
                .andExpect(model().attribute("errorMessage", "Access Denied"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void shouldDeleteEmployeeByAdmin() throws Exception {
        this.mvc.perform(
                        delete("/employees/103")
                                .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/employees"));
    }
}
