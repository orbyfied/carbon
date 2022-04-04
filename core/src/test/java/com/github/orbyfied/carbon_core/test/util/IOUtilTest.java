package com.github.orbyfied.carbon_core.test.util;

import com.github.orbyfied.carbon.util.IOUtil;
import org.junit.jupiter.api.Test;

public class IOUtilTest {

    @Test
    public void unicodeTest() {

        String s = "\u6969hello\u8888world lol";

        System.out.println(
                IOUtil.escapeUstr(s)
        );

    }

}
