package services;

import io.restassured.response.Response;
import objects.User;
import org.testng.Assert;

import static io.restassured.RestAssured.given;

public class GraphQLService {

    private final String BEARER_TOKEN;

    private final String ENDPOINT;

    public GraphQLService(String BEARER_TOKEN, String ENDPOINT) {
        this.BEARER_TOKEN = BEARER_TOKEN;
        this.ENDPOINT = ENDPOINT;
    }

    public User getUser(Long userId) {
        Response response = given()
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .when()
                .body("{\n" +
                        "  \"query\": \"query User { user(id: \\\"" + userId + "\\\") { email gender id name status }}\"\n" +
                        "}")
                .post(ENDPOINT);

        User getUser = response.jsonPath().getObject("data.user", User.class);

        response.prettyPrint();
        response.then().statusCode(200);

        return getUser;
    }

    public User createUser(User user) {
        Response response = given()
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .body("{\n" +
                        "  \"query\": \"mutation CreateUser { createUser( input: { name: \\\"" + user.getName() + "\\\", email: \\\"" + user.getEmail() + "\\\", " +
                        "gender: \\\"" + user.getGender() + "\\\", status: \\\"" + user.getStatus() + "\\\" } ) { user { email gender id name status } }}\"\n" +
                        "}")
                .when()
                .post(ENDPOINT);

        response.prettyPrint();
        response.then().statusCode(200);

        return response.jsonPath().getObject("data.createUser.user", User.class);
    }

    public void deleteUser(Long userId) {
        Response response = given()
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .body("{\n" +
                        "  \"query\": \"mutation DeleteUser { deleteUser(input: { id: \\" + userId + "\\ }) { user { id email gender name status } }}\"\n" +
                        "}")
                .when()
                .post(ENDPOINT);

        response.prettyPrint();
        response.then().statusCode(200);

        Response getResponse = given()
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .when()
                .body("{\n" +
                        "  \"query\": \"query User { user(id: \\" + userId + "\\) { email gender id name status }}\"\n" +
                        "}")
                .post(ENDPOINT);

        getResponse.prettyPrint();
        String errorMessage = getResponse.jsonPath().getString("errors[0].message");

        // Verifying the error message
        Assert.assertEquals(errorMessage, "Resource not found!", "Error message mismatch");
    }
}