package net.orbyfied.carbon.element;

import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.registry.MalformedIdentifierException;
import net.orbyfied.carbon.util.StringReader;

import java.util.List;
import java.util.Objects;

public class SpecifiedIdentifier extends Identifier {

    public static SpecifiedIdentifier parse(SpecifiedIdentifier id,
                                            String str) {
        StringReader reader     = new StringReader(str, 0);
        List<String> components = reader.split(':', '/');

        if (components.size() >= 3) {
            return new SpecifiedIdentifier(
                    components.get(0),
                    components.get(1),
                    components.get(2)
            );
        } else if (components.size() == 2) {
            return new SpecifiedIdentifier(
                    null,
                    components.get(0),
                    components.get(1)
            );
        } else {
            throw new MalformedIdentifierException(str, SpecifiedIdentifier.class);
        }

    }

    ////////////////////////////////////////////

    /**
     * The group these elements belong to.
     */
    protected String elementGroup;

    /**
     * Constructor.
     * @param namespace The namespace.
     * @param elementGroup The group.
     * @param path The path.
     */
    public SpecifiedIdentifier(String namespace,
                               String elementGroup,
                               String path) {
        super(namespace, path);
        this.elementGroup = elementGroup;
    }

    /**
     * Get the group this belongs to.
     * @return The group.
     */
    public String getGroup() {
        return elementGroup;
    }

    ////////////////////////////////////////

    @Override
    public String toString() {
        return namespace + ":" + elementGroup + "/" + path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SpecifiedIdentifier that = (SpecifiedIdentifier) o;
        return Objects.equals(elementGroup, that.elementGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), elementGroup);
    }

}
