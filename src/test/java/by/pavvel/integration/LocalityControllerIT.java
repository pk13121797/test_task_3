package by.pavvel.integration;

import by.pavvel.config.TestConfig;
import by.pavvel.config.WebConfig;
import by.pavvel.model.Locality;
import by.pavvel.repository.LocalityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        WebConfig.class,
        TestConfig.class
})
@WebAppConfiguration
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class LocalityControllerIT {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private LocalityRepository localityRepository;

    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @Test
    public void shouldAddNewLocality() throws Exception {
        //given
        String title = "Brest";
        Double population = 345000.0;
        Boolean metroAvailability = false;

        RequestBuilder requestBuilder =
                post("http://localhost:8080/localities")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title" : "%s",
                          "population" : "%s",
                          "metroAvailability" : "%s"
                        }
                        """.formatted(title, population, metroAvailability));

        // when
        this.mvc.perform(requestBuilder)
                //then
                .andExpectAll(status().isCreated());

        Optional<Locality> localityById = localityRepository.findById(-48L);
        assertThat(localityById)
                .isPresent()
                .hasValueSatisfying(l -> {
                    assertThat(l.getTitle()).isEqualTo(title);
                    assertThat(l.getPopulation()).isEqualTo(population);
                    assertThat(l.getMetroAvailability()).isEqualTo(metroAvailability);
                });
    }

    @Test
    public void shouldUpdateLocality() throws Exception {
        //given
        Long localityId = 2L;
        Double population = 345000.0;
        Boolean metroAvailability = false;
        RequestBuilder requestBuilder =
                put("http://localhost:8080/localities/{localityId}", localityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "population" : "%s",
                          "metroAvailability" : "%s"
                        }
                        """.formatted(population, metroAvailability));

        // when
        this.mvc.perform(requestBuilder)
                //then
                .andExpectAll(status().isOk());

        Optional<Locality> localityById = localityRepository.findById(localityId);
        assertThat(localityById)
                .isPresent()
                .hasValueSatisfying(l -> {
                    assertThat(l.getPopulation()).isEqualTo(population);
                    assertThat(l.getMetroAvailability()).isEqualTo(metroAvailability);
                });
    }
}
