package com.bugwarriors.chickstable.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsersEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 식별값

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId; // 사용자 ID

    private String password; // 사용자 PW

    private String nickname; // 사용자 닉네임

    @Column(name = "email", unique = true, nullable = false)
    private String email; // 사용자 이메일

    private String roles;

    private Long mediaId; // 프로필 사진 식별값
}