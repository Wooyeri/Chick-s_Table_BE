package com.bugwarriors.chickstable.security;

import com.bugwarriors.chickstable.entity.UsersEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;

@Data
@RequiredArgsConstructor
public class AuthDetails implements UserDetails {
	/* 인증된 사용자에 대한 세부 정보를 다루는 UserDetails의 구현체(Principal 객체) */
	private static final long serialVersionUID = 1L;
	
	private UsersEntity userEntity;
	
	public AuthDetails(UsersEntity userEntity) {
		this.userEntity = userEntity;
	}
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 사용자 권한 반환
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(userEntity.getRoles()));
        
        return authorities;
    }

    @Override
    public String getPassword() {
        return userEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getUserId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}