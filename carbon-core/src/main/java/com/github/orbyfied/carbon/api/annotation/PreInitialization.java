package com.github.orbyfied.carbon.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Informational annotation that
 * indicates a certain API feature
 * is available before mod initialization.
 * (at the loading stage)
 */
@Retention(RetentionPolicy.CLASS)
public @interface PreInitialization { }
