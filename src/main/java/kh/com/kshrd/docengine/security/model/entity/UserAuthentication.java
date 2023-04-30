package kh.com.kshrd.docengine.security.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;


import java.util.Collections;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserAuthentication implements UserDetails {

    private UUID userId;
    private String username;
    private String email;
    private String password;
    private String profileImage;
    private boolean isEnable;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        return Collections.singleton(authority);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public String getUserName() {

        return this.username;
    }

    @Override
    public String getUsername() {

        return this.email;
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
        return this.isEnable;
    }
}
