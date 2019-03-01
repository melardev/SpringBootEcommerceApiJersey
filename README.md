https://stackoverflow.com/questions/51235724/how-to-save-object-to-reactive-repository-inside-flux
https://stackoverflow.com/questions/44431870/how-can-i-add-in-reactive-java-a-new-object-to-an-existing-stream
https://www.codingame.com/playgrounds/929/reactive-programming-with-reactor-3/Flux

# Introduction
This is one of my E-commerce API app implementations. It is written in Java using Spring Boot, Hibernate and Jersey as the main dependencies.
This is not a finished project by any means, but it has a valid enough shape to be git cloned and studied if you are interested in this topic.
If you are interested in this project take a look at my other server API implementations I have made with:

- [Spring Boot + Hibernate(no Jersey)](https://github.com/melardev/SBootApiEcomMVCHibernate)
- [Node Js + Sequelize](https://github.com/melardev/ApiEcomSequelizeExpress)
- [Node Js + Bookshelf](https://github.com/melardev/ApiEcomBookshelfExpress)
- [Node Js + Mongoose](https://github.com/melardev/ApiEcomMongooseExpress)
- [Python Django](https://github.com/melardev/DjangoRestShopApy)
- [Flask](https://github.com/melardev/FlaskApiEcommerce)
- [Golang go gonic](https://github.com/melardev/api_shop_gonic)
- [Ruby on Rails](https://github.com/melardev/RailsApiEcommerce)
- [AspNet Core](https://github.com/melardev/ApiAspCoreEcommerce)
- [Laravel](https://github.com/melardev/ApiEcommerceLaravel)


## WARNING
I have mass of projects to deal with so I make some copy/paste around, if something I say is missing or is wrong, then I apologize
and you may let me know opening an issue.

# Getting started
As with most node js projects, do the following
1. git clone
2. open src/java/resources/application.yml and change the database info to yours, and ddl-auto to create
3. Then to seed you have to pass seeds as the first argument to the application, in intelliJ it is done easily, 
just go to `Run > Edit Configuration`, copy the run config for the default Spring Boot application which automatically appeared in IntelliJ,
now move to copied configuration change the name to whatever you want, expand the Environment tree, and add write seeds in program arguments:

![Fetching products page](./github_images/intellij.png)

4. Then switch back to application.yml change dd-auto to update, change the run config to the default, and run the app.
If you did not understood the above steps, then it is easy, go to DbSeeder.java, comment this: `@Profile("seeds")` and run the app,
you don't have to pass any args, the seeding process will always run, but don't worry, the seeding code checks if it has seeded before and
only seeds if it does not.

5. You can also open the api.postman_collection.json file in postman and begin issuing requests

# Features
- Authentication / Authorization
- Paging
- Admin feature (incomplete)
- CRUD operations on products, comments, tags, categories
![Fetching products page](./github_images/postman.png)
- Orders, guest users may place an order
![Database diagram](./github_images/db_structure.png)

# What you will learn
- Spring Boot
- Jersey
- Spring Web
    - JWT
    - Controllers
    - Filters
- Spring Security
- Spring Data
- Hibernate ORM
    - associations: ManyToMany, OneToMany, ManyToOne
    - transient fields
    - complex queries
    
- seed data with faker

- misc
    - project structure

# Understanding the project
The project is meant to be educational, to learn something beyond the hello world thing we find in a lot, lot of 
tutorials and blog posts. Since its main goal is educational, I try to make as much use as features of APIs, in other
words, I used different code to do the same thing over and over, there is some repeated code but I tried to be as unique
as possible so you can learn different ways of achieving the same goal.

Project structure:
- models: Mvc, it is our domain data.
- dtos: it contains our serializers, they will create the response to be sent as json. They also take care of validating the input(feature incomplete)
- controllers: well this is the mvC, our business logic.
- config: the web and security configurers.
- seeds: contains the file that seeds the database.
- uploads: a folder that will be generated when you create a product or tag or category with images
- errors: it contains the app exception handler and some custom exception classes
- services: contains some business logic for each model, and for authorization
- repositories: perform the crud operations on the database using Spring Data repositories
- annotations: contains an annotation used for confirming password and password confirmation


# TODO
- Security config antMatchers + hasRole(ADMIN) does not work, I have to use PreAuthorize as a workaround
- Make it more Jersey like project, for example implement Jwt using pure Jax RS
- For the moment I have not filtered any input, the next commit will sanitize any input from untrusted users.
- Delete Product feature, does not work because of FK issues
- Improve SQL query:
    - get Orders
    - getting product associations, it must to join instead of fetching everything in a separate query
- OrderControllers::checkout() does not work with @Valid, fix
- Show how many times a product was bought in Product JSON List and details
- It is cool to use slug for urls for pretty urls but, for POST/PUT/DELETE 
requests it is better to use Ids for performance so we can retrierve the Hibernate Proxy, anyways, the client side app will not show the ugly /users/:pid/comments/:id url
- Write a validation routine for CheckoutDto, it has to validate depending
on if addressId is set or not. if it is set, we have to use it.
- Improve User Responses, return orders count, comments count
- Refactor the ErrorResponse parsing, it should take only meaningful messages from bindingResult.getModel()
otherwise app crashes ..
- Change authorization logic from Enum based(AuthorizationPolicy) to role name based (incomplete)

# Resources
- [](https://stackoverflow.com/questions/4334970/hibernate-cannot-simultaneously-fetch-multiple-bags/4335514)
- [](https://www.concretepage.com/spring-boot/spring-boot-jersey-rest-jpa-hibernate-crud-example#resourceconfig)
- [](https://www.geekmj.org/jersey/jax-rs-multiple-files-upload-example-408/)