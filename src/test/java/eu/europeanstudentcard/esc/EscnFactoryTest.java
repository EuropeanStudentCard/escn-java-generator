package eu.europeanstudentcard.esc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EscnFactoryTest {

    private static final String UUID_PATTERN = "(\\w{8}(-\\w{4}){3}-\\w{12}?)";

    @Test
    public void getEscnShouldReturnValidUUIDWhenPrefixAndPicAreValid() throws Exception {
        Pattern uuidPattern = Pattern.compile(UUID_PATTERN);

        // Obtain the UUID from the factory method
        String result = EscnFactory.getEscn(66, "999859608");

        // Match the result against the UUID pattern
        Matcher matcher = uuidPattern.matcher(result);

        // Grouped assertions for better feedback
        Assertions.assertAll("UUID and Suffix Validation",
                // Validate the UUID format
                () -> Assertions.assertTrue(matcher.matches(), "The result should match the UUID pattern."),
                // Validate the suffix
                () -> Assertions.assertEquals("066999859608", result.substring(result.length() - 12), "The suffix should match the expected value.")
        );
    }

    @Test
    public void getEscnShouldThrowAnEscnExceptionWhenPrefixIsInvalid() throws Exception {
        Assertions.assertThrows(EscnFactoryException.class, () -> EscnFactory.getEscn(6666, "999859608"));
    }

    @Test
    public void getEscnShouldThrowAnEscnExceptionWhenPicIsInvalid() throws Exception {
        Assertions.assertThrows(EscnFactoryException.class, () -> EscnFactory.getEscn(666, "InvalidPicOfMoreThan9Chars"));
    }

}