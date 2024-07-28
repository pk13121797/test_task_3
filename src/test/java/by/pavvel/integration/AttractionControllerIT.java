package by.pavvel.integration;

import by.pavvel.config.TestConfig;
import by.pavvel.config.WebConfig;
import by.pavvel.model.Attraction;
import by.pavvel.model.AttractionType;
import by.pavvel.repository.AttractionRepository;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
        WebConfig.class,
        TestConfig.class
})
@WebAppConfiguration
@Sql(value = {"/data.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class AttractionControllerIT {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AttractionRepository attractionRepository;

    private MockMvc mvc;

    @BeforeEach
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }

    @Test
    public void shouldGetSortedAttractions() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilder = get("/attractions?sortDirection=desc&type=museum");

        //when
        this.mvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                  {
                                    "id": 2,
                                    "title": "The estate of the Belarusian Santa Claus",
                                    "creationDate": [
                                      2003,
                                      12,
                                      16
                                    ],
                                    "description": "some description",
                                    "attractionType": "MUSEUM"
                                  },
                                  {
                                    "id": 4,
                                    "title": "National Historical Museum",
                                    "creationDate": [
                                      1957,
                                      1,
                                      1
                                    ],
                                    "description": "some description",
                                    "attractionType": "MUSEUM"
                                  }
                                ]
                        """)
                );
    }

    @Test
    public void shouldGetAttractionsByLocality() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilder = get("http://localhost:8080/attractions/locality/minsk");

        //when
        this.mvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                  {
                                    "id": 4,
                                    "title": "National Historical Museum",
                                    "creationDate": [
                                      1957,
                                      1,
                                      1
                                    ],
                                    "description": "some description",
                                    "attractionType": "MUSEUM",
                                    "population": 2000000.0,
                                    "metroAvailability": true
                                  }
                                ]
                        """)
                );
    }

    @Test
    public void shouldGetAttractionsRecommendation() throws Exception {
        //given
        MockHttpServletRequestBuilder requestBuilder = get("http://localhost:8080/attractions/recommendation?latitude=66.766237&longitude=33.632615");

        //when
        this.mvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                  "id": "202ec8ce-b284-438f-a6ec-a7ef99ba3b01",
                                  "recommendation": "The weather is great. Enjoy",
                                  "text": "Sunny",
                                  "address": "Etobicoke",
                                  "localDateTime": "2024-07-29"
                                }
                        """)
                );
    }

    @Test
    public void shouldAddNewAttractionWithLocalityAndServices() throws Exception {
        //given
        String title = "Minsk Planetarium";
        LocalDate creationDate = LocalDate.parse("1965-07-29");
        String description = "description";
        AttractionType attractionType = AttractionType.NATURE_RESERVE;

        RequestBuilder requestBuilder = post("http://localhost:8080/attractions?locality=3&services=1,3")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title" : "%s",
                          "creationDate" : "%s",
                          "description" : "%s",
                          "attractionType" : "%s"
                        }
                        """.formatted(title, creationDate, description, attractionType));

        // when
        this.mvc.perform(requestBuilder)
                //then
                .andExpectAll(status().isCreated());

        Optional<Attraction> attractionById = attractionRepository.findById(-48L);
        assertThat(attractionById)
                .isPresent()
                .hasValueSatisfying(a -> {
                    assertThat(a.getTitle()).isEqualTo(title);
                    assertThat(a.getCreationDate()).isEqualTo(creationDate);
                    assertThat(a.getDescription()).isEqualTo(description);
                    assertThat(a.getAttractionType()).isEqualTo(attractionType);
                });
    }

    @Test
    public void shouldUpdateAttractionInfo() throws Exception {
        //given
        Long attractionId = 3L;
        String abbreviation = "abc";
        RequestBuilder requestBuilder =
                put("http://localhost:8080/attractions/{attractionId}", attractionId)
                        .param("abbreviation", abbreviation)
                        .contentType(MediaType.APPLICATION_JSON);

        // when
        this.mvc.perform(requestBuilder)
                //then
                .andExpectAll(status().isOk());

        Optional<Attraction> attractionById = attractionRepository.findAttractionWithServices(attractionId);
        assertThat(attractionById)
                .isPresent()
                .hasValueSatisfying(a -> a.getServices().forEach(s -> assertThat(s.getAbbreviation()).isEqualTo(abbreviation)));
    }

    @Test
    public void shouldDeleteAttraction() throws Exception {
        //given
        Long attractionId = 1L;
        RequestBuilder requestBuilder = delete("http://localhost:8080/attractions/{attractionId}", attractionId)
                .contentType(MediaType.APPLICATION_JSON);

        // when
        this.mvc.perform(requestBuilder)
                //then
                .andExpectAll(status().isNoContent());

        Optional<Attraction> attractionById = attractionRepository.findById(attractionId);
        assertThat(attractionById).isNotPresent();
    }
}
