package com.github.orbyfied.carbon_core.test.config;

import com.github.orbyfied.carbon.config.AbstractConfiguration;
import com.github.orbyfied.carbon.config.Configurable;
import com.github.orbyfied.carbon.config.Configuration;
import com.github.orbyfied.carbon.config.Configure;
import org.junit.jupiter.api.Test;

public class SimpleConfigurableTest {

    /* ---- 1 ---- */

    static class MyConfigurable implements Configurable {

        private static class TheMyConfig extends AbstractConfiguration {

            @Configure
            public int myInt;

            @Configure
            public float f;

            @Configure
            public TheMyConfig embeddedConfig;

            public TheMyConfig(Configurable configurable) {
                super(configurable);
            }

        }

        @Override
        public String getConfigurationPath() {
            return "my.config";
        }

        private TheMyConfig cfg;

        @Override
        public Configuration getConfiguration() {
            return cfg;
        }

    }

    @Test
    public void test1() {

    }

}
