package com.github.orbyfied.carbon.config;

import com.github.orbyfied.carbon.util.ReflectionUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Helper class for managing configurations
 * and configurables. Includes a lot of boilerplate.
 */
public class ConfigurationHelper<S extends ConfigurationSection> {

    /**
     * All configurables linearly.
     */
    protected final List<Configurable<?>> configurables = new ArrayList<>();

    /**
     * All configurables mapped by class.
     */
    protected final Map<Class<?>, Configurable<?>> configurablesByClass = new HashMap<>();

    /**
     * The children of this helper.
     */
    protected final HashMap<String, ConfigurationHelper<ConfigurationSection>> children = new HashMap<>();

    /**
     * The configuration section to
     * load from.
     */
    protected S section;

    /**
     * Runs before loading.
     */
    protected Consumer<S> beforeLoad;

    /**
     * Runs after saving.
     */
    protected Consumer<S> afterSave;

    /**
     * Get all configurables.
     * @return An unmodifiable list.
     */
    public List<Configurable<?>> getConfigurables() {
        return Collections.unmodifiableList(configurables);
    }

    /**
     * Get all configurables by class.
     * @return An unmodifiable map.
     */
    public Map<Class<?>, Configurable<?>> getConfigurablesByClass() {
        return Collections.unmodifiableMap(configurablesByClass);
    }

    /**
     * Get the children of this configuration
     * helper.
     * @return The children mapped by name.
     */
    public Map<String, ConfigurationHelper<ConfigurationSection>> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    /**
     * Get a configurable by class.
     * @param klass The class.
     * @param <T> The configurable type.
     * @return The configurable.
     */
    @SuppressWarnings("unchecked")
    public <T extends Configurable<?>> T getConfigurable(Class<T> klass) {
        return (T) configurablesByClass.get(klass);
    }

    /**
     * Add a configurable.
     * Registers it to the list and
     * maps it by class.
     * @param c The configurable.
     * @return This.
     */
    public ConfigurationHelper<S> addConfigurable(Configurable<?> c) {
        configurables.add(c);
        configurablesByClass.put(c.getClass(), c);
        return this;
    }

    /**
     * Remove a configurable by instance.
     * @param c The configurable.
     * @return This.
     */
    public ConfigurationHelper<S> removeConfigurable(Configurable<?> c) {
        configurables.remove(c);
        configurablesByClass.remove(c.getClass());
        return this;
    }

    /**
     * Remove a configurable by class.
     * @param c The class.
     * @return This.
     */
    public ConfigurationHelper<S> removeConfigurable(Class<?> c) {
        return removeConfigurable(configurablesByClass.get(c));
    }

    /* Getters and Setters. */

    public ConfigurationHelper<S> section(S section) {
        this.section = section;
        return this;
    }

    public ConfigurationHelper<S> afterSave(Consumer<S> consumer) {
        this.afterSave = consumer;
        return this;
    }

    public ConfigurationHelper<S> beforeLoad(Consumer<S> consumer) {
        this.beforeLoad = consumer;
        return this;
    }

    /* Children */

    @SuppressWarnings("unchecked")
    public <C extends ConfigurationSection> ConfigurationHelper<S> child(String key,
                                                                         Supplier<ConfigurationHelper<C>> supplier,
                                                                         Consumer<ConfigurationHelper<C>> consumer) {
        ConfigurationHelper<C> helper = (ConfigurationHelper<C>) createChildren(key, supplier);
        if (consumer != null)
            consumer.accept(helper);
        children.put(key, (ConfigurationHelper<ConfigurationSection>) helper);
        return this;
    }

    public <C extends ConfigurationSection> ConfigurationHelper<S> child(String key,
                                                                         Supplier<ConfigurationHelper<C>> supplier) {
        return child(key, supplier, null);
    }

    public ConfigurationHelper<S> unchild(String key) {
        children.remove(key);
        return this;
    }

    /**
     * Saves all configurables to a configuration section.
     * @return This.
     */
    public ConfigurationHelper<S> save() {

        for (Configurable<?> c : configurables) {
            // path split
            String[] ps = c.getConfigurationPath().split("/");
            // parse path
            ConfigurationHelper<?> helper;
            String path;
            if (ps.length == 1) {
                // this is root
                helper = getChild(null);
                path = ps[0];
            } else {
                // child is root
                helper = getChild(ps[0]);
                path = ps[1];
            }

            c.getConfiguration().save(getOrCreateSection(helper.section, path));
        }

        // save all children
        for (var ch : children.values())
            ch.save();

        if (afterSave != null)
            afterSave.accept(section);

        return this;
    }

