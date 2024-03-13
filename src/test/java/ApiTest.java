import componenets.enums.HeaderParameter;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import componenets.objects.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import services.ApiService;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class ApiTest extends BaseTest {

    private final List<Long> userIds = new ArrayList<>();

    private final ApiService apiService = new ApiService();

    @BeforeTest
    public static void init() {
        RestAssured.baseURI = "https://gorest.co.in";
        RestAssured.basePath = "/public/v2";
    }

    @AfterMethod(groups = "created", alwaysRun = true)
    public void deleteCreatedUser() {
        userIds.forEach(apiService::deleteUser);
        userIds.clear();
    }


    // ========================================== Tests =====================================================

    @Test
    public void getAllUsersTest() {
        Response response = given()
                .when()
                .get("/users");
        response.prettyPrint();
        response.then().statusCode(200);

        List<User> userList = response.jsonPath().getList(".", User.class);

        Assert.assertFalse(userList.isEmpty(), "User list should not be empty !");
    }

    @Test(groups = "created")
    public void getUserByIdTest() {
        User createdUser = generateRandomUser();
        createdUser = apiService.createUser(createdUser);
        userIds.add(createdUser.getId());

        Response getResponse = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .get("/users/" + createdUser.getId());

        getResponse.prettyPrint();
        getResponse.then().statusCode(200);

        User getUser = getResponse.as(User.class);

        Assert.assertEquals(getUser, createdUser, "User is not as excepted !");
    }


    @Test(groups = "created")
    public void createUserTest() {
        User user = new User();
        user.setName(RandomStringUtils.randomAlphabetic(5));
        user.setEmail(RandomStringUtils.randomAlphabetic(6) + "@Gmgmail.com");
        user.setGender("male");
        user.setStatus("inactive");
        Response response = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .body(user)
                .post("/users");

        response.prettyPrint();
        response.then().statusCode(201);

        User createdUser = response.as(User.class);
        userIds.add(createdUser.getId());

        User getUser = apiService.getUser(createdUser.getId());

        Assert.assertEquals(getUser, createdUser, "User is not created !");
    }

    @Test(groups = "created")
    public void updateUserTest() {
        User user = generateRandomUser();
        user = apiService.createUser(user);
        userIds.add(user.getId());

        String initialUserName = user.getName();

        User updatedUser = user;
        updatedUser.setName("UpdatedName");

        Response putResponse = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .body(updatedUser)
                .put("/users/" + user.getId());

        putResponse.prettyPrint();
        putResponse.then().statusCode(200);

        User getUser = apiService.getUser(updatedUser.getId());

        Assert.assertNotEquals(getUser.getName(), initialUserName, "User name should not be equal of initial created user name !");
        Assert.assertEquals(getUser, updatedUser, "User is not updated !");
    }

    @Test
    public void deleteUserTest() {
        User createdUser = generateRandomUser();
        createdUser = apiService.createUser(createdUser);

        Response deleteResponse = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .delete("/users/" + createdUser.getId());

        deleteResponse.prettyPrint();
        deleteResponse.then().statusCode(204);

        Response getResponse = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .get("/users/" + createdUser.getId());

        getResponse.prettyPrint();
        getResponse.then().statusCode(404);
    }

    @Test
    public void getUserWithInvalidIdTest() {
        Response getResponse = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .get("/users/" + RandomStringUtils.randomNumeric(100));

        getResponse.prettyPrint();
        getResponse.then().statusCode(404);
    }

    @Test
    public void deleteUserWithInvalidUserIdTest() {
        Response getResponse = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .delete("/users/" + RandomStringUtils.randomNumeric(100));

        getResponse.prettyPrint();
        getResponse.then().statusCode(404);
    }

    @Test
    public void updateUserWithInvalidIdTest() {
        User user = generateRandomUser();
        user = apiService.createUser(user);
        userIds.add(user.getId());

        User updatedUser = user;
        updatedUser.setName("UpdatedName");

        Response putResponse = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .body(updatedUser)
                .put("/users/" + RandomStringUtils.randomNumeric(100));

        putResponse.prettyPrint();
        putResponse.then().statusCode(404);
    }

    @Test
    public void createUserWithMissingFieldsTest() {
        User user = new User();
        Response response = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .body(user)
                .post("/users");

        response.prettyPrint();
        response.then().statusCode(422);
    }

    @Test
    public void createUserWithInvalidEmailFormatTest() {
        User user = new User();
        user.setName(RandomStringUtils.randomAlphabetic(5));
        user.setEmail("123456");
        user.setGender("male");
        user.setStatus("inactive");

        Response response = given()
                .header("Authorization", HeaderParameter.AUTHORIZATION.getParameter())
                .header("Content-Type", HeaderParameter.CONTENT_TYPE.getParameter())
                .header("Connection", HeaderParameter.CONNECTION.getParameter())
                .when()
                .body(user)
                .post("/users");

        response.prettyPrint();
        response.then().statusCode(422);
    }
}