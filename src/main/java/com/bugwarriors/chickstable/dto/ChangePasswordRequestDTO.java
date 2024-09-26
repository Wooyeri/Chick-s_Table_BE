package com.bugwarriors.chickstable.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePasswordRequestDTO {
    private String email;
    private String password;
    private String newPassword;
}
