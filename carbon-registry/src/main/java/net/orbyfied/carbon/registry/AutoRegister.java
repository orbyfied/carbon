package net.orbyfied.carbon.registry;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface AutoRegister {

    /**
     * If it should be auto registered.
     */
    boolean allow();

    /**
     * If it should be treated as an all-auto registrable
     * collection class/instance.
     */
    boolean all() default false;

    /**
     * If it should ignore the instance when registering all.
     */
    boolean allStatic() default false;

}
