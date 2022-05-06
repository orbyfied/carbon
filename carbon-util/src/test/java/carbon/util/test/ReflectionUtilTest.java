package carbon.util.test;

import net.orbyfied.carbon.util.ReflectionUtil;
import org.junit.jupiter.api.Test;

public class ReflectionUtilTest {

        /* ---- 1 ---- */

    @Test
    public void testWalkParents() {
        ReflectionUtil.walkParents(String.class, null, (d, c) -> {
            System.out.println("@ depth " + d + ": " + "  ".repeat(d) + "|- " + c);
        });
    }

}
