package com.melardev.spring.shoppingcartweb.controllers;

import com.melardev.spring.shoppingcartweb.dtos.request.orders.CheckoutDto;
import com.melardev.spring.shoppingcartweb.dtos.response.base.ErrorResponse;
import com.melardev.spring.shoppingcartweb.dtos.response.orders.OrderListResponse;
import com.melardev.spring.shoppingcartweb.dtos.response.orders.OrderSingleResponse;
import com.melardev.spring.shoppingcartweb.errors.exceptions.PermissionDeniedException;
import com.melardev.spring.shoppingcartweb.models.Order;
import com.melardev.spring.shoppingcartweb.models.User;
import com.melardev.spring.shoppingcartweb.services.OrdersService;
import com.melardev.spring.shoppingcartweb.services.SettingsService;
import com.melardev.spring.shoppingcartweb.services.auth.AuthorizationService;
import com.melardev.spring.shoppingcartweb.services.auth.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Controller
@Path("/orders")
public class OrdersController {

    private final SettingsService settingsService;
    private final OrdersService ordersService;
    private final UsersService userService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    public OrdersController(SettingsService settingsService, OrdersService ordersService,
                            UsersService userService) {
        this.ordersService = ordersService;
        this.settingsService = settingsService;
        this.userService = userService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response index(@Context HttpServletRequest request,
                          @DefaultValue("1") @QueryParam(value = "page") int page,
                          @DefaultValue("5") @QueryParam("page_size") int pageSize) {
        if (page <= 0)
            page = 1;
        if (pageSize <= 0)
            pageSize = 5;

        if (this.userService.isAnonymous())
            return Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("We can not retrieve orders for anonymous users")).build();

        Page<Order> ordersPage = this.ordersService.findOrderSummariesBelongingToUser(this.userService.getCurrentLoggedInUser(), page, pageSize);
        return Response.ok().entity(OrderListResponse.build(ordersPage, request.getRequestURI())).build();
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response show(@PathParam("id") Long id) {
        User user = userService.getCurrentLoggedInUser();

        if (user == null)
            throw new PermissionDeniedException("Not logged in");

        Order order = ordersService.findById(id);
        if (!order.getUser().getId().equals(user.getId()) || !userService.isUserAdmin((user)))
            Response.status(Response.Status.FORBIDDEN).entity(new ErrorResponse("You are not authorized")).build();

        return Response.ok().entity(OrderSingleResponse.build(order)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checkout(CheckoutDto form) {
        Order order = this.ordersService.save(form, userService.getCurrentLoggedInUser());
        return Response.ok().entity(OrderSingleResponse.build(order)).build();
    }
}
