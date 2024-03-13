package services;

import componenets.enums.HeaderParameter;
import io.restassured.response.Response;
import componenets.objects.User;

import static io.restassured.RestAssured.given;

public class ApiService {

    public User createUser(User user) {
        Response createResponse = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .body(user)
                .post("/users");

        createResponse.prettyPrint();
        createResponse.then().statusCode(201);

        return createResponse.as(User.class);
    }

    public User getUser(Long userId) {
        Response getResponse = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .get("/users/" + userId);

        User getUser = getResponse.as(User.class);

        getResponse.prettyPrint();
        getResponse.then().statusCode(200);

        return getUser;
    }

    public void deleteUser(Long userId) {
        Response deleteResponse = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .delete("/users/" + userId);

        deleteResponse.prettyPrint();
        deleteResponse.then().statusCode(204);
    }
}