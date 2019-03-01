package com.melardev.spring.shoppingcartweb.controllers;


import com.melardev.spring.shoppingcartweb.dtos.response.products.ProductListResponse;
import com.melardev.spring.shoppingcartweb.dtos.response.products.SingleProductResponse;
import com.melardev.spring.shoppingcartweb.enums.CrudOperation;
import com.melardev.spring.shoppingcartweb.errors.exceptions.PermissionDeniedException;
import com.melardev.spring.shoppingcartweb.models.*;
import com.melardev.spring.shoppingcartweb.repository.OrdersRepository;
import com.melardev.spring.shoppingcartweb.repository.RolesRepository;
import com.melardev.spring.shoppingcartweb.repository.UsersRepository;
import com.melardev.spring.shoppingcartweb.services.*;
import com.melardev.spring.shoppingcartweb.services.auth.AuthorizationService;
import com.melardev.spring.shoppingcartweb.services.auth.UsersService;
import com.melardev.spring.shoppingcartweb.services.interfaces.ISettingsService;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.util.List;

@CrossOrigin
@Path("/products")
public class ProductsController {

    private final ProductsService productsService;

    protected ISettingsService settingsService;
    private AuthorizationService authorizationService;


    @Autowired
    private UsersService usersService;
    private CommentsService commentsService;
    @Autowired
    private CategoryService categoriesService;
    @Autowired
    private TagService tagService;
    @Autowired
    private StorageService storageService;


    @Autowired
    public ProductsController(ProductsService productsService, AuthorizationService authorizationService,
                              CommentsService commentsService) {
        super();
        this.authorizationService = authorizationService;
        this.productsService = productsService;
        this.commentsService = commentsService;
    }

