package com.melardev.spring.shoppingcartweb.controllers;

import com.melardev.spring.shoppingcartweb.dtos.response.addresses.AddressListDto;
import com.melardev.spring.shoppingcartweb.errors.exceptions.PermissionDeniedException;
import com.melardev.spring.shoppingcartweb.models.Address;
import com.melardev.spring.shoppingcartweb.models.User;
import com.melardev.spring.shoppingcartweb.services.AddressService;
import com.melardev.spring.shoppingcartweb.services.auth.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Controller
@Path("/addresses")
public class AddressesController {

    @Autowired
    private UsersService usersService;
    @Autowired
    private AddressService addressesService;

    public AddressesController() {
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response myAddresses() {
        User user = usersService.getCurrentLoggedInUser();
        if (user == null)
            throw new PermissionDeniedException("You are not logged In");
        List<Address> addresses = addressesService.fetchByUserId(user.getId());
        return Response.ok().entity(AddressListDto.build(addresses)).build();
    }
}
