package com.bugwarriors.chickstable.service;

import com.bugwarriors.chickstable.common.FileUtilsForLocal;
import com.bugwarriors.chickstable.common.FileUtilsForS3;
import com.bugwarriors.chickstable.dto.JoinRequestDTO;
import com.bugwarriors.chickstable.dto.UsersDiseaseDTO;
import com.bugwarriors.chickstable.dto.UsersInfoRequestDTO;
import com.bugwarriors.chickstable.dto.UsersInfoResponseDTO;
import com.bugwarriors.chickstable.entity.DiseaseEntity;
import com.bugwarriors.chickstable.entity.MediaEntity;
import com.bugwarriors.chickstable.entity.UsersEntity;
import com.bugwarriors.chickstable.repository.DiseaseRepository;
import com.bugwarriors.chickstable.repository.MediaRepository;
import com.bugwarriors.chickstable.repository.ScrapRepository;
import com.bugwarriors.chickstable.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private DiseaseRepository diseaseRepository;

    @Autowired
    private ScrapRepository scrapRepository;

    @Autowired
    private FileUtilsForS3 fileUtils;

    @Autowired
    private BCryptPasswordEncoder encoder;

    public long join(JoinRequestDTO requestDto) {
        try {
            // 유저 생성
            UsersEntity users = new UsersEntity();
            users.setUserId(requestDto.getId());
            users.setPassword(encoder.encode(requestDto.getPassword()));
            users.setNickname(requestDto.getNickname());
            users.setEmail(requestDto.getEmail());
            users.setRoles("ROLE_USER");

            UsersEntity createdUsers = usersRepository.save(users);
            return createdUsers.getId();
        } catch (Exception e) {
            return -1;
        }
    }

    public int changePassword(UsersEntity users, String newPassword) {
        try {
            users.setPassword(encoder.encode(newPassword));
            usersRepository.save(users);
            return 1;
        } catch (Exception e) {
            return -1;
        }
    }

    public UsersInfoResponseDTO getUsersInfo(UsersEntity users) {
        UsersInfoResponseDTO responseDTO = new UsersInfoResponseDTO();
        responseDTO.setId(users.getUserId());
        responseDTO.setEmail(users.getEmail());
        responseDTO.setNickname(users.getNickname());

        if(mediaRepository.findByUsers(users).isPresent()) {
            MediaEntity media = mediaRepository.findByUsers(users).get();
            responseDTO.setProfilePath(media.getStoredFilePath());
        }

        List<DiseaseEntity> diseases = diseaseRepository.findByUsersOrderByIdDesc(users);
        if (!diseases.isEmpty()) {
            List<UsersDiseaseDTO> diseaseList = new ArrayList<>();
            for (DiseaseEntity disease : diseases) {
                UsersDiseaseDTO diseaseDTO = new UsersDiseaseDTO();
                diseaseDTO.setName(disease.getName());
                diseaseDTO.setContents(disease.getContents());
                diseaseList.add(diseaseDTO);
            }
            responseDTO.setDiseases(diseaseList);
        }

        return responseDTO;
    }

    @Transactional
    public int updateUserInfo(UsersEntity users, UsersInfoRequestDTO requestDto, MultipartFile file) {
        try {
            // 유저 상세 정보 수정
            if (requestDto != null && !requestDto.getNickname().isBlank()) {
                users.setNickname(requestDto.getNickname());
            }

            MediaEntity media = fileUtils.parseMediaInfo(users, file);
            if (media != null) {
                mediaRepository.save(media);
                users.setMediaId(media.getId());
            }

            List<DiseaseEntity> diseases = diseaseRepository.findByUsersOrderByIdDesc(users);
            if (!diseases.isEmpty()) {
                diseaseRepository.deleteAllByUsers(users);
            }

            if(requestDto != null && !requestDto.getDiseases().isEmpty()) {
                List<UsersDiseaseDTO> diseaseList = requestDto.getDiseases();
                for (UsersDiseaseDTO disease : diseaseList) {
                    DiseaseEntity diseaseEntity = new DiseaseEntity();
                    diseaseEntity.setName(disease.getName());
                    diseaseEntity.setContents(disease.getContents());
                    diseaseEntity.setUsers(users);
                    diseaseRepository.save(diseaseEntity);
                }
            }

            usersRepository.save(users);
            return 1;
        } catch (Exception e) {
            log.error(e.toString());
            return -1;
        }
    }

    @Transactional
    public void deleteUsers(UsersEntity users) {
        try {
            scrapRepository.deleteByUsers(users);
            diseaseRepository.deleteAllByUsers(users);
            mediaRepository.deleteByUsers(users);
            usersRepository.delete(users);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    public boolean existsByUserId(String userId) {
        return usersRepository.existsByUserId(userId);
    };

    public boolean existsByEmail(String email) { return usersRepository.existsByEmail(email); };

    public boolean existsById(Long id) {
        return usersRepository.existsById(id);
    };
}
