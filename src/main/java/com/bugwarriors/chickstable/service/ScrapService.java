package com.bugwarriors.chickstable.service;

import com.bugwarriors.chickstable.dto.ScrapListResponseDTO;
import com.bugwarriors.chickstable.dto.ScrapListResponseInnerDTO;
import com.bugwarriors.chickstable.dto.ScrapRequestDTO;
import com.bugwarriors.chickstable.dto.ScrapResponseDTO;
import com.bugwarriors.chickstable.entity.ScrapEntity;
import com.bugwarriors.chickstable.entity.UsersEntity;
import com.bugwarriors.chickstable.repository.ScrapRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ScrapService {
    @Autowired
    private ScrapRepository scrapRepository;

    // 스크랩하기
    public long scrap(UsersEntity users, ScrapRequestDTO requestDTO) {
        try {
            ScrapEntity scrap = new ScrapEntity();
            scrap.setTitle(requestDTO.getTitle());
            scrap.setContents(requestDTO.getContents());
            scrap.setUsers(users);

            ScrapEntity createdScrap = scrapRepository.save(scrap);
            return createdScrap.getId();
        } catch (Exception e) {
            log.error(e.toString());
            return -1;
        }
    }

    // 스크랩 리스트 전체 조회
    public ScrapListResponseDTO getScrapList(UsersEntity users) {
        ScrapListResponseDTO responseDTO = new ScrapListResponseDTO();

        List<ScrapEntity> scrapList = scrapRepository.findAllByUsers(users);
        if (!scrapList.isEmpty()) {
            List<ScrapListResponseInnerDTO> scrapDTOList = new ArrayList<>();
            for (ScrapEntity scrap : scrapList) {
                ScrapListResponseInnerDTO scrapDTO = new ScrapListResponseInnerDTO();
                scrapDTO.setId(scrap.getId());
                scrapDTO.setTitle(scrap.getTitle());
                scrapDTOList.add(scrapDTO);
            }
            responseDTO.setScraps(scrapDTOList);
        }
        return responseDTO;
    }

    // 스크랩 단일 조회
    public ScrapResponseDTO getScrap(ScrapEntity scrap) {
        ScrapResponseDTO responseDTO = new ScrapResponseDTO();
        responseDTO.setId(scrap.getId());
        responseDTO.setTitle(scrap.getTitle());
        responseDTO.setContents(scrap.getContents());
        return responseDTO;
    }

    // 스크랩 리스트 하나 삭제
    @Transactional
    public int deleteScrap(ScrapEntity scrap) {
        try {
            scrapRepository.delete(scrap);
            return 1;
        } catch (Exception e) {
            log.error(e.toString());
            return -1;
        }
    }

    public Optional<ScrapEntity> findById(long id) {
        return scrapRepository.findById(id);
    }
}
