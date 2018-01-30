package eu.europeanstudentcard.esc;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EscnFactoryTest {
    @Test
    public void getUuid() throws Exception {
        String uuidFormat = "(\\w{8}(-\\w{4}){3}-\\w{12}?)";
        Pattern p = Pattern.compile(uuidFormat);
        String returnTest;

        returnTest = EscnFactory.getEscn(66, "999859608");

        Matcher m = p.matcher(returnTest);
        assertTrue(m.matches());
        assertEquals("066999859608", returnTest.substring(returnTest.length() - 12));
    }

    @Test(expected = EscnFactoryException.class)
    public void testIllegalArgumentException() throws Exception {
        EscnFactory.getEscn(6666, "999859608");
    }
}