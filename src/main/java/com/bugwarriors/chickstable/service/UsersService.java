package com.bugwarriors.chickstable.service;

import com.bugwarriors.chickstable.dto.JoinRequestDTO;
import com.bugwarriors.chickstable.entity.UsersEntity;
import com.bugwarriors.chickstable.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public long join(JoinRequestDTO requestDto) {
        try {
            // 유저 생성
            UsersEntity users = new UsersEntity();
            users.setUserId(requestDto.getId());
            users.setPassword(encoder.encode(requestDto.getPassword()));
            users.setNickname(requestDto.getNickname());
            users.setEmail(requestDto.getEmail());
            users.setRoles("ROLE_USER");

            UsersEntity createdUsers = usersRepository.save(users);
            return createdUsers.getId();
        } catch (Exception e) {
            return -1;
        }
    }

    public void deleteUsers(UsersEntity users) {
        usersRepository.delete(users);
    };

    public boolean existsByUserId(String userId) {
        return usersRepository.existsByUserId(userId);
    };

    public boolean existsByEmail(String email) { return usersRepository.existsByEmail(email); };

    public boolean existsById(Long id) {
        return usersRepository.existsById(id);
    };
}
