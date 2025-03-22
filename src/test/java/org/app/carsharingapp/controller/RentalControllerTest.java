package org.app.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.Month;
import org.app.carsharingapp.dto.rental.RentalRequestDto;
import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RentalControllerTest {
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

    @Sql(scripts = {
            "classpath:database/roles/add-mock-roles-to-roles-table.sql",
            "classpath:database/users/add-mock-user-to-users-table.sql",
            "classpath:database/users_roles/add-mock-users_roles.sql",
            "classpath:database/cars/add-mock-cars-to-cars-table.sql"
    })
    @Sql(scripts = {
            "classpath:database/rentals/delete-mock-rentals.sql",
            "classpath:database/users_roles/delete-from-users_roles.sql",
            "classpath:database/roles/delete-from-roles-table.sql",
            "classpath:database/users/delete-from-users-table.sql",
            "classpath:database/cars/delete-from-cars-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Add new rental")
    @Test
    void addRental() throws Exception {
        //Given
        RentalResponseDto expectedRental = getMockRentalResponseDto();
        RentalRequestDto rentalRequestDto = getMockRentalRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(rentalRequestDto);

        // When
        MvcResult result = mockMvc.perform(
                post("/rentals")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("mock@email.com").roles("CUSTOMER"))
        )
                .andExpect(status().isCreated())
                .andReturn();

        // Then

        RentalResponseDto actualResponse = objectMapper.readValue(
                result.getResponse()
                        .getContentAsString(),
                RentalResponseDto.class);
        EqualsBuilder.reflectionEquals(expectedRental, actualResponse);
    }

    private RentalRequestDto getMockRentalRequestDto() {
        return new RentalRequestDto(LocalDate.of(2025, Month.JANUARY, 1),
                LocalDate.of(2025, Month.JANUARY, 1), 1L);
    }

    private RentalResponseDto getMockRentalResponseDto() {
        return new RentalResponseDto()
                .setRentalId(1L)
                .setRentalDate(LocalDate.of(2025, Month.JANUARY, 1))
                .setReturnDate(LocalDate.of(2025, Month.JANUARY, 1))
                .setCarId(1L)
                .setUserId(1L);
    }

}
