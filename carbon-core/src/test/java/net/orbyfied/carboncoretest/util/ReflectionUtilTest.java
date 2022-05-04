package net.orbyfied.carboncoretest.util;

import net.orbyfied.carbon.core.mod.CarbonJavaModAPI;
import net.orbyfied.carbon.util.ReflectionUtil;
import org.junit.jupiter.api.Test;

public class ReflectionUtilTest {

        /* ---- 1 ---- */

    @Test
    public void testWalkParents() {
        ReflectionUtil.walkParents(CarbonJavaModAPI.class, null, (d, c) -> {
            System.out.println("@ depth " + d + ": " + "  ".repeat(d) + "|- " + c);
        });
    }

}
