package org.app.carsharingapp.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.app.carsharingapp.dto.user.UserRegistrationRequestDto;
import org.app.carsharingapp.dto.user.UserResponseDto;
import org.app.carsharingapp.dto.user.UserUpdateRoleRequestDto;
import org.app.carsharingapp.entity.Role;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.exception.RegistrationException;
import org.app.carsharingapp.mapper.UserMapper;
import org.app.carsharingapp.repository.RoleRepository;
import org.app.carsharingapp.repository.UserRepository;
import org.app.carsharingapp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;
    private UserRegistrationRequestDto registrationRequestDto;
    private UserUpdateRoleRequestDto updateRoleRequestDto;
    private UserResponseDto userResponseDto;
    private User user;
    private Role roleCustomer;
    private Role roleManager;

    @BeforeEach
    void setUp() {
        registrationRequestDto = new UserRegistrationRequestDto()
                .setEmail("email@email.com")
                .setFirstName("User")
                .setLastName("Test")
                .setPassword("password")
                .setRepeatPassword("password");

        updateRoleRequestDto = new UserUpdateRoleRequestDto()
                .setRole(Role.RoleName.MANAGER);

        userResponseDto = new UserResponseDto("email@email.com",
                "User",
                "Test",
                Role.RoleName.CUSTOMER);

        user = new User()
                .setId(1L)
                .setEmail("email@email.com")
                .setFirstName("User")
                .setLastName("Test");

        roleCustomer = new Role()
                .setId(1L)
                .setRoleName(Role.RoleName.CUSTOMER);

        roleManager = new Role()
                .setId(2L)
                .setRoleName(Role.RoleName.MANAGER);
    }

    @DisplayName("Email was already registered")
    @Test
    void register_EmailWasRegistered_ShouldThrowException() {
        when(userRepository.existsByEmail(registrationRequestDto.getEmail())).thenReturn(true);

        assertThrows(RegistrationException.class,
                () -> userService.register(registrationRequestDto));
    }

    @DisplayName("Valid registration")
    @Test
    void register_ValidRequestDto_ShouldAddNewUser() throws RegistrationException {
        when(userRepository.existsByEmail(registrationRequestDto.getEmail())).thenReturn(false);
        when(userMapper.toModel(registrationRequestDto)).thenReturn(user);
        when(passwordEncoder.encode(registrationRequestDto.getPassword()))
                .thenReturn("11password11");
        when(roleRepository.getRoleByRoleName(Role.RoleName.CUSTOMER))
                .thenReturn(Optional.of(roleCustomer));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        assertEquals(userResponseDto, userService.register(registrationRequestDto));
        assertEquals("11password11", user.getPassword());
    }

    @DisplayName("Valid role update")
    @Test
    void updateUserRole_ValidIdAndRequest_ShouldUpdateUserRole() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.getRoleByRoleName(updateRoleRequestDto.getRole()))
                .thenReturn(Optional.of(roleManager));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        assertEquals(userResponseDto, userService.updateUserRole(1L, updateRoleRequestDto));
    }

    @DisplayName("Exception invalid Role")
    @Test
    void updateUserRole_InvalidRole_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.getRoleByRoleName(updateRoleRequestDto.getRole()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUserRole(1L, updateRoleRequestDto));
        assertEquals("Can't find the role: MANAGER", exception.getMessage());
    }

    @DisplayName("Incorrect profile info")
    @Test
    void getProfileInfo_InvalidId_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getProfileInfo(1L));
    }

    @DisplayName("Correct profile info")
    @Test
    void getProfileInfo_CorrectId_ShouldReturnCorrectUserResponseDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        assertEquals(userResponseDto, userService.getProfileInfo(1L));
    }
}
