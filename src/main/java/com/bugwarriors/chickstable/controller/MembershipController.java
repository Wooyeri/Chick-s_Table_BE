package com.bugwarriors.chickstable.controller;

import com.bugwarriors.chickstable.common.AuthUser;
import com.bugwarriors.chickstable.dto.JoinRequestDTO;
import com.bugwarriors.chickstable.dto.WithdrawRequestDTO;
import com.bugwarriors.chickstable.entity.UsersEntity;
import com.bugwarriors.chickstable.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@Slf4j
public class MembershipController {
    @Autowired
    private UsersService usersService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @PostMapping("/join")
    public ResponseEntity<?> join(
            @RequestBody JoinRequestDTO requestDto
    ) {
        if (usersService.existsByUserId(requestDto.getId())) {
            return ResponseEntity.badRequest().body("이미 존재하는 아이디입니다.");
        }
        if (usersService.existsByEmail(requestDto.getEmail())) {
            return ResponseEntity.badRequest().body("이미 존재하는 이메일 주소입니다.");
        }
        if (!requestDto.checkPassword()) {
            return ResponseEntity.badRequest().body("비밀번호를 다시 확인하세요.");
        }

        long createdUserId = usersService.join(requestDto);

        if (createdUserId == -1) {
            return ResponseEntity.internalServerError().body("회원 등록에 실패하였습니다.");
        }

        return ResponseEntity.created(URI.create("/user/" + createdUserId)).body("회원 등록에 성공하였습니다.");
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<?> register(
            @AuthUser UsersEntity users,
            @RequestBody WithdrawRequestDTO requestDto) {
        if (users == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (!requestDto.checkPassword() || !encoder.matches(requestDto.getPassword(), users.getPassword())) {
            return ResponseEntity.badRequest().body("비밀번호를 다시 확인하세요.");
        }

        usersService.deleteUsers(users);

        return ResponseEntity.ok(users.getNickname() + " 님이 회원 탈퇴하였습니다.");
    }
}
