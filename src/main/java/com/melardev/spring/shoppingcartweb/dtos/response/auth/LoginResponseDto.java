package com.melardev.spring.shoppingcartweb.dtos.response.auth;


import com.melardev.spring.shoppingcartweb.dtos.response.auth.partials.UserJwtResponse;
import com.melardev.spring.shoppingcartweb.dtos.response.base.SuccessResponse;
import com.melardev.spring.shoppingcartweb.models.User;

public class LoginResponseDto extends SuccessResponse {
    private final UserJwtResponse user;
    private String scheme = "Bearer";
    private String token;

    private LoginResponseDto(String jwt, UserJwtResponse user) {
        this.token = jwt;
        this.user = user;
    }


    public static LoginResponseDto build(String jwt, User user) {
        return new LoginResponseDto(jwt, UserJwtResponse.build(user.getFirstName(), user.getLastName(), user.getUsername(), user.getEmail(),
                user.getAuthorities()));
    }

    public UserJwtResponse getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

}