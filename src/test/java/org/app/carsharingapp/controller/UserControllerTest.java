package org.app.carsharingapp.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.carsharingapp.dto.rental.RentalResponseDto;
import org.app.carsharingapp.dto.user.UserResponseDto;
import org.app.carsharingapp.entity.Role;
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
class UserControllerTest {
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
            "classpath:database/users_roles/add-mock-users_roles.sql"
    })
    @Sql(scripts = {
            "classpath:database/users_roles/delete-from-users_roles.sql",
            "classpath:database/roles/delete-from-roles-table.sql",
            "classpath:database/users/delete-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Get profile info")
    void getProfileInfo() throws Exception {
        //Given
        UserResponseDto expectedResponseDto = getMockUser();

        // When
        MvcResult result = mockMvc.perform(
                get("/users/me")
                        .with(user("mock@email.com").roles("CUSTOMER"))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        RentalResponseDto actualResponseDto = objectMapper.readValue(
                result.getResponse()
                        .getContentAsString(),
                RentalResponseDto.class
        );

        EqualsBuilder.reflectionEquals(expectedResponseDto, actualResponseDto);
    }

    private UserResponseDto getMockUser() {
        return new UserResponseDto("mock@email.com",
                "Name",
                "Surname",
                Role.RoleName.CUSTOMER);
    }
}
