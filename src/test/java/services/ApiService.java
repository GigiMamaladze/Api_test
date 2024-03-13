package services;

import componenets.enums.HeaderParameter;
import io.restassured.response.Response;
import componenets.objects.User;

import static io.restassured.RestAssured.given;

public class ApiService {

    private final String BEARER_TOKEN = "Bearer 17a915d98bc061595c53aa898006e9e63a8d5935bfe8d316b0399ebbd31af775";

    private final String CONTENT_TYPE = "application/json";

    private final String CONNECTION = "keep-alive";

    public User createUser(User user) {
        Response createResponse = given()
                .header(HeaderParameter.AUTHORIZATION.getParameter(), BEARER_TOKEN)
                .header(HeaderParameter.CONTENT_TYPE.getParameter(), CONTENT_TYPE)
                .header(HeaderParameter.CONNECTION.getParameter(), CONNECTION)
                .when()
                .body(user)
                .post("/users");

        createResponse.prettyPrint();
        createResponse.then().statusCode(201);

        return createResponse.as(User.class);
    }

    public User getUser(Long userId) {
        Response getResponse = given()
                .header(HeaderParameter.AUTHORIZATION.getParameter(), BEARER_TOKEN)
                .header(HeaderParameter.CONTENT_TYPE.getParameter(), CONTENT_TYPE)
                .header(HeaderParameter.CONNECTION.getParameter(), CONNECTION)
                .when()
                .get("/users/" + userId);

        User getUser = getResponse.as(User.class);

        getResponse.prettyPrint();
        getResponse.then().statusCode(200);

        return getUser;
    }

    public void deleteUser(Long userId) {
        Response deleteResponse = given()
                .header(HeaderParameter.AUTHORIZATION.getParameter(), BEARER_TOKEN)
                .header(HeaderParameter.CONTENT_TYPE.getParameter(), CONTENT_TYPE)
                .header(HeaderParameter.CONNECTION.getParameter(), CONNECTION)
                .when()
                .delete("/users/" + userId);

        deleteResponse.prettyPrint();
        deleteResponse.then().statusCode(204);
    }
}