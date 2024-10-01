package com.bugwarriors.chickstable.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UsersInfoResponseDTO {
    private String id;
    private String email;
    private String nickname;
    private String profilePath;
    private List<UsersDiseaseDTO> diseases;
}
