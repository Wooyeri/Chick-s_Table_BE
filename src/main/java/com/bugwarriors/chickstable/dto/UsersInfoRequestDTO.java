package com.bugwarriors.chickstable.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UsersInfoRequestDTO {
    private String nickname;
    private List<UsersDiseaseDTO> diseases;
}
