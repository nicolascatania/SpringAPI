# SPRING RESTFUL API
I am currently learning to create APIs with SpringBoot so I built this, a simple concept based in users that can store products that they sell, this products can be asociated to pre created categories, and a lot of functionalities and features mentioned below.

## Features
- Spring MVC Pattern.
- Data Transfer Object Pattern.
- Inversion of control.
- Dependency injection.
- Spring Security and JWT based authorization.
- CRUD for different entyties. For example: admins are be able to se the full list of users and remove them or add/remove the rol ADMIN to any user, moreover, they will create, eliminate or update products. On the other hand, a normal person can register (create an user) himself, login and then edit its profile, also delete their own profile.
- Password encryption with BCrypt.
- Implementation of authorization with roles.
- Documentation with SpringDocs and Swagger UI. 
- Filter products by category.(Developed in the frontend, ill clean this readme when I make the frontend repository)
- Sorting products by certain fields. (Developed in frontend)

## What is coming...
- I'll try to add more funciontalities to this.
- Unit Testing with Junit and Mockito.
- OAtuh2.
- DEPLOY

## How to read the docs
- Go to: http://localhost:8080/doc/swagger-ui/index.html after you start the application and make sure to login in the auth endpoint with username: user1, email:"" (I'll fix it is not necessary to log in with email) and password: 12345, u will get access to see and try all the enpoints avaiable for users.
