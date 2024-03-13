import componenets.enums.HeaderParameter;
import componenets.objects.User;
import org.apache.commons.lang3.RandomStringUtils;

public abstract class BaseTest {

    public User generateRandomUser() {
        User user = new User();
        user.setName(RandomStringUtils.randomAlphabetic(5));
        user.setEmail(RandomStringUtils.randomAlphabetic(6) + "@gmail.com");
        user.setGender("male");
        user.setStatus("inactive");

        return user;
    }
}
