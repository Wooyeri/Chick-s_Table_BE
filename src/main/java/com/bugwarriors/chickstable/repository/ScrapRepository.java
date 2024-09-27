package com.bugwarriors.chickstable.repository;

import com.bugwarriors.chickstable.entity.ScrapEntity;
import com.bugwarriors.chickstable.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapRepository extends JpaRepository<ScrapEntity, Long> {
    List<ScrapEntity> findAllByUsers(UsersEntity users);
    void deleteByUsers(UsersEntity users);
}