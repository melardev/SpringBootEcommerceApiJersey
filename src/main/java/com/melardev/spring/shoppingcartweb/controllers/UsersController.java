package com.melardev.spring.shoppingcartweb.controllers;

import com.melardev.spring.shoppingcartweb.dtos.request.RegisterDto;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@CrossOrigin
@Path("users")
public class UsersController {

    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response registerUser(RegisterDto createUserDto, @Context AuthController authController) {
        return authController.register(createUserDto);
    }

}
