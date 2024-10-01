package com.bugwarriors.chickstable.controller;

import com.bugwarriors.chickstable.common.AuthUser;
import com.bugwarriors.chickstable.dto.ScrapListResponseDTO;
import com.bugwarriors.chickstable.dto.ScrapRequestDTO;
import com.bugwarriors.chickstable.dto.ScrapResponseDTO;
import com.bugwarriors.chickstable.entity.ScrapEntity;
import com.bugwarriors.chickstable.entity.UsersEntity;
import com.bugwarriors.chickstable.service.ScrapService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/scrap")
@Slf4j
public class ScrapController {

    @Autowired
    private ScrapService scrapService;

    @PostMapping
    public ResponseEntity<?> scrap(
            @AuthUser UsersEntity users,
            @RequestBody ScrapRequestDTO requestDTO
    ) {
        if (users == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        long createdScrapId = scrapService.scrap(users, requestDTO);

        if (createdScrapId == -1) {
            return ResponseEntity.internalServerError().body("스크랩에 실패하였습니다.");
        }

        return ResponseEntity.created(URI.create("/scrap/" + createdScrapId)).body("스크랩에 성공하였습니다.");
    }

    @GetMapping
    public ResponseEntity<?> getScrapList(
            @AuthUser UsersEntity users
    ) {
        if (users == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ScrapListResponseDTO responseDTO = scrapService.getScrapList(users);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{scrap_id}")
    public ResponseEntity<?> getScrap(
            @AuthUser UsersEntity users,
            @PathVariable(value = "scrap_id") Long scrapId
    ) {
        if (users == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<ScrapEntity> optionalScrap = scrapService.findById(scrapId);

        if (optionalScrap.isPresent()) {
            ScrapEntity scrap = optionalScrap.get();
            if (scrap.getUsers().equals(users)) {
                ScrapResponseDTO responseDTO = scrapService.getScrap(scrap);
                return ResponseEntity.ok(responseDTO);
            } else {
                // id에 해당하는 스크랩이 사용자가 생성한 스크랩이 아닌 경우
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("본인의 스크랩만 조회할 수 있습니다.");
            }
        } else {
            // id에 해당하는 스크랩이 존재하지 않는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당하는 스크랩이 존재하지 않습니다.");
        }
    }

    @DeleteMapping("/{scrap_id}")
    public ResponseEntity<?> deleteScrap(
            @AuthUser UsersEntity users,
            @PathVariable(value = "scrap_id") Long scrapId
    ) {
        if (users == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<ScrapEntity> optionalScrap = scrapService.findById(scrapId);

        if (optionalScrap.isPresent()) {
            ScrapEntity scrap = optionalScrap.get();
            if (scrap.getUsers().equals(users)) {
                int deleted = scrapService.deleteScrap(scrap);
                if(deleted == 1) {
                    return ResponseEntity.ok(scrap.getTitle() + " 스크랩을 삭제하였습니다.");
                } else {
                    return ResponseEntity.internalServerError().body("스크랩 삭제에 실패하였습니다.");
                }
            } else {
                // id에 해당하는 스크랩이 사용자가 생성한 스크랩이 아닌 경우
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("본인의 스크랩만 삭제할 수 있습니다.");
            }
        } else {
            // id에 해당하는 스크랩이 존재하지 않는 경우
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당하는 스크랩이 존재하지 않습니다.");
        }
    }
}
