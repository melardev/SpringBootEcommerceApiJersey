package com.melardev.spring.shoppingcartweb.controllers;

import com.melardev.spring.shoppingcartweb.dtos.response.base.AppResponse;
import com.melardev.spring.shoppingcartweb.dtos.response.base.ErrorResponse;
import com.melardev.spring.shoppingcartweb.dtos.response.categories.CategoriesListResponse;
import com.melardev.spring.shoppingcartweb.dtos.response.categories.SingleCategoryDto;
import com.melardev.spring.shoppingcartweb.enums.CrudOperation;
import com.melardev.spring.shoppingcartweb.errors.exceptions.PermissionDeniedException;
import com.melardev.spring.shoppingcartweb.models.Category;
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
import org.springframework.stereotype.Controller;
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
import java.util.List;
import java.util.Set;

@Path("categories")
@Controller
public class CategoriesController {

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
    public Response getCategories(@DefaultValue("1") @QueryParam(value = "page") int page,
                                  @DefaultValue("5") @QueryParam(value = "page_size") int pageSize,
                                  @Context HttpServletRequest request) {
        Set<Category> categories = categoryService.fetchAll();
        return Response.ok().entity(CategoriesListResponse.build(categories, request.getRequestURI())).build();
    }

    // TODO: do it the jax-rs jersey way
    @PreAuthorize("hasRole('ADMIN')") // Remember, if you do not set the prefix, it will add ROLE_ prefix for you
    @PostMapping("categories")
    public ResponseEntity<AppResponse> createCategory(HttpServletRequest request, @RequestParam("images[]") MultipartFile[] uploadingFiles) {

        try {
            User user = usersService.getCurrentLoggedInUser();
            if (!this.authorizationCheckOnCategories(CrudOperation.CREATE, user))
                throw new PermissionDeniedException("You are not allowed to create products");

            String name = request.getParameter("name");
            String description = request.getParameter("description");

            List<File> files = storageService.upload(uploadingFiles, "/images/categories");

            Category category = categoryService.create(name, description, files);
            return new ResponseEntity<>(SingleCategoryDto.build(category, true, true), HttpStatus.CREATED);

        } catch (IOException e) {
            return new ResponseEntity<>(new ErrorResponse(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private boolean authorizationCheckOnCategories(CrudOperation crudOperation, User user) {
        return authorizationCheckCategories(crudOperation, user, null);
    }

    private boolean authorizationCheckCategories(CrudOperation operation, User user, Category category) {
        switch (operation) {
            case CREATE:
                return authorizationService.canCreateCategories(user);
            case UPDATE:
                return authorizationService.canUpdateCategories(user, category);
            case DELETE:
                return authorizationService.canDeleteCategories(user);
            case READ:
                return authorizationService.canReadCategories(user);
            default:
                return false;
        }
    }
}
