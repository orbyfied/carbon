package net.orbyfied.carboncoretest.util;

import net.orbyfied.carbon.util.security.Access;
import net.orbyfied.carbon.util.security.AccessValidator;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SecurityUtilTest {

    private static final AccessValidator av1 = (elements, accessCaller) -> {
        System.out.println(Arrays.toString(elements));
        System.out.println("CALLER: " + accessCaller);
        return elements[0].getClassName().startsWith("com.");
    };

    @Test
    public void testAccessValidation() {

        assertEquals(true, av1.test(0));
        assertEquals(true, Access.checkAccess(0, av1));

        av1.require(0);
        Access.assertAccess(0, av1);

    }

}
