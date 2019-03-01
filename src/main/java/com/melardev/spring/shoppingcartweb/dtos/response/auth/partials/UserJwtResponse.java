package com.melardev.spring.shoppingcartweb.dtos.response.auth.partials;

import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserJwtResponse {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String email;

    private final List<String> roles;

    public UserJwtResponse(String firstName, String lastName, String username, String email, List<String> authorities) {
        this.firstName = firstName;
        this.lastName = lastName;

        this.username = username;
        this.email = email;
        roles = authorities;

    }

    public static UserJwtResponse build(String firstName, String lastName, String username, String email, Collection<? extends GrantedAuthority> authorities) {
        List<String> auths = new ArrayList<>();
        authorities.forEach(a -> auths.add(a.getAuthority().toString()));
        return new UserJwtResponse(firstName, lastName,username, email, auths);
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }



    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
