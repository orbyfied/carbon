package net.orbyfied.carbon.core.mod;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.api.CarbonModAPI;
import net.orbyfied.carbon.api.mod.CarbonMod;
import net.orbyfied.carbon.api.mod.CarbonModInitializer;
import net.orbyfied.carbon.api.mod.MalformedModException;
import net.orbyfied.carbon.api.mod.ModLoaderException;
import net.orbyfied.carbon.api.util.Version;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public class LoadedMod {

    /**
     * The Carbon main instance.
     */
    protected final Carbon main;

    /**
     * The mod property descriptor.
     */
    protected CarbonMod.Descriptor descriptor;

    /**
     * The mod initializer class.
     */
    protected Class<? extends CarbonModInitializer> modInitializerClass;

    /**
     * The mod initializer.
     */
    protected CarbonModInitializer modInitializer;

    /**
     * The Bukkit plugin it is linked to.
     * This is also the mod instance and the
     * holder of the {@link CarbonMod}.
     */
    protected final Plugin plugin;

    /**
     * The plugin/mod class.
     */
    protected final Class<? extends Plugin> modClass;

    /**
     * The assigned mod API.
     */
    protected CarbonModAPI api;

    /**
     * The mod API factory.
     */
    protected CarbonModAPIFactory<? extends CarbonModAPI> modApiFactory = CarbonModAPIFactory.JAVA_API_FACTORY;

    /**
     * Constructs a loadable mod with
     * the plugin instance.
     * @param plugin The plugin instance.
     */
    public LoadedMod(Carbon carbon,
                     Plugin plugin) {
        Objects.requireNonNull(carbon, "carbon reference cannot be null");
        Objects.requireNonNull(plugin, "plugin reference cannot be null");
        this.main     = carbon;
        this.plugin   = plugin;
        this.modClass = this.plugin.getClass();
    }

    /**
     * Gets the mod descriptor.
     * Loads it if necessary.
     * @return The descriptor.
     */
    public CarbonMod.Descriptor getDescriptor() {
        // load descriptor if needed
        if (descriptor == null) {
            CarbonMod an = modClass.getAnnotation(CarbonMod.class);
            if (an == null)
                throw new MalformedModException(this, "no mod descriptor found");
            try { // set descriptor safely
                this.descriptor = CarbonMod.Descriptor.of(modClass, an);
            } catch (Exception e) {
                throw new MalformedModException(this, "error while compiling descriptor", e);
            }
        }

        // return
        return this.descriptor;
    }

    @SuppressWarnings("unchecked")
    public LoadedMod load() {
        // load descriptor
        getDescriptor();

        // get the mod initializer class
        this.modInitializerClass = descriptor.getInitializerClass();

        // instantiate a mod initializer if it is a remote class
        if (modInitializerClass != CarbonModInitializer.class) {
            try {
                // get constructor
                Constructor<? extends CarbonModInitializer> constructor = (Constructor<? extends CarbonModInitializer>)
                        modInitializerClass.getDeclaredConstructor(LoadedMod.class);
                constructor.setAccessible(true);

                // create instance
                modInitializer = constructor.newInstance(this);
            } catch (NoSuchMethodException e) {
                // catch no or invalid constructor
                throw new MalformedModException(this, "no constructor for mod initializer");
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                // catch instantiation exception
                throw new MalformedModException(this, "failed to instantiate mod initializer", e);
            }
        } else { // use mod instance as default initializer
            if (!(plugin instanceof CarbonModInitializer))
                throw new MalformedModException(this, "expected mod class to be initializer");

            // set instance and class
            modInitializer      = (CarbonModInitializer) plugin;
            modInitializerClass = (Class<? extends CarbonModInitializer>) modClass;
        }

        // call loaded
        try {
            modInitializer.modLoaded(this);
        } catch (Throwable e) {
            // catch exceptions in there
            throw new ModLoaderException(this, "Exception occurred in load handler "
                    + modInitializerClass.getName() + "#modLoaded(...)", e);
        }

        // return
        return this;
    }

    public LoadedMod initialize() {
        // construct mod API if needed
        if (api == null) {
            api = modApiFactory.create(main, this);
        }

        // check api
        if (api == null)
            throw new ModLoaderException(this, "mod api cannot be null");

        // link, ready or modify the
        // API if it is accepted
        if (modApiFactory.accepts(main, this, api))
            modApiFactory.link(main, this, api);

        try {
            // call initialized
            modInitializer.modInitialize(api);
        } catch (Throwable e) {
            // catch exceptions in there
            throw new ModLoaderException(this, "Exception occurred in initialization handler "
                    + modInitializerClass.getName() + "#modInitialize(...)", e);
        }

        // return
        return this;
    }

    /**
     * Disables/unloads this mod.
     * @return This.
     */
    public LoadedMod disable() {
        // call disable
        modInitializer.modDisable(api);

        // return
        return this;
    }

    /**
     * Builds an information string with
     * all currently available data.
     * @return The information string.
     */
    public String asAvailableString() {
        // create builder
        StringBuilder b = new StringBuilder();

        // append certain mod information
        b.append("mod(").append(modClass.getName()).append(")");

        if (descriptor != null) { // append descriptor
            b.append(": ")
                    .append(descriptor.getId()) // mod id
                    .append(" (")
                    .append(descriptor.getName()) // mod name
                    .append(")");
        }

        // return
        return b.toString();
    }

    /* Getters. */

    public Plugin getPlugin() {
        return plugin;
    }

    public Class<? extends Plugin> getPluginClass() {
        return modClass;
    }

    public CarbonModInitializer getModInitializer() {
        return modInitializer;
    }

    public Class<?> getModInitializerClass() {
        return modInitializerClass;
    }

    public CarbonModAPI getApi() {
        return api;
    }

    public String getId() {
        return descriptor.getId();
    }

    public String getName() {
        return descriptor.getName();
    }

    public String getDescription() {
        return descriptor.getDescription();
    }

    public Version getVersion() {
        return descriptor.getVersion();
    }

    //////////////////////////

    @Override
    public String toString() {
        return asAvailableString();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

}
