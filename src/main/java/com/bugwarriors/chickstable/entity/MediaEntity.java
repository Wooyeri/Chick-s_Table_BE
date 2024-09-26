package com.bugwarriors.chickstable.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "media")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 700)
    private String storedFilePath;

    @Column(length = 700)
    private String originalFileName;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UsersEntity users;
}