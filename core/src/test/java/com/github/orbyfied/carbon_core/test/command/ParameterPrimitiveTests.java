package com.github.orbyfied.carbon_core.test.command;

import com.github.orbyfied.carbon.command.ParameterType;
import com.github.orbyfied.carbon.command.impl.SystemParameterType;
import com.github.orbyfied.carbon.util.StringReader;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class ParameterPrimitiveTests {

        /* ---- 1 ---- */

    @Test
    public void testParameterTypesPrimitive() {

        final String myString = "a \"bob sus\" 0b1011001101 (1, 2, 3) [(1, 2, 3), (4, 5, 6), (7, 8, 9)]";

        final ParameterType<?>[] types = new ParameterType[] {
                SystemParameterType.CHAR,
                SystemParameterType.STRING,
                SystemParameterType.INT,
                SystemParameterType.VEC_3_INT,
                SystemParameterType.listOf(SystemParameterType.VEC_3_INT)
        };

        /* code */

        List<Object> results = new ArrayList<>();

        final StringReader reader = new StringReader(myString, 0);
        for (ParameterType<?> type : types) {
            results.add(type.parse(null,  reader));
            reader.next();

            System.out.println(results + ", idx: " + reader.index() + " = '" + reader.current() + "'");
        }

        System.out.println("results: " + results);

    }

}
