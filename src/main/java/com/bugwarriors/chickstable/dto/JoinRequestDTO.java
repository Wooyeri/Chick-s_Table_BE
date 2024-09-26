package com.bugwarriors.chickstable.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequestDTO {
    private String id;
    private String nickname;
    private String email;
    private String password;
    private String passwordConfirm;

    public boolean checkPassword() {
        return this.password.equals(this.passwordConfirm);
    }
}
