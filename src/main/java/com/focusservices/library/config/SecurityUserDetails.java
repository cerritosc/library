/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.focusservices.library.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Setter
@Getter
public class SecurityUserDetails implements UserDetails {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String cuser;
    private String role;
    private List<Auth> authorities = new ArrayList<>();
    private List<String> roles = new ArrayList<>();

    public void addGrantedAuthority(String name) {
        Auth auth = new Auth();
        auth.setName(name);
        if (this.authorities.stream().noneMatch(o -> o.getName().equals(name))) {
            this.authorities.add(auth);
        }

    }

    public void addRole(String name) {
        roles.add(name);
    }

    public List<String> getRoles() {
        return this.roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.cuser;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;// To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEnabled() {
        return true; // To change body of generated methods, choose Tools | Templates.
    }

    @Setter
    @Getter
    public class Auth implements GrantedAuthority {

        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private String name;

        @Override
        public String getAuthority() {
            return this.name;
        }

    }
    
    public boolean isLibrarian() {
        return "L".equals(this.role)? true : false;
    }

}