    /**
     * Loads one configurable from the
     * configuration section.
     * @return This.
     */
    public ConfigurationHelper<S> loadOne(Configurable<?> c) {
        // path split
        String[] ps = c.getConfigurationPath().split("/");
        // parse path
        ConfigurationHelper<?> helper;
        String path;
        if (ps.length == 1) {
            // this is root
            helper = getChild(null);
            path = ps[0];
        } else {
            // child is root
            helper = getChild(ps[0]);
            path = ps[1];
        }

        // actually load
        c.getConfiguration().load(getOrCreateSection(helper.section, path));

        // return
        return this;
    }

    /**
     * Loads all configurables from the
     * configuration section.
     * @return This.
     */
    public ConfigurationHelper<S> load() {
        if (beforeLoad != null)
            beforeLoad.accept(section);

        // load all children
        for (var ch : children.values())
            ch.load();

        for (Configurable<?> c : configurables)
            loadOne(c);

        return this;
    }

    public ConfigurationHelper<?> getChild(String s) {
        if (s == null) return this;
        // walk path
        String[] c = s.split("\\.");
        ConfigurationHelper<?> curr = this;
        for (String k : c)
            curr = curr.children.get(k);
        return curr;
    }

    @SuppressWarnings("unchecked")
    public <C extends ConfigurationSection> ConfigurationHelper<?> createChildren(String s,
                                                Supplier<ConfigurationHelper<C>> supplier) {
        // walk path
        String[] c = s.split("\\.");
        ConfigurationHelper<?> curr = this;
        ConfigurationHelper<?> prev;
        for (int i = 0; i < c.length - 1; i++) {
            String k = c[i];
            prev = curr;
            curr = prev.children.get(k);
            if (curr == null) {
                curr = new ConfigurationHelper<>();
                prev.children.put(k, (ConfigurationHelper<ConfigurationSection>) curr);
            }
        }

        ConfigurationHelper<C> helper = supplier.get();
        curr.children.put(c[c.length - 1], (ConfigurationHelper<ConfigurationSection>) helper);
        return helper;
    }

    public ConfigurationHelper<S> applyTemplate(Consumer<ConfigurationHelper<? extends S>> consumer) {
        consumer.accept(this);
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ConfigurationHelper.class.getSimpleName() + "[", "]")
                .add("configurables=" + configurables)
                .add("children=" + children)
                .add("section=" + section)
                .toString();
    }

    ///////////////////////////////////////////////////////

    public static Consumer<ConfigurationHelper<YamlConfiguration>> bukkitYamlConfiguration(Path file,
                                                                                           String defaults) {
        final Class<?> resolver = ReflectionUtil.getCallerClass(1);
        return helper -> {
            helper.section(new YamlConfiguration())
                    .beforeLoad(sect -> {
                        if (defaults != null)
                            saveDefaults(resolver, defaults, file, false);
                        loadFromFile(sect, file);
                    }).afterSave(sect -> {
                        saveToFile(sect, file);
                    });
        };
    }

    public static ConfigurationSection getOrCreateSection(ConfigurationSection cfg,
                                                          String path) {
        ConfigurationSection section;
        if ((section = cfg.getConfigurationSection(path)) != null)
            return section;
        return cfg.createSection(path);
    }

    public static boolean saveDefaults(Class<?> resolver,
                                    String resource,
                                    Path to,
                                    boolean overwrite) {
        try {
            if (!Files.exists(to)) {
                Files.createDirectories(to.getParent());
                Files.createFile(to);
            } else if (!overwrite) return false;

            InputStream resIn = resolver.getResourceAsStream(resource);
            OutputStream fOut = Files.newOutputStream(to);
            if (resIn == null)
                throw new IllegalArgumentException("resource '" + resource + "' not found");
            resIn.transferTo(fOut);
            resIn.close();
            fOut.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void saveToFile(FileConfiguration cfg,
                                  Path file) {
        try {
            if (!Files.exists(file)) {
                Files.createDirectories(file.getParent());
                Files.createFile(file);
            }
            cfg.save(file.toFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadFromFile(FileConfiguration cfg,
                                    Path file) {
        try {
            if (Files.exists(file)) {
                cfg.load(file.toFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConfigurationHelper<YamlConfiguration> newYamlFileConfiguration(Path file,
                                                                                  String defaults) {
        final Class<?> resolver = ReflectionUtil.getCallerClass(1);
        ConfigurationHelper<YamlConfiguration> helper = new ConfigurationHelper<>();
        helper.section(new YamlConfiguration())
                .beforeLoad(sect -> {
                    if (defaults != null) {
                        saveDefaults(resolver, defaults, file, false);
                    }
                    loadFromFile(sect, file);
                })
                .afterSave(sect -> {
                    saveToFile(sect, file);
                }
        );

        return helper;
    }

}
