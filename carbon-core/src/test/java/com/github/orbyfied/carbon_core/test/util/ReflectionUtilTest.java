package com.github.orbyfied.carbon_core.test.util;

import com.github.orbyfied.carbon.core.mod.CarbonJavaModAPI;
import com.github.orbyfied.carbon.util.ReflectionUtil;
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