    @Autowired
    UsersRepository usersRepository;
    @Autowired
    RolesRepository rolesRepository;
    @Autowired
    OrdersRepository ordersRepository;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response index(@DefaultValue("1") @QueryParam(value = "page") int page,
                          @DefaultValue("5") @QueryParam("page_size") int pageSize,
                          @Context HttpServletRequest request) {

        if (page <= 0)
            page = 1;
        if (pageSize <= 0)
            pageSize = 5;
        Page<Product> productsPage = productsService.findAllForSummary(page, pageSize);
        return Response.ok().entity(ProductListResponse.build(productsPage, request.getRequestURI())).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("by_tag/{tag_name}")
    public Response getProductsByTag(@PathParam("tag_name") String tagName,
                                     @Context HttpServletRequest request,
                                     @DefaultValue("1") @QueryParam(value = "page") int page,
                                     @DefaultValue("5") @QueryParam("page_size") int pageSize) {

        if (page <= 0)
            page = 1;
        if (pageSize <= 0)
            pageSize = 5;

        Page<Product> productsPage = productsService.findByTagName(tagName, page, pageSize);
        return Response.ok().entity(ProductListResponse.build(productsPage, request.getRequestURI())).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("by_category/{category_name}")
    public Response getByCategory(@PathParam("category_name") String categoryName,
                                  @Context HttpServletRequest request,
                                  @DefaultValue("1") @QueryParam(value = "page") int page,
                                  @DefaultValue("5") @QueryParam("page_size") int pageSize) {

        Page<Product> productsPage = productsService.getByCategory(categoryName, page, pageSize);
        return Response.ok().entity(ProductListResponse.build(productsPage, request.getRequestURI())).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{slug}")
    public Response show(@PathParam("slug") String slug) {

        Product product = productsService.findBySlug(slug);
        return Response.ok().entity(SingleProductResponse.build(product)).build();

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("by_id/{id}")
    public SingleProductResponse show(@PathParam("id") Long id) {
        Product product = productsService.findById(id);
        return SingleProductResponse.build(product);
    }

    /* // TODO: It does not work, why?
        // https://www.geekmj.org/jersey/jax-rs-multiple-files-upload-example-408/
        @PreAuthorize("hasRole('ROLE_ADMIN')")
        @Consumes(MediaType.MULTIPART_FORM_DATA)
        @POST
        public Response create(@FormDataParam("images") List<FormDataBodyPart> formDataBodyParts,
                               @FormDataParam("files") FormDataContentDisposition fileDispositions,
                               @Context HttpServletRequest request) throws IOException {

            User user = usersService.getCurrentLoggedInUser();
            if (!this.authorizationCheckOnProducts(CrudOperation.CREATE, user))
                throw new PermissionDeniedException("You are not allowed to create products");

            final Product product = new Product();
            request.getParameterMap().forEach((key, value) -> {
                if (key.equalsIgnoreCase("name"))
                    product.setName(value[0]);
                if (key.equalsIgnoreCase("description"))
                    product.setDescription(value[0]);
                if (key.equalsIgnoreCase("price"))
                    product.setPrice(Double.parseDouble(value[0]));
                if (key.equalsIgnoreCase("stock"))
                    product.setStock(Integer.parseInt(value[0]));

                // TODO: improve this, I tried to use regex but it does not work, someone knows why?
                if (key.indexOf("tags[") != -1) {
                    key = key.replace("tags[", "").replace("]", "");
                    product.getTags().add(new Tag(key, value[0]));
                }

                if (key.indexOf("categories[") != -1) {
                    key = key.replace("categories[", "").replace("]", "");
                    product.getCategories().add(new Category(key, value[0]));
                }
            });

            List<File> files = storageService.upload(formDataBodyParts, "/images/products");

            productsService.createWithDettachedTagsAndCategories(product, files);
            return Response.ok().entity(SingleProductResponse.build(product)).status(Response.Status.CREATED).build();
        }
    */

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response uploadFiles(FormDataMultiPart multiPart, @Context HttpServletRequest request) throws IOException {

        // BodyPartEntity bodyPartEntity = ((BodyPartEntity) multiPart.getField("image").getEntity());

        User user = usersService.getCurrentLoggedInUser();
        if (!this.authorizationCheckOnProducts(CrudOperation.CREATE, user))
            throw new PermissionDeniedException("You are not allowed to create products");

        final Product product = new Product();
        multiPart.getFields().forEach((key, value) -> {
            if (key.equalsIgnoreCase("name"))
                product.setName(value.get(0).getValue());
            if (key.equalsIgnoreCase("description"))
                product.setDescription(value.get(0).getValue());
            if (key.equalsIgnoreCase("price"))
                product.setPrice(Double.parseDouble(value.get(0).getValue()));
            if (key.equalsIgnoreCase("stock"))
                product.setStock(Integer.parseInt(value.get(0).getValue()));

            // TODO: improve this, I tried to use regex but it does not work, someone knows why?
            if (key.indexOf("tags[") != -1) {
                key = key.replace("tags[", "").replace("]", "");
                product.getTags().add(new Tag(key, value.get(0).getValue()));
            }

            if (key.indexOf("categories[") != -1) {
                key = key.replace("categories[", "").replace("]", "");
                product.getCategories().add(new Category(key, value.get(0).getValue()));
            }
        });


        List<FormDataBodyPart> bodyParts = multiPart.getFields("images[]");
        List<File> files = storageService.upload(bodyParts, "/images/products");

        productsService.createWithDettachedTagsAndCategories(product, files);
        return Response.ok().entity(SingleProductResponse.build(product)).status(Response.Status.CREATED).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{slug}/comments")
    public Response index(@PathParam("slug") String slug,
                          @Context HttpServletRequest request,
                          @DefaultValue("1") @QueryParam(value = "page") int page,
                          @DefaultValue("5") @QueryParam(value = "page_size") int pageSize,
                          @Context CommentsController controller) {
        // The reasoning behind this is that you can not map CommentsController to @Path("")
        // you have to provide a non empty string, since we already mapped /products to ProductController, we can not use it
        // again for CommentsController, the trick is to place the code logic in CommentController, and then inject the CommentsController
        // into this method through the JAX-RS @Context
        return controller.index(slug, request, page, pageSize);
    }

    private boolean authorizationCheck(CrudOperation operation, Long id) {
        return this.authorizationCheck(this.commentsService.findByIdNotThrow(id), operation);
    }

    private boolean authorizationCheck(Comment comment, CrudOperation operation) {
        switch (operation) {
            case CREATE:
                return this.authorizationService.canCreateComments(usersService.getCurrentLoggedInUser());
            case UPDATE:
                return this.authorizationService.canUpdateComments(comment, usersService.getCurrentLoggedInUser());
            case DELETE:
                return this.authorizationService.canDeleteComments(comment, usersService.getCurrentLoggedInUser());
            default:
                return false;
        }
    }

    private boolean authorizationCheckOnProducts(CrudOperation operation, User user) {
        if (user == null)
            user = usersService.getCurrentLoggedInUser();

        switch (operation) {
            case CREATE:
                return this.authorizationService.canCreateProducts(user);
            case UPDATE:
                return this.authorizationService.canUpdateProducts(user);
            case DELETE:
                return this.authorizationService.canDeleteProducts(user);
            default:
                return false;
        }
    }
}