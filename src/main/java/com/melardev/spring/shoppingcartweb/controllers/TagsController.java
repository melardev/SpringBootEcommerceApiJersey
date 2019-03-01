package com.melardev.spring.shoppingcartweb.controllers;

import com.melardev.spring.shoppingcartweb.dtos.response.base.AppResponse;
import com.melardev.spring.shoppingcartweb.dtos.response.base.ErrorResponse;
import com.melardev.spring.shoppingcartweb.dtos.response.tags.SingleTagDto;
import com.melardev.spring.shoppingcartweb.dtos.response.tags.TagsListResponse;
import com.melardev.spring.shoppingcartweb.enums.CrudOperation;
import com.melardev.spring.shoppingcartweb.errors.exceptions.PermissionDeniedException;
import com.melardev.spring.shoppingcartweb.models.Tag;
import com.melardev.spring.shoppingcartweb.models.User;
import com.melardev.spring.shoppingcartweb.services.CategoryService;
import com.melardev.spring.shoppingcartweb.services.StorageService;
import com.melardev.spring.shoppingcartweb.services.TagService;
import com.melardev.spring.shoppingcartweb.services.auth.AuthorizationService;
import com.melardev.spring.shoppingcartweb.services.auth.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Component
@Path("tags")
public class TagsController {

    @Autowired
    TagService tagService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private UsersService usersService;

    @Autowired
    private AuthorizationService authorizationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getTags(@DefaultValue("1") @QueryParam(value = "page") int page,
                            @DefaultValue("5") @QueryParam(value = "page_size") int pageSize,
                            @Context HttpServletRequest request) {
        Collection<Tag> tags = tagService.fetchAll();
        return Response.ok().entity(TagsListResponse.build(tags, request.getRequestURI())).build();
    }

    // TODO: do it the jax-rs jersey way
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("tags")
    public ResponseEntity<AppResponse> createTag(HttpServletRequest request, @RequestParam("images[]") MultipartFile[] uploadingFiles) {

        try {
            User user = usersService.getCurrentLoggedInUser();
            if (!this.authorizationCheckOnTags(CrudOperation.CREATE, user))
                throw new PermissionDeniedException("You are not allowed to create products");

            String name = request.getParameter("name");
            String description = request.getParameter("description");

            List<File> files = storageService.upload(uploadingFiles, "/images/tags");

            Tag tag = tagService.create(name, description, files);
            return new ResponseEntity<>(SingleTagDto.build(tag, true, true), HttpStatus.CREATED);

        } catch (IOException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private boolean authorizationCheckOnTags(CrudOperation operation, User user) {
        return this.authorizationCheckOnTags(operation, user, null);
    }

    private boolean authorizationCheckOnTags(CrudOperation operation, User user, Tag tag) {
        switch (operation) {
            case CREATE:
                return authorizationService.canCreateTags(user);
            case UPDATE:
                return authorizationService.canUpdateTags(user, tag);
            case DELETE:
                return authorizationService.canDeleteTags(user, tag);
            case READ:
                return authorizationService.canReadTags(user);
            default:
                return false;
        }
    }

}
