package net.orbyfied.carbon.api.mod;

import net.orbyfied.carbon.api.util.Version;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that a class is a Carbon mod.
 * This is a sort of descriptor, holding
 * all necessary information about a mod.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CarbonMod {

    /**
     * The mod ID.
     */
    String id();

    /**
     * The mod initializer.
     * @see CarbonModInitializer
     */
    Class<? extends CarbonModInitializer> initializer() default CarbonModInitializer.class;

    /**
     * The mod (display) name.
     */
    String name();

    /**
     * The mod version.
     * Must follow {@link Version}
     */
    String version();

    /**
     * The authors of this mod. Optional.
     */
    String[] authors() default { };

    /**
     * A mod property descriptor.
     */
    class Descriptor {

        /**
         * Compiles an annotation into a descriptor.
         * @return The descriptor.
         */
        public static Descriptor of(Class<?> on, CarbonMod ann) {
            return new Descriptor(
                    ann.initializer(),
                    ann.id(),
                    ann.name(),
                    Version.of(ann.version()),
                    ann.authors(),
                    on);
        }

        /**
         * @see CarbonMod#initializer()
         */
        private final Class<? extends CarbonModInitializer> initializer;

        /**
         * @see CarbonMod#id()
         */
        private final String id;

        /**
         * @see CarbonMod#name()
         */
        private final String name;

        /**
         * @see CarbonMod#version()
         */
        private final Version version;

        /**
         * @see CarbonMod#authors()
         */
        private final String[] authors;

        private final Class<?> onClass;

        private final String description;

        private Descriptor(Class<? extends CarbonModInitializer> initializer,
                           String id,
                           String name,
                           Version version,
                           String[] authors,
                           Class<?> onClass) {
            this.initializer = initializer;
            this.id      = id;
            this.name    = name;
            this.version = version;
            this.authors = authors;
            this.onClass = onClass;

            if (onClass.isAnnotationPresent(CarbonModDescription.class))
                description = onClass.getAnnotation(CarbonModDescription.class).value();
            else
                description = null;
        }

        public Class<? extends CarbonModInitializer> getInitializerClass() {
            return initializer;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Version getVersion() {
            return version;
        }

        public String[] getAuthors() {
            return authors;
        }

        public Class<?> getOnClass() {
            return onClass;
        }

        public String getDescription() {
            return description;
        }
    }

}
