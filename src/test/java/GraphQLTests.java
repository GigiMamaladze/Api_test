import io.restassured.response.Response;
import objects.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import services.GraphQLService;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class GraphQLTests extends BaseTest {

    private final List<Long> userIds = new ArrayList<>();

    private final String ENDPOINT = "https://gorest.co.in/public/v2/graphql";

    private final GraphQLService graphQLService = new GraphQLService(BEARER_TOKEN, ENDPOINT);


    @AfterMethod(groups = "created", alwaysRun = true)
    public void deleteCreatedUser() {
        userIds.forEach(graphQLService::deleteUser);
        userIds.clear();
    }

    // =============================== Tests ==================================================

    @Test
    public void getUsersQueryTest() {
        Response response = given()
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .body("{\n" +
                        "  \"query\": \"query Users { users { nodes { id email gender name status } }}\"\n" +
                        "}")
                .when()
                .post(ENDPOINT);

        response.prettyPrint();
        response.then().statusCode(200);

        List<User> userList = response.jsonPath().getList("data.users.nodes", User.class);

        Assert.assertFalse(userList.isEmpty(), "User list should not be empty !");
    }


    @Test(groups = "created")
    public void getUserByIdQueryTest() {
        User user = generateRandomUser();
        user = graphQLService.createUser(user);
        userIds.add(user.getId());

        Response response = given()
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .when()
                .body("{\n" +
                        "  \"query\": \"query User { user(id: \\\""+ user.getId() +"\\\") { id email gender name status }}\"\n" +
                        "}")
                .post(ENDPOINT);

        User getUser = response.jsonPath().getObject("data.user", User.class);;

        response.prettyPrint();
        response.then().statusCode(200);

        Assert.assertEquals(getUser, user, "User is not as excepted !");
    }

    @Test(groups = "created")
    public void createUserMutationTest() {
        User user = new User();
        user.setName(RandomStringUtils.randomAlphabetic(5));
        user.setEmail(RandomStringUtils.randomAlphabetic(6) + "@Gmgmail.com");
        user.setGender("male");
        user.setStatus("inactive");
        Response response = given()
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .body("{\n" +
                        "  \"query\": \"mutation CreateUser { createUser( input: { name: \\\"" + user.getName() + "\\\", email: \\\"" + user.getEmail() + "\\\", " +
                        "gender: \\\"" + user.getGender() + "\\\", status: \\\"" + user.getStatus() + "\\\" } ) { user { id email gender name status } }}\"\n" +
                        "}")
                .when()
                .post(ENDPOINT);

        response.prettyPrint();
        response.then().statusCode(200);

        user = response.jsonPath().getObject("data.createUser.user", User.class);
        User createdUser = graphQLService.getUser(user.getId());
        userIds.add(user.getId());

        Assert.assertEquals(createdUser, user, "User is not created !");
    }

    @Test(groups = "created")
    public void updateUserTest() {
        User user = generateRandomUser();
        user = graphQLService.createUser(user);
        userIds.add(user.getId());

        String initialUserName = user.getName();

        User updatedUser = user;
        updatedUser.setName("UpdatedName");

        Response putResponse = given()
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .when()
                .body("{\n" +
                        "  \"query\": \"mutation UpdateUser { updateUser(input: { name: \\\""+ updatedUser.getName() + "\\\", " +
                        "id: \\"+ user.getId() + "\\ }) " +
                        "{ user { email gender id name status } }}\"\n" +
                        "}")
                .post(ENDPOINT);

        putResponse.prettyPrint();
        putResponse.then().statusCode(200);

        User getUser = graphQLService.getUser(updatedUser.getId());

        Assert.assertNotEquals(getUser.getName(), initialUserName, "User name should not be equal of initial created user name !");
        Assert.assertEquals(getUser, updatedUser, "User is not updated !");
    }


    @Test
    public void deleteUserMutationTest() {
        User user = generateRandomUser();
        user = graphQLService.createUser(user);

        Response response = given()
                .header("Authorization", BEARER_TOKEN)
                .header("Content-Type", "application/json")
                .header("Connection", "keep-alive")
                .body("{\n" +
                        "  \"query\": \"mutation DeleteUser { deleteUser(input: { id: \\" + user.getId() + "\\ }) { user { id email gender name status } }}\"\n" +
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
                        "  \"query\": \"query User { user(id: \\"+ user.getId() +"\\) { email gender id name status }}\"\n" +
                        "}")
                .post(ENDPOINT);

        getResponse.prettyPrint();
        String errorMessage = getResponse.jsonPath().getString("errors[0].message");

        // Verifying the error message
        Assert.assertEquals(errorMessage, "Resource not found!", "Error message mismatch");
    }
}