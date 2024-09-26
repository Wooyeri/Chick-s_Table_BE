package com.bugwarriors.chickstable.controller;

import com.bugwarriors.chickstable.common.AuthUser;
import com.bugwarriors.chickstable.dto.ChangePasswordRequestDTO;
import com.bugwarriors.chickstable.dto.UsersInfoRequestDTO;
import com.bugwarriors.chickstable.dto.UsersInfoResponseDTO;
import com.bugwarriors.chickstable.entity.UsersEntity;
import com.bugwarriors.chickstable.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/user")
@Slf4j
public class UsersInfoController {
    @Autowired
    private UsersService usersService;

    @Autowired
    private BCryptPasswordEncoder encoder;

    // 유저 상세 정보 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserInfo(
            @AuthUser UsersEntity users,
            @PathVariable(value = "id") Long userId
    ) {
        if (!usersService.existsById(userId)) {
            return ResponseEntity.badRequest().body("존재하지 않는 회원입니다.");
        }

        if (!users.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("본인의 마이페이지만 확인할 수 있습니다.");
        }

        UsersInfoResponseDTO responseDTO = usersService.getUsersInfo(users);
        return ResponseEntity.ok(responseDTO);
    }

    // 유저 상세 정보 수정
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUserInfo(
            @AuthUser UsersEntity users,
            @PathVariable(value = "id") Long userId,
            @RequestPart(value = "data") UsersInfoRequestDTO requestDto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        if (!usersService.existsById(userId)) {
            return ResponseEntity.badRequest().body("존재하지 않는 회원입니다.");
        }

        if (!users.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("본인의 마이페이지만 수정할 수 있습니다.");
        }

        int updatedUserId = usersService.updateUserInfo(users, requestDto, file);

        if (updatedUserId == -1) {
            return ResponseEntity.internalServerError().body("회원 정보 수정에 실패하였습니다.");
        }

        return ResponseEntity.ok("회원 정보 수정에 성공하였습니다.");
    }

    // 유저 비밀번호 변경
    @PatchMapping("/password/{id}")
    public ResponseEntity<?> changePassword(
            @AuthUser UsersEntity users,
            @PathVariable(value = "id") Long userId,
            @RequestBody ChangePasswordRequestDTO requestDto
    ) {
        if (!usersService.existsById(userId)) {
            return ResponseEntity.badRequest().body("존재하지 않는 회원입니다.");
        }
        if (!users.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("본인의 비밀번호만 변경할 수 있습니다.");
        }
        if (!requestDto.getEmail().equals(users.getEmail()) || !encoder.matches(requestDto.getPassword(), users.getPassword())) {
            return ResponseEntity.badRequest().body("틀린 정보입니다. 다시 입력해주세요.");
        }

        int value = usersService.changePassword(users, requestDto.getNewPassword());
        if (value == 1) {
            return ResponseEntity.ok(users.getNickname() + " 님의 비밀번호를 수정하였습니다.");
        } else {
            return ResponseEntity.internalServerError().body("비밀번호 수정에 실패하였습니다.");
        }
    }
}
