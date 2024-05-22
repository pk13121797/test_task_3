package by.pavvel.integration;

import by.pavvel.config.AbstractTestcontainers;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
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
@Sql(value = {"/users.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RegistrationAndLoginIT extends AbstractTestcontainers {

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
    @WithAnonymousUser
    public void shouldRegisterWhenUserIsAnonymous() throws Exception {
        this.mvc.perform(post("/reg")
                        .with(csrf())
                        .param("name", "username")
                        .param("email", "fff@gmail.com")
                        .param("password", "password")
                )
                .andExpect(status().is3xxRedirection())
                .andDo(print());
    }

    @Test
    @WithAnonymousUser
    public void shouldNotRegisterWhenEmailIsAlreadyTaken() throws Exception {
        this.mvc.perform(post("/reg")
                        .with(csrf())
                        .param("name", "heleg14940@dovesilo.com")
                        .param("email", "heleg14940@dovesilo.com")
                        .param("password", "password")
                )
                .andExpect(redirectedUrl("/reg"))
                .andExpect(status().is3xxRedirection())
                .andDo(print());
    }

    @Test
    @WithAnonymousUser
    public void shouldLoginWhenUserIsAnonymous() throws Exception {
        this.mvc.perform(formLogin("/login")
                        .user("heleg14940@dovesilo.com")
                        .password("heleg14940@dovesilo.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated());
    }

    @Test
    @WithAnonymousUser
    public void shouldNotLoginWhenEmailIsWrong() throws Exception {
        this.mvc.perform(formLogin("/login")
                        .user("test3@gmail.com")
                        .password("test@gmail.com"))
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andDo(print());
    }

    @Test
    @WithAnonymousUser
    public void shouldNotLoginWhenPasswordIsWrong() throws Exception {
        this.mvc.perform(formLogin("/login")
                        .password("invalid"))
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    public void shouldLogoutSuccessfully() throws Exception {
        this.mvc.perform(logout("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithAnonymousUser
    public void shouldNotLoginWhenPasswordIsWrong1() throws Exception {
        this.mvc.perform(post("/forgot")
                        .param("name","pass"))
                .andExpect(unauthenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andDo(print());
    }
}
