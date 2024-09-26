package com.bugwarriors.chickstable.common;

import com.bugwarriors.chickstable.entity.MediaEntity;
import com.bugwarriors.chickstable.entity.UsersEntity;
import com.bugwarriors.chickstable.repository.MediaRepository;
import com.bugwarriors.chickstable.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Component
@Slf4j
public class FileUtilsForS3 {
	@Autowired
	private S3Service s3Service;

	@Autowired
	private MediaRepository mediaRepository;

	public MediaEntity parseMediaInfo(UsersEntity users, MultipartFile file) throws Exception {
		// 파일 없을 시 null 반환
		if (ObjectUtils.isEmpty(file)) {
			return null;
		}

		// 파일 정보를 저장할 객체를 생성: 해당 메서드에서 반환하는 값
		MediaEntity mediaEntity = new MediaEntity();

		// 해당 Users에 해당하는 Media Entity 존재하는지 확인
		Optional<MediaEntity> storedMediaOptional = mediaRepository.findByUsers(users);

		if (storedMediaOptional.isPresent()) {
			// 이전 이미지 삭제
			s3Service.delete(storedMediaOptional.get().getOriginalFileName());

			// 이미 존재하는 Image Entity 불러오기
			mediaEntity = storedMediaOptional.get();
		}

		// S3 파일 업로드 설정
		// 파일명 지정 (겹치면 안되고, 확장자 빼먹지 않도록 조심!)
		String fileName = System.nanoTime() + file.getOriginalFilename();
		// 파일데이터와 파일명 넘겨서 S3에 저장
		s3Service.upload(file, fileName);

		// 파일 정보를 리스트에 저장
		mediaEntity.setStoredFilePath(s3Service.getPresignedURL(fileName));
		mediaEntity.setOriginalFileName(fileName);
		mediaEntity.setUsers(users);

		return mediaEntity;
	}

}
