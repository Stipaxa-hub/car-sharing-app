package org.app.carsharingapp.controller;

import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.app.carsharingapp.dto.car.CarDto;
import org.app.carsharingapp.entity.Car;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CarControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = "classpath:database/cars/delete-from-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void creteCar() throws Exception {
        //Given
        CarDto expectedCarDto = CarDto.builder()
                .model("Model Y")
                .brand("Tesla")
                .type(Car.Type.SEDAN)
                .inventory(2)
                .dailyFee(BigDecimal.valueOf(99.9))
                .build();
        String jsonRequest = objectMapper.writeValueAsString(expectedCarDto);

        //When
        MvcResult result = mockMvc.perform(
                post("/cars")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isCreated())
                .andReturn();

        //Then
        CarDto actualCarDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), CarDto.class
        );
        assertNotNull(actualCarDto);
        EqualsBuilder.reflectionEquals(expectedCarDto, actualCarDto, "id");
        EqualsBuilder.reflectionEquals(expectedCarDto, actualCarDto, "model");
    }
}
