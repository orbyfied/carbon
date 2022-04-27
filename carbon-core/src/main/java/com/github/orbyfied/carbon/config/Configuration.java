package com.github.orbyfied.carbon.config;

import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a configuration for a holder
 * that can be saved and loaded. Always
 * related to a {@link Configurable}
 */
public interface Configuration {

    /**
     * Get the holder of this configuration.
     * Aka. the configurable.
     * @return The configurable.
     */
    Configurable<?> getConfigurable();

    /**
     * Called when a configuration has been
     * loaded. Used to, for example, fill
     * additional fields and data.
     * @param config The configuration section.
     */
    default void whenLoaded(ConfigurationSection config) { }

    /**
     * Called when a configuration has been
     * saved. Used to, for example, save
     * additional fields and data.
     * @param config The configuration section.
     */
    default void whenSaved(ConfigurationSection config) { }

    /**
     * Loads the data from the configuration
     * section into this configuration.
     * @param config The configuration section.
     */
    default void load(ConfigurationSection config) {
        try {

            // get class
            Class<?> klass = this.getClass();

            // load fields
            for (Field field : klass.getDeclaredFields()) {

                // set accessible
                field.setAccessible(true);

                // get and check descriptor
                Configure desc = field.getAnnotation(Configure.class);
                if (desc == null) continue;

                // get name
                String name = desc.name();
                if (name.equals("<get>"))
                    name = field.getName();

                // skip loading if it doesnt exist
                // this keeps the defaults which will
                // be saved immediately after
                if (!config.contains(name))
                    continue;

                // get data
                Object o = config.get(name);

                if (o == null) {
                    // load null
                    field.set(this, null);
                } else if (Configurable.class.isAssignableFrom(field.getType())) {
                    // load configuration
                    Configurable c = (Configurable) o;
                    c.getConfiguration().load(config.getConfigurationSection(name));
                } else {
                    // load simple data
                    field.set(this, o);
                }

                // call when loaded
                whenLoaded(config);

            }

        } catch (Exception e) {
            throw new ConfigurationException("failed to load configuration", e);
        }
    }

    /**
     * Saves the data from this configuration
     * into the provided configuration section.
     * @param config The configuration section.
     */
    default void save(ConfigurationSection config) {
        try {

            // get class
            Class<?> klass = this.getClass();

            // load fields
            for (Field field : klass.getDeclaredFields()) {

                // set accessible
                field.setAccessible(true);

                // get and check descriptor
                Configure desc = field.getAnnotation(Configure.class);
                if (desc == null) continue;

                // get comment descriptor
                Comment comment = field.getAnnotation(Comment.class);
                if (comment != null) {
                    List<String> list = new ArrayList<>(config.getComments(""));
                    list.addAll(Arrays.asList(comment.value().split("\n")));
                    config.setComments("", list);
                }

                // get name
                String name = desc.name();
                if (name.equals("<get>"))
                    name = field.getName();

                // get data
                Object o = field.get(this);

                if (o == null) {
                    // save null
                    config.set(name, null);
                } else if (Configurable.class.isAssignableFrom(field.getType())) {
                    // save configuration
                    Configurable c = (Configurable) o;
                    c.getConfiguration().save(config.getConfigurationSection(name));
                } else {
                    // save simple data
                    config.set(name, o);
                }

                // call when loaded
                whenSaved(config);

            }

        } catch (Exception e) {
            throw new ConfigurationException("failed to load configuration", e);
        }
    }

}
