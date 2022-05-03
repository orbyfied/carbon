package net.orbyfied.carbon.item.material;

/**
 * The material API is like OreDictionary,
 * it tags items with properties which denote
 * it as a type of item. Imagine two mods register
 * copper dust. Then in crafting, you require copper
 * dust. If you match by item you will only be able
 * to use one of the dusts, but with material tags
 * (which describe what an item actually is) you
 * will be able to use both if they both register a
 * common tag, like {@code dust.copper}.
 *
 * This class just provides utilities for working
 * with the material system/'API'.
 *
 * @see MaterialTag
 */
public class MaterialAPI {

    public static boolean is(MaterialTag tag, MaterialTag match) {
        String[] parts = match.parts;
        // tag is unspecific, there for it cant match the detail
        // of the provided matching string
        if (parts.length > tag.length())
            return false;

        String[] otherParts = tag.parts;
        int l = parts.length;
        for (int i = 0; i < l; i++)
            if (!otherParts[i].equals(parts[i]))
                return false;
        return true;
    }

    public static boolean is(MaterialTag tag, String str) {
        return is(tag, MaterialTag.of(str));
    }

    public static MaterialTag tag(String... parts) {
        return new MaterialTag(parts);
    }

    public static MaterialTag tag(String str) {
        return MaterialTag.of(str);
    }

}
