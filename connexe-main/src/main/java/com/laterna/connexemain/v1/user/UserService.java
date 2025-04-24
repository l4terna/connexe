package com.laterna.connexemain.v1.user;

import com.laterna.connexemain.v1._shared.exception.EntityAlreadyExistsException;
import com.laterna.connexemain.v1.hub.member.HubMemberProviderService;
import com.laterna.connexemain.v1.hub.member.dto.HubMemberDTO;
import com.laterna.connexemain.v1.user.avatar.UserAvatarService;
import com.laterna.connexemain.v1.user.dto.*;
import com.laterna.connexemain.v1.user.event.UserCreatedEvent;
import com.laterna.proto.v1.UserDataProto;
import com.laterna.proto.v1.UserEvent;
import com.laterna.proto.v1.UserEventProduce;
import com.laterna.proto.v1.UserEventType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final HubMemberProviderService hubMemberProviderService;
    private final UserAvatarService userAvatarService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public UserDTO findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional
    public void createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.existsByEmail(userCreateDTO.email())) {
            throw new EntityAlreadyExistsException("User already exists");
        }

        User user = User.builder()
                .id(userCreateDTO.userId())
                .login(userCreateDTO.login())
                .email(userCreateDTO.email())
                .build();

        User savedUser = userRepository.save(user);

        applicationEventPublisher.publishEvent(new UserCreatedEvent(savedUser));
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Transactional(readOnly = true)
    public List<User> findAllUsersByIds(Iterable<Long> users) {
        return userRepository.findAllById(users);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findAllByIds(Iterable<Long> users) {
        return userRepository.findAllById(users).stream().map(userMapper::toDTO).toList();
    }

    public UserDTO getMe(User user) {
        return userMapper.toDTO(user);
    }

    @Transactional(readOnly = true)
    public UserProfileDTO getProfile(
            Long id,
            GetUserFilter filter
    ) {
        HubMemberDTO hubMemberDTO = null;

        UserDTO userDTO = userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (filter.getHubId() != null) {
            hubMemberDTO = hubMemberProviderService.findByHubIdAndUserId(filter.getHubId(), userDTO.id());
        }

        return UserProfileDTO.builder()
                .user(userDTO)
                .hubMember(hubMemberDTO)
                .build();
    }

    @Transactional(readOnly = true)
    public Set<Long> findUserIdsByEmails(Set<String> emails) {
        return userRepository.findUserIdsByEmails(emails);
    }


    @Transactional
    public UserDTO updateMe(UserUpdateDTO updateDTO, User user) {
        if (updateDTO.avatar() != null && !updateDTO.avatar().isEmpty()) {
            userAvatarService.save(updateDTO.avatar(), user.getId());
        }

        if (updateDTO.login() != null &&
                !updateDTO.login().isEmpty()) {
            if (userRepository.existsByLogin(updateDTO.login())) {
                throw new EntityAlreadyExistsException("User already exists");
            }
            user.setLogin(updateDTO.login());
        }

        UserDTO savedUserDTO = userMapper.toDTO(userRepository.save(user));

        if (updateDTO.login() != null && updateDTO.login().equals(savedUserDTO.login())) {
            UserEvent event = UserEvent.newBuilder()
                    .setEventType(UserEventType.USER_UPDATED)
                    .setUpdatedUser(
                            UserDataProto.newBuilder()
                                    .setUserId(user.getId())
                                    .setLogin(savedUserDTO.login())
                                    .build()
                    )
                    .build();

            applicationEventPublisher.publishEvent(UserEventProduce.newBuilder().setUserEvent(event).build());
        }

        return savedUserDTO;
    }

    @Transactional
    public void deleteMe(User user) {
        user.setDeletedAt(Instant.now());
        userRepository.save(user);

        UserEvent event = UserEvent.newBuilder()
                .setEventType(UserEventType.USER_DELETED)
                .setDeletedUserId(user.getId())
                .build();

        applicationEventPublisher.publishEvent(UserEventProduce.newBuilder().setUserEvent(event).build());
    }
}
