package com.bugwarriors.chickstable.repository;

import com.bugwarriors.chickstable.entity.DiseaseEntity;
import com.bugwarriors.chickstable.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiseaseRepository extends JpaRepository<DiseaseEntity, Long> {
    List<DiseaseEntity> findByUsersOrderByIdDesc(UsersEntity users);
    void deleteAllByUsers(UsersEntity users);
}