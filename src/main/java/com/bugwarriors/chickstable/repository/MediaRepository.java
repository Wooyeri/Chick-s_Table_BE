package com.bugwarriors.chickstable.repository;

import com.bugwarriors.chickstable.entity.MediaEntity;
import com.bugwarriors.chickstable.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MediaRepository extends JpaRepository<MediaEntity, Long> {
    Optional<MediaEntity> findByUsers(UsersEntity users);
}