package org.app.carsharingapp.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.app.carsharingapp.dto.user.UserLoginRequestDto;
import org.app.carsharingapp.dto.user.UserLoginResponseDto;
import org.app.carsharingapp.dto.user.UserRegistrationRequestDto;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthenticationControllerTest {
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

    @Sql(scripts = "classpath:database/roles/add-mock-roles-to-roles-table.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/users_roles/delete-from-users_roles.sql",
            "classpath:database/roles/delete-from-roles-table.sql",
            "classpath:database/users/delete-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Registration valid data")
    void register_ValidParams_Ok() throws Exception {
        UserRegistrationRequestDto registrationRequestDto = getRegistrationRequestDtoMock();
        UserResponseDto expectedResponseDto = getUserResponseDto();
        String jsonRequest = objectMapper.writeValueAsString(registrationRequestDto);

        MvcResult result = mockMvc.perform(
                post("/auth/register")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        UserResponseDto actualResponseDto = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserResponseDto.class
        );
        assertEquals(expectedResponseDto, actualResponseDto);
    }

    @Sql(scripts = {
            "classpath:database/roles/add-mock-roles-to-roles-table.sql",
            "classpath:database/users/add-mock-user-to-users-table.sql",
            "classpath:database/users_roles/add-mock-users_roles.sql"
    })
    @Sql(scripts = {"classpath:database/users_roles/delete-from-users_roles.sql",
            "classpath:database/roles/delete-from-roles-table.sql",
            "classpath:database/users/delete-from-users-table.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Login success")
    void login_ValidData_Ok() throws Exception {
        UserLoginRequestDto userLoginRequestDto = getUserLoginRequestDto();
        String jsonRequest = objectMapper.writeValueAsString(userLoginRequestDto);

        MvcResult result = mockMvc.perform(
                post("/auth/login")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andReturn();

        UserLoginResponseDto actualResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), UserLoginResponseDto.class
        );

        assertNotNull(actualResponse);
    }

    private UserResponseDto getUserResponseDto() {
        return new UserResponseDto("mock@email.com", "Mock", "Name", Role.RoleName.CUSTOMER);
    }

    private UserLoginRequestDto getUserLoginRequestDto() {
        UserLoginRequestDto userLoginRequestDto = new UserLoginRequestDto();
        userLoginRequestDto.setEmail("mock@email.com");
        userLoginRequestDto.setPassword("P@ssword123");
        return userLoginRequestDto;
    }

    private UserRegistrationRequestDto getRegistrationRequestDtoMock() {
        UserRegistrationRequestDto registrationRequestDto = new UserRegistrationRequestDto();
        registrationRequestDto.setEmail("mock@email.com");
        registrationRequestDto.setFirstName("Mock");
        registrationRequestDto.setLastName("Name");
        registrationRequestDto.setPassword("mockP@ss123");
        registrationRequestDto.setRepeatPassword("mockP@ss123");
        return registrationRequestDto;
    }
}
