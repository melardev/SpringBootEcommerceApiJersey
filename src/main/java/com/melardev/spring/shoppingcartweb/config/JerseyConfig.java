package com.melardev.spring.shoppingcartweb.config;

import com.melardev.spring.shoppingcartweb.admin.controllers.OrdersController;
import com.melardev.spring.shoppingcartweb.controllers.*;
import com.melardev.spring.shoppingcartweb.filters.JaxAppCorsFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/api")
@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        super(MultiPartFeature.class);
        register(ProductsController.class);
        register(CommentsController.class);
        register(AddressesController.class);
        register(TagsController.class);
        register(CategoriesController.class);
        register(OrdersController.class);
        register(com.melardev.spring.shoppingcartweb.controllers.OrdersController.class);
        register(AuthController.class);
        register(UsersController.class);
        register(JaxAppCorsFilter.class);
    }

}
