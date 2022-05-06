package net.orbyfied.carbon.util.mc;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Item builder which builds skull item stacks with
 * custom textures.
 * @param <M> Should never be manually set!
 */
public class SkullBuilder<M extends MetaBuilder<SkullMeta, M>> extends ItemBuilder<SkullMeta, M, SkullBuilder<M>> {

    public static <M extends MetaBuilder<SkullMeta, M>> SkullBuilder<M> construct() {
        return new SkullBuilder<>();
    }

    public static <M extends MetaBuilder<SkullMeta, M>> SkullBuilder<M> construct(String texture) {
        return new SkullBuilder<>();
    }

    /**
     * String which will prefix every URL.
     */
    static final String PREFIX = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv";

    /**
     * The Base64 texture string.
     */
    String texture;

    protected SkullBuilder() { super(); material = Material.PLAYER_HEAD; amount = 1; }

    public SkullBuilder<M> setTexture(String texture) {
        this.texture = texture;
        return this;
    }

    public GameProfile buildProfile() {
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", PREFIX + texture));
        return profile;
    }

    @Override
    public ItemStack build() {
        meta(meta -> {
            try {
                Field f = meta.it.getClass().getDeclaredField("profile");
                f.setAccessible(true);
                f.set(meta.it, buildProfile());
            } catch (Exception e) { e.printStackTrace(); }
        });

        return super.build();
    }

    @Override
    public SkullBuilder setMaterial(Material material) {
        throw new UnsupportedOperationException("material cannot be changed");
    }
}
