package services;

import componenets.enums.HeaderParameter;
import componenets.objects.User;
import io.restassured.response.Response;
import org.testng.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class GraphQLService {

    private final String ENDPOINT;
    private final String REQUEST_BODY_DIRECTORY = "src/test/resources/graphql/";

    public GraphQLService(String ENDPOINT) {
        this.ENDPOINT = ENDPOINT;
    }

    private String readRequestBodyFromFile(String fileName) throws IOException {
        String filePath = REQUEST_BODY_DIRECTORY + fileName;
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    private Response sendRequestWithBody(String requestBody) {
        return given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .body(requestBody)
                .when()
                .post(ENDPOINT);
    }

    private String replaceDynamicParameters(String requestBody, Long userId) {
        return requestBody.replace("{{userId}}", String.valueOf(userId));
    }

    private String replaceDynamicParameters(String requestBody, User user) {
        return requestBody.replace("{{userId}}", String.valueOf(user.getId()))
                .replace("{{userName}}", user.getName())
                .replace("{{userEmail}}", user.getEmail())
                .replace("{{userGender}}", user.getGender())
                .replace("{{userStatus}}", user.getStatus());
    }

    //===================================== Services =======================================

    public User getUser(Long userId) throws IOException {
        String requestBody = readRequestBodyFromFile("get_user_by_id_query.json");
        requestBody = replaceDynamicParameters(requestBody, userId);
        Response response = sendRequestWithBody(requestBody);
        User getUser = response.jsonPath().getObject("data.user", User.class);
        response.prettyPrint();
        response.then().statusCode(200);
        return getUser;
    }

    public User createUser(User user) throws IOException {
        String requestBody = readRequestBodyFromFile("create_user_mutation.json");
        requestBody = replaceDynamicParameters(requestBody, user);
        Response response = sendRequestWithBody(requestBody);
        response.prettyPrint();
        response.then().statusCode(200);
        return response.jsonPath().getObject("data.createUser.user", User.class);
    }

    public void deleteUser(Long userId) throws IOException {
        String requestBody = readRequestBodyFromFile("delete_user_mutation.json");
        requestBody = replaceDynamicParameters(requestBody, userId);
        Response response = sendRequestWithBody(requestBody);
        response.prettyPrint();
        response.then().statusCode(200);
        Response getResponse = sendRequestWithBody(readRequestBodyFromFile("get_user_by_id_query.json"));
        getResponse.prettyPrint();
        String errorMessage = getResponse.jsonPath().getString("errors[0].message");
        // Verifying the error message
        Assert.assertEquals(errorMessage, "Resource not found!", "Error message mismatch");
    }
}