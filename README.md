# workshift

How to start the workshift application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/workshift-0.0.1-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`

Description
---

A test project using dropwizard for creating a simple rest api. The most interesting part is adding a user to a shift.

# Sample curl commands

User
---

1. add user - `curl -s --header "Content-Type: application/json" --request POST --data '{"name": "Adam"}' localhost:8080/user`
1. get user by id - `curl -s --header "Content-Type: application/json" localhost:8080/user/{userId}`
1. get all users - `curl -s --header "Content-Type: application/json" localhost:8080/user/all`

Shop
---

1. add shop - `curl -s --header "Content-Type: application/json" --request POST --data '{"name": "Adams shop"}' localhost:8080/shop`
1. get shop by id - `curl -s --header "Content-Type: application/json" localhost:8080/shop/{shopId}`
1. get all shops - `curl -s --header "Content-Type: application/json" localhost:8080/shop/all`
1. get all users for shop - `curl -s --header "Content-Type: application/json" localhost:8080/shop/{userId}/users`

Shift
---

1. add shift (need shop id) - `curl -s --header "Content-Type: application/json" --request POST --data '{"startTS": "2023-09-23T10:00:00.000+02:00", "endTS": "2023-09-23T18:00:00.000+02:00", "shopId": 1}' localhost:8080/shift`
1. add user to shift - `curl -s --header "Content-Type: application/json" --request PUT localhost:8080/shift/{shiftId}/user/{userId}`
1. get shift by id - `curl -s --header "Content-Type: application/json" localhost:8080/shift/{shiftId}`
1. get all shifts - `curl -s --header "Content-Type: application/json" localhost:8080/shift/all`
1. get all users for shift - `curl -s --header "Content-Type: application/json" localhost:8080/shift/{userId}/users`
