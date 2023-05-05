# Smartmat - Project in System Development 2 IDATT2106

## Description
Smartmat is a simple app for organising your refrigerator and shopping list, with the purpose of reducing food waste. Smartmat allows you and your family to easily plan your shopping trips, and also keep track of your refrigerator at home. To reduce food waste the app recommends recipes based on which foods are soon to expire, thus reminding you to use all the ingredients you buy. Food waste is a large enviromental problem and with Smartmat you can contribute to the solution.


### Table of contents
-[Intended audience] (###Intended audience)
-[Database] (###Database (database))
-[Frameworks used]
-[Application]
-[Security]
-[How to run and install the backend]
-[How to run and install the frontend]
-[How to use the application]
-[Team members]
-[Future work]

<br>

### Intended audience
The intended audience for this application are primarily private households wanting to control their food waste. Smartmat also focuses on households with smaller children, as they can recieve an account with limited functionality. Currently the application is only available in English, but future versions aim to add support for multiple languages.

<br>

### Database (database)

(image)

<br>

### Frameworks used

Smartmat is built on the following:
-Vue.js in frontend
-Vuetify for frontend design
-SpringBoot in backend
-JWT for safe user registration and authentication
-MySQL for data storage (Database hosted on available NTNU servers)
-Swagger for API documentation
-H2 for quick in-memory testing
-ChatGPT API for generating recipes

<br>

### Application
Smart is built upon the principle of Separation of concerns, which implies that it is divided into different sections. Each section handles a specific concern, leaving other concerns to the other sections. This ensures great code readability as well as easy modifications, since one section has minimal impact on other sections.
Below are the different sections and how we have implemented them:

**The database section** This is the model folder. Each class in this folder creates a table in the database on startup, complete with attributes and relations.

**The persistence section** This is the repository folder. This folder contains interfaces that implements JPARepository which enables us to access the database and perform queries.

**The business section** This is the service folder. All the logical operations are performed in this folder, with different methods for saving, retrieving, deleting and updating data from the database.

**The presentation section** This is the controller folder. This folder contains all the controllers used to communicate with the database. These controllers again contains endpoints that the frontend uses to manipulate the database. The controllers also handles all validation of incoming data and error handling, sending only sanitary data down to the business section. 

In our code, each model has a correlating repository, and each controller has a correlating service. Each service may use one or more repository. They are grouped since not every repository serves a purpose on its own. We have worked with the theory that every endpoint needs to serve a specific purpose. Any redundant enpoints, and its similarly redundant services will be deleted.

### Security
The application uses tokens provided by JWT to authenticate a user each time they maneuver the application. To use the application and access their data, they need to log in with their email and password. If correct, they recieve a token which is stored in the frontend ????. This token needs to be provided with every API-call, in order to access the endpoint. The token is deleted from the frontend when logging out. Using BCryptPasswordEncoder, all passwords are salted and hashed in the database, no passwords are stored in clear text. 




