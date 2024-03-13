import io.restassured.response.Response;
import objects.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import services.GraphQLService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import static io.restassured.RestAssured.given;

public class GraphQLTests extends BaseTest {

    private final List<Long> userIds = new ArrayList<>();
    private final String ENDPOINT = "https://gorest.co.in/public/v2/graphql";
    private final GraphQLService graphQLService = new GraphQLService(BEARER_TOKEN, ENDPOINT);
    private final String REQUEST_BODY_DIRECTORY = "src/test/resources/graphql/";

    @AfterMethod(groups = "created", alwaysRun = true)
    public void deleteCreatedUser() {
        userIds.forEach(graphQLService::deleteUser);
        userIds.clear();
    }

    private String readRequestBodyFromFile(String fileName) throws IOException {
        String filePath = REQUEST_BODY_DIRECTORY + fileName;
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    private Response sendRequestWithBody(String requestBody) {
        return given()
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", CONTENT_TYPE)
                .header("Connection", CONNECTION)
                .body(requestBody)
                .when()
                .post(ENDPOINT);
    }

    private String replaceDynamicParameters(String requestBody, User user) {
        return requestBody.replace("{{userId}}", String.valueOf(user.getId()))
                .replace("{{userName}}", user.getName())
                .replace("{{userEmail}}", user.getEmail())
                .replace("{{userGender}}", user.getGender())
                .replace("{{userStatus}}", user.getStatus());
    }

    // =============================== Tests ==================================================

    @Test
    public void getUsersQueryTest() throws IOException {
        String requestBody = readRequestBodyFromFile("get_users_query.json");
        Response response = sendRequestWithBody(requestBody);
        response.prettyPrint();
        response.then().statusCode(200);
        List<User> userList = response.jsonPath().getList("data.users.nodes", User.class);
        Assert.assertFalse(userList.isEmpty(), "User list should not be empty !");
    }

    @Test(groups = "created")
    public void getUserByIdQueryTest() throws IOException {
        User user = generateRandomUser();
        user = graphQLService.createUser(user);
        userIds.add(user.getId());
        String requestBody = readRequestBodyFromFile("get_user_by_id_query.json");
        requestBody = replaceDynamicParameters(requestBody, user);
        Response response = sendRequestWithBody(requestBody);
        User getUser = response.jsonPath().getObject("data.user", User.class);
        response.prettyPrint();
        response.then().statusCode(200);
        Assert.assertEquals(getUser, user, "User is not as expected !");
    }

    @Test(groups = "created")
    public void createUserMutationTest() throws IOException {
        User user = new User();
        user.setName(RandomStringUtils.randomAlphabetic(5));
        user.setEmail(RandomStringUtils.randomAlphabetic(6) + "@Gmgmail.com");
        user.setGender("male");
        user.setStatus("inactive");
        String requestBody = readRequestBodyFromFile("create_user_mutation.json");
        requestBody = replaceDynamicParameters(requestBody, user);
        Response response = sendRequestWithBody(requestBody);
        response.prettyPrint();
        response.then().statusCode(200);
        user = response.jsonPath().getObject("data.createUser.user", User.class);
        User createdUser = graphQLService.getUser(user.getId());
        userIds.add(user.getId());
        Assert.assertEquals(createdUser, user, "User is not created !");
    }

    @Test(groups = "created")
    public void updateUserTest() throws IOException {
        User user = generateRandomUser();
        user = graphQLService.createUser(user);
        userIds.add(user.getId());
        String initialUserName = user.getName();
        User updatedUser = user;
        updatedUser.setName("UpdatedName");
        String requestBody = readRequestBodyFromFile("update_user_mutation.json");
        requestBody = replaceDynamicParameters(requestBody, updatedUser);
        Response putResponse = sendRequestWithBody(requestBody);
        putResponse.prettyPrint();
        putResponse.then().statusCode(200);
        User getUser = graphQLService.getUser(updatedUser.getId());
        Assert.assertNotEquals(getUser.getName(), initialUserName, "User name should not be equal to initial created user name !");
        Assert.assertEquals(getUser, updatedUser, "User is not updated !");
    }

    @Test
    public void deleteUserMutationTest() throws IOException {
        User user = generateRandomUser();
        user = graphQLService.createUser(user);
        Response response = given()
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", CONTENT_TYPE)
                .header("Connection", CONNECTION)
                .body(readRequestBodyFromFile("delete_user_mutation.json"))
                .when()
                .post(ENDPOINT);
        response.prettyPrint();
        response.then().statusCode(200);
        Response getResponse = given()
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", CONTENT_TYPE)
                .header("Connection", CONNECTION)
                .body(readRequestBodyFromFile("get_user_by_id_query.json"))
                .when()
                .post(ENDPOINT);
        getResponse.prettyPrint();
        String errorMessage = getResponse.jsonPath().getString("errors[0].message");
        // Verifying the error message
        Assert.assertEquals(errorMessage, "Resource not found!", "Error message mismatch");
    }
}
