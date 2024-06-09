package org.app.carsharingapp.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.app.carsharingapp.dto.user.UserRegistrationRequestDto;
import org.app.carsharingapp.dto.user.UserResponseDto;
import org.app.carsharingapp.dto.user.UserUpdateRoleRequestDto;
import org.app.carsharingapp.entity.Role;
import org.app.carsharingapp.entity.User;
import org.app.carsharingapp.exception.RegistrationException;
import org.app.carsharingapp.mapper.UserMapper;
import org.app.carsharingapp.repository.RoleRepository;
import org.app.carsharingapp.repository.UserRepository;
import org.app.carsharingapp.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException(
                    "Can't register user. User with the same email was registered"
            );
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role defaultRole = roleRepository.getRoleByRoleName(Role.RoleName.CUSTOMER)
                .orElseThrow(() -> new EntityNotFoundException("Can't find the role: CUSTOMER"));
        user.setRoles(Set.of(defaultRole));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserResponseDto updateUserRole(Long userId, UserUpdateRoleRequestDto requestDto) {
        User user = getUserById(userId);
        user.setRoles(Set.of(roleRepository.getRoleByRoleName(requestDto.getRole())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find the role: " + requestDto.getRole().name()
                ))));
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserResponseDto getProfileInfo(Long userId) {
        User user = getUserById(userId);
        return userMapper.toDto(user);
    }

    @Override
    public UserResponseDto updateProfileInfo(Long userId, UserRegistrationRequestDto requestDto) {
        User user = getUserById(userId);
        userMapper.updateUserFromDto(user, requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(user);
        return userMapper.toDto(user);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user with id" + userId));
    }
}
