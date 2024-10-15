package org.app.carsharingapp.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.app.carsharingapp.dto.car.CarDto;
import org.app.carsharingapp.entity.Car;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@Transactional
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
    @DisplayName("Create new car")
    void createCar() throws Exception {
        //Given
        CarDto expectedCarDto = CarDto.builder()
                .model("Model S")
                .brand("Tesla")
                .type(Car.Type.SEDAN)
                .inventory(5)
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

    @Test
    @Sql(scripts = "classpath:database/cars/add-mock-cars-to-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-from-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get car by id")
    void getCar_ValidId_ShouldReturnValidCarDto() throws Exception {
        //Given
        Long id = 1L;
        CarDto expectedCarDto = CarDto.builder()
                .model("Model S")
                .brand("Tesla")
                .type(Car.Type.SEDAN)
                .inventory(5)
                .dailyFee(BigDecimal.valueOf(99.99))
                .build();

        // When
        MvcResult result = mockMvc.perform(
                get("/cars/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        CarDto actualCarDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                CarDto.class);
        assertNotNull(actualCarDto);
        assertEquals(expectedCarDto, actualCarDto);
    }

    @Test
    @Sql(scripts = "classpath:database/cars/add-mock-cars-to-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-from-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get all cars")
    void getAllCars_ValidParam_ShouldReturnAllCars() throws Exception {
        // Given
        List<CarDto> expectedCarsDtoList = getMockListCarsDto();

        // When
        MvcResult result = mockMvc.perform(
                        get("/cars").contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        // Then
        CarDto[] actualCarsDtoList = objectMapper.readValue(
                result.getResponse().getContentAsByteArray(), CarDto[].class);
        assertEquals(expectedCarsDtoList, Arrays.stream(actualCarsDtoList).toList());
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = "classpath:database/cars/add-mock-cars-to-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-from-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Update car")
    void updateCar_ValidParam_ShouldReturnUpdatedCarDto() throws Exception {
        // Given
        CarDto expectedCarDto = getMockCarDto();
        String jsonRequest = objectMapper.writeValueAsString(expectedCarDto);

        // When
        MvcResult result = mockMvc.perform(
                        put("/cars/1")
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn();

        // Then
        CarDto actualCarDto = objectMapper.readValue(result.getResponse().getContentAsString(),
                CarDto.class);
        assertEquals(expectedCarDto, actualCarDto);
    }

    @WithMockUser(username = "manager", roles = {"MANAGER"})
    @Sql(scripts = "classpath:database/cars/add-mock-cars-to-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/cars/delete-from-cars-table.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Delete car")
    void deleteCar() throws Exception {
        // When and Then
        mockMvc.perform(delete("/cars/1"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    private List<CarDto> getMockListCarsDto() {
        CarDto carDto1 = new CarDto("Model S", "Tesla", Car.Type.SEDAN, 5,
                BigDecimal.valueOf(99.99));
        CarDto carDto2 = new CarDto("X5", "BMW", Car.Type.SUV, 3,
                BigDecimal.valueOf(120.00).setScale(2, RoundingMode.UNNECESSARY));
        CarDto carDto3 = new CarDto("Golf", "Volkswagen", Car.Type.HATCHBACK, 5,
                BigDecimal.valueOf(70.50).setScale(2, RoundingMode.UNNECESSARY));
        CarDto carDto4 = new CarDto("Passat", "Volkswagen", Car.Type.UNIVERSAL, 7,
                BigDecimal.valueOf(85.00).setScale(2, RoundingMode.UNNECESSARY));

        List<CarDto> mockListCarsDto = new ArrayList<>();
        mockListCarsDto.add(carDto1);
        mockListCarsDto.add(carDto2);
        mockListCarsDto.add(carDto3);
        mockListCarsDto.add(carDto4);

        return mockListCarsDto;
    }
    
    private CarDto getMockCarDto() {
        return new CarDto("Model S", "Tesla", Car.Type.SEDAN, 3,
                BigDecimal.valueOf(120.50).setScale(2, RoundingMode.UNNECESSARY));
    }
}
