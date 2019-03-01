package com.melardev.spring.shoppingcartweb.controllers;

import com.melardev.spring.shoppingcartweb.config.JwtProvider;
import com.melardev.spring.shoppingcartweb.dtos.request.LoginRequestDto;
import com.melardev.spring.shoppingcartweb.dtos.request.RegisterDto;
import com.melardev.spring.shoppingcartweb.dtos.response.auth.LoginResponseDto;
import com.melardev.spring.shoppingcartweb.dtos.response.base.ErrorResponse;
import com.melardev.spring.shoppingcartweb.dtos.response.base.SuccessResponse;
import com.melardev.spring.shoppingcartweb.models.Role;
import com.melardev.spring.shoppingcartweb.models.User;
import com.melardev.spring.shoppingcartweb.services.RolesService;
import com.melardev.spring.shoppingcartweb.services.auth.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@Controller
@Path("auth")
public class AuthController {


    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    private UsersService usersService;
    @Autowired
    private RolesService rolesService;

    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(RegisterDto dto) {
        if (usersService.existsByUsername(dto.getUsername())) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("username", "Username already taken");
            return Response.status(Response.Status.NOT_ACCEPTABLE).entity(new ErrorResponse(errors)).build();
        }

        if (usersService.existsByEmail(dto.getEmail())) {
            Map<String, Object> errors = new HashMap<>();
            errors.put("email", "Email already taken");
            return Response.status(Response.Status.BAD_REQUEST).entity(new ResponseEntity<>(new ErrorResponse(errors), HttpStatus.BAD_REQUEST)).build();
        }

        HashSet<Role> roles = new HashSet<Role>(Collections.singletonList(rolesService.fetchOrCreate("ROLE_USER")));

        // Creating user's account
        User user = new User(dto.getFirstName(), dto.getLastName(), dto.getEmail(),
                dto.getUsername(), dto.getPassword(), roles);

        usersService.createUser(user);

        return Response.status(Response.Status.CREATED).entity(new SuccessResponse("User registered successfully")).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(LoginRequestDto loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);
        User userPrinciple = (User) authentication.getPrincipal();

        User user = ((User) authentication.getPrincipal());
        return Response.ok().entity(LoginResponseDto.build(jwt, user)).build();
    }
}