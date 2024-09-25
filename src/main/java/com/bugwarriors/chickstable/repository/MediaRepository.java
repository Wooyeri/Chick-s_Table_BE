package com.bugwarriors.chickstable.repository;

import com.bugwarriors.chickstable.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<UsersEntity, Long> {
    UsersEntity findByUserId(String userId);
    boolean existsByUserId(String userId);
}