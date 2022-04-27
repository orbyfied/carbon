package com.github.orbyfied.carbon.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes a field as being a
 * part of a configuration.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Configure {

    /**
     * The name of the configuration
     * setting. The default makes the
     * field name the setting name.
     * @return The name.
     */
    String name() default "<get>";

}
