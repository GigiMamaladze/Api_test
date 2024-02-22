package services;

import io.restassured.response.Response;
import objects.User;

import static io.restassured.RestAssured.given;

public class ApiService {

    private final String bearerToken;

    public ApiService(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public User createUser(User user) {
        Response createResponse = given()
                .header("Authorization", bearerToken)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .when()
                .body(user)
                .post("/users");

        createResponse.prettyPrint();
        createResponse.then().statusCode(201);

        User createdUser = createResponse.as(User.class);
        return createdUser;
    }

    public void updateUser(User user) {
        Response putResponse = given()
                .header("Authorization", bearerToken)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .when()
                .body(user)
                .put("/users/" + user.getId());

        putResponse.prettyPrint();
        putResponse.then().statusCode(200);
    }

    public User getUser(Long userId) {
        Response getResponse = given()
                .header("Authorization", bearerToken)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .when()
                .get("/users/" + userId);

        User getUser = getResponse.as(User.class);

        getResponse.prettyPrint();
        getResponse.then().statusCode(200);

        return getUser;
    }

    public void deleteUser(Long userId) {
        Response deleteResponse = given()
                .header("Authorization", bearerToken)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .when()
                .delete("/users/" + userId);

        deleteResponse.prettyPrint();
        deleteResponse.then().statusCode(204);
    }
}