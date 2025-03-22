package org.app.carsharingapp.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import org.app.carsharingapp.dto.payment.PaymentResponseDto;
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
class PaymentControllerTest {
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
            "classpath:database/cars/add-mock-cars-to-cars-table.sql",
            "classpath:database/rentals/add-mock-rental.sql",
            "classpath:database/payments/add-mock-payment-to-payments-table.sql"
    })
    @Sql(scripts = {
            "classpath:database/users_roles/delete-from-users_roles.sql",
            "classpath:database/roles/delete-from-roles-table.sql",
            "classpath:database/payments/delete-from-payments-table.sql",
            "classpath:database/rentals/delete-mock-rentals.sql",
            "classpath:database/users/delete-from-users-table.sql",
            "classpath:database/cars/delete-from-cars-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @DisplayName("Get payments by user ID - Manager role required")
    void getPaymentsByUserId() throws Exception {
        // When
        MvcResult result = mockMvc.perform(
                        get("/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(user("mock@email.com").roles("MANAGER"))
                )
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String jsonResponse = result.getResponse().getContentAsString();
        List<PaymentResponseDto> actualPayments = Arrays.asList(
                objectMapper.readValue(jsonResponse, PaymentResponseDto[].class)
        );

        assertFalse(actualPayments.isEmpty());
    }

    @Test
    @DisplayName("Access denied for non-manager users")
    void getPaymentsByUserId_AccessDenied() throws Exception {
        mockMvc.perform(get("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user("mock@email.com").roles("CUSTOMER")))
                .andExpect(status().isForbidden());
    }
}
