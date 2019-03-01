package com.melardev.spring.shoppingcartweb.controllers;


import com.melardev.spring.shoppingcartweb.dtos.response.base.SuccessResponse;
import com.melardev.spring.shoppingcartweb.dtos.response.comments.CommentListResponse;
import com.melardev.spring.shoppingcartweb.dtos.response.comments.SingleCommentDto;
import com.melardev.spring.shoppingcartweb.enums.CrudOperation;
import com.melardev.spring.shoppingcartweb.errors.exceptions.PermissionDeniedException;
import com.melardev.spring.shoppingcartweb.forms.CommentForm;
import com.melardev.spring.shoppingcartweb.models.Comment;
import com.melardev.spring.shoppingcartweb.models.Product;
import com.melardev.spring.shoppingcartweb.models.User;
import com.melardev.spring.shoppingcartweb.services.CommentsService;
import com.melardev.spring.shoppingcartweb.services.ProductsService;
import com.melardev.spring.shoppingcartweb.services.auth.AuthorizationService;
import com.melardev.spring.shoppingcartweb.services.auth.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("comments")
public class CommentsController {

    private final AuthorizationService authorizationService;
    private final CommentsService commentsService;

    private final UsersService usersService;
    private final ProductsService productsService;


    @Autowired
    public CommentsController(CommentsService commentsService,
                              AuthorizationService authorizationService,
                              UsersService usersService, ProductsService productsService) {
        this.commentsService = commentsService;
        this.authorizationService = authorizationService;
        this.productsService = productsService;
        this.usersService = usersService;

    }

    // This is triggered from ProductsController
    public Response index(String slug, HttpServletRequest request,
                          int page, int pageSize) {
        Page<Comment> commentsPage = this.commentsService.fetchCommentsFromProductWithSlug(slug, page, pageSize);
        return Response.ok().entity(CommentListResponse.build(commentsPage, request.getRequestURI())).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public SingleCommentDto show(@PathParam("id") Long id) {
        Comment comment = this.commentsService.findById(id);
        return SingleCommentDto.build(comment);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/from_user/{user_id}")
    public CommentListResponse fromUser(@PathParam("user_id") Long id,
                                        @Context HttpServletRequest request,
                                        @QueryParam(value = "page") int page,
                                        @QueryParam(value = "page_size") int pageSize) {

        User user = this.usersService.findById(id);
        Page<Comment> comments = this.commentsService.getCommentsFromUserWithId(user.getId(), page, pageSize);
        return CommentListResponse.build(comments, "");
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/products/{slug}/comments")
    public Response create(@PathParam("slug") String slug, CommentForm form) {
        if (this.isNotAuthorized(CrudOperation.CREATE, null))
            throw new PermissionDeniedException("Permission denied");

        Product product = this.productsService.findById(slug);
        User user = this.usersService.getCurrentLoggedInUser();
        Comment comment = this.commentsService.save(form.getContent(), product, user);

        return Response.ok().entity(SingleCommentDto.build(comment)).status(Response.Status.CREATED).build();
    }


    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response update(@PathParam(value = "id") Long id, CommentForm form) {
        if (this.isNotAuthorized(CrudOperation.UPDATE, id))
            throw new PermissionDeniedException("You are not allowed to this comment");

        User user = this.usersService.getCurrentLoggedInUser();
        Comment comment = this.commentsService.update(id, form.getContent(), user);

        return Response.ok().entity(SingleCommentDto.build(comment)).status(Response.Status.OK).build();
    }


    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    public Response delete(@PathParam("id") Long id) {
        if (this.isNotAuthorized(CrudOperation.DELETE, id))
            throw new PermissionDeniedException();

        Comment comment = this.commentsService.findById(id);
        if (comment == null)
            throw new PermissionDeniedException();

        this.commentsService.delete(comment);
        return Response.ok().entity(new SuccessResponse("Deleted Successfully")).build();
    }

    private boolean isNotAuthorized(CrudOperation operation, Long id) {
        return !isAuthorized(operation, id);
    }

    private boolean isAuthorized(CrudOperation operation, Long id) {
        return this.isAuthorized(this.commentsService.findByIdNotThrow(id), operation);
    }

    private boolean isAuthorized(Comment comment, CrudOperation operation) {
        switch (operation) {
            case CREATE:
                return this.authorizationService.canCreateComments(usersService.getCurrentLoggedInUser());
            case UPDATE:
                return this.authorizationService.canUpdateComments(comment, usersService.getCurrentLoggedInUser());
            case DELETE:
                return this.authorizationService.canDeleteComments(comment);
            default:
                return false;
        }
    }

}