package com.github.orbyfied.carbon.api.mod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to specify an additional
 * description for a mod. Optional.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
/* GET */ @CarbonModDescription("<none>")
public @interface CarbonModDescription {

    /**
     * An instance for fast no-description comparison.
     */
    CarbonModDescription NONE = CarbonModDescription.class.getAnnotation(CarbonModDescription.class);

    /**
     * The description of this mod.
     */
    String value();

}
