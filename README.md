# POWER TINDER

This project is composed of two modules :  
* **powertinder-api**, a proxy that ease usage of the tinder API  
  * The Tinder API proxy (powertinder-api) could be used as a way to bypass the CORS restriction if you want to develop you
 own webapp querying the tinder API
  * Automatic retrieval of access token when calling an endpoint
  * Swagger documentation to easily  see and try available endpoints
* **powertinder-web**, a simple work in progress (and very ugly) webapp : 
  * Ability to change your location by specifying the latitude and longitude you want
  * Like several people in on click

## Requirements
* Java 8 (powertinder-api)
* node / npm (powertinder-web)

## How to run the app
### powertinder-api
* First you need to setup your user infos (facebook_id, facebook_email, facebook_password) in order to get access to the Tinder API. 
To do so fill in your infos in the `user.yml` config file in the root directory of **powertinder-api** project

* Start backend by specifying location of the `user.yml` file and the chromedriver executable :   
`` $> powertinder-api> ./mvnw spring-boot:run -Dspring-boot.run.arguments=--spring.config.location=user.yml -Dspring-boot.run.jvmArguments="-Dwebdriver.chrome.driver=./webdrivers/chromedriver.exe"``

* You can easily view and try the exposed endpoints by browsing the generated swagger documentation here http://localhost:8080/swagger-ui.html

### powertinder-web
* Retrieve frontend dependencies (only before first start)  
``$> powertinder-web> npm install``  
``$> powertinder-web> bower install``

* Start frontend:  
``$> powertinder-web> grunt serve``

* Ensure backend is started then open your browser : http://localhost:9000

* Have fun !

## Available operations
The easiest way to view and try the available operations is to start the **powertinder-api** application and check the swagger
documentation (http://localhost:8080/swagger-ui.html).  

Here is the summary of currently available operations :  

| endpoint                 | description                                                              |
|--------------------------|-------------------------------------------------------------------------|
| /user/me                 | Get information about the currently logged user (you)                   |
| /user/recs               | Get next set of recommendations                                         |
| /user/matches            | Get the list of all your matches                                        |
| /user/like/{userId}      | Like someone giving its user id                                         |
| /user/superlike/{userId} | Super like someone giving its user id                                   |
| /user/pass/{userId}      | Pass someone giving its user id                                         |
| /user/position           | Update your current position by providing wanted latitude and longitude |




