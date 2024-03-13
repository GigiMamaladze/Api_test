import objects.User;
import org.apache.commons.lang3.RandomStringUtils;

public abstract class BaseTest {

    protected final String BEARER_TOKEN = "Bearer 17a915d98bc061595c53aa898006e9e63a8d5935bfe8d316b0399ebbd31af775";

    public User generateRandomUser() {
        User user = new User();
        user.setName(RandomStringUtils.randomAlphabetic(5));
        user.setEmail(RandomStringUtils.randomAlphabetic(6) + "@gmail.com");
        user.setGender("male");
        user.setStatus("inactive");

        return user;
    }
}
