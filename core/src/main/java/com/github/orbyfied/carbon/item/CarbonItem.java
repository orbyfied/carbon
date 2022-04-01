package com.github.orbyfied.carbon.item;

import com.github.orbyfied.carbon.element.RegistrableElement;
import com.github.orbyfied.carbon.registry.Identifiable;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.util.mc.ItemUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static com.github.orbyfied.carbon.util.mc.Nbt.getOrCreateCompound;

import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * A custom item type.
 */
public class CarbonItem<S extends CarbonItemState> extends RegistrableElement {

    public static final String ITEM_STATE_TAG = "CarbonItemState";
    public static final String ITEM_ID_TAG    = "CarbonItemId";

    ///////////////////////////////////////////////////////

    /**
     * The identifier of this item.
     * Storing it as a variable makes
     * implementation easier.
     */
    protected final Identifier identifier;

    /**
     * The base material of the item.
     */
    protected Material baseMaterial;

    /**
     * The display strategy for this item.
     */
    protected ItemDisplayStrategy displayStrategy;

    protected ItemInteractStrategy interactStrategy;

    protected StateAllocator<S> stateAllocator;

    protected final Class<S> runtimeStateType;

    protected boolean isBuilt = false;

    /**
     * Internal Constructor.
     * It is supposed to be called
     * using {@code super(...)} inside
     * of an item class to supply an identifier.
     * @param id The identifier of this block.
     */
    public CarbonItem(Identifier id,
                      Class<S> runtimeStateType) {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(runtimeStateType, "runtime state type cannot be null");
        this.identifier       = id;
        this.runtimeStateType = runtimeStateType;
    }

    /**
     * @see Identifiable#getIdentifier()
     */
    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    public Material getBaseMaterial() {
        return baseMaterial;
    }

    public ItemDisplayStrategy getDisplayStrategy() {
        return displayStrategy;
    }

    public ItemInteractStrategy getInteractStrategy() {
        return interactStrategy;
    }

    public CarbonItem<S> setBaseMaterial(Material material) {
        this.baseMaterial = material;
        return this;
    }

    public CarbonItem<S> setDisplayStrategy(ItemDisplayStrategy displayStrategy) {
        this.displayStrategy = displayStrategy;
        return this;
    }

    public CarbonItem<S> setInteractStrategy(ItemInteractStrategy interactStrategy) {
        this.interactStrategy = interactStrategy;
        return this;
    }

    public <T extends ItemDisplayStrategy> CarbonItem<S> setDisplayStrategy(Function<CarbonItem<S>, T> constructor) {
        this.displayStrategy = constructor.apply(this);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends ItemDisplayStrategy> CarbonItem<S> setDisplayStrategy(Function<CarbonItem<S>, T> constructor,
                                                                            BiConsumer<CarbonItem<S>, T> consumer) {
        setDisplayStrategy(constructor);
        consumer.accept(this, (T) displayStrategy);
        return this;
    }

    public StateAllocator<S> getStateAllocator() {
        return stateAllocator;
    }

    public CarbonItem<S> setStateAllocator(StateAllocator<S> stateAllocator) {
        this.stateAllocator = stateAllocator;
        return this;
    }

    public CarbonItem<S> build() {
        // build strategies
        displayStrategy.build();

        // check for unset
        if (baseMaterial == null)
            throw new IllegalArgumentException("base material is null");
        if (stateAllocator == null)
            stateAllocator = StateAllocator.blank(this);

        // set built
        isBuilt = true;

        // return
        return this;
    }

    /**
     * Creates a new, empty state for
     * an item. Should be overridden
     * to create a new item state for
     * this item.
     * @return The new state.
     */
    @SuppressWarnings("unchecked")
    public S newState() {
        return (S) new CarbonItemState<>(this);
    }

    public S loadState(ItemStack stack) {
        // check if it is built
        checkBuilt();

        // get handle
        net.minecraft.world.item.ItemStack nmsStack = ItemUtil.getHandle(stack);
        CompoundTag stateTag = nmsStack.getOrCreateTag().getCompound(ITEM_STATE_TAG);

        // allocate state
        S s = stateAllocator.allocate(this);

        // load state
        s.load(nmsStack, stateTag);

        // return
        return s;
    }

    /**
     * Creates a new item stack with
     * the default state.
     * @see CarbonItem#newState()
     * @return The item stack.
     */
    public ItemStack newStack() {
        // check if it is built
        checkBuilt();

        // create stack with base item
        // and get required handle and data
        ItemStack stack = ItemUtil.newCraftStack(
                baseMaterial, 1, (short) 0, null
        );

        net.minecraft.world.item.ItemStack nmsStack = ItemUtil.getHandle(stack);
        CompoundTag tag = nmsStack.getOrCreateTag();

        // set item id
        tag.putInt(ITEM_ID_TAG, getId());

        // write default state
        S state = newState();
        state.save(nmsStack, getOrCreateCompound(tag, ITEM_STATE_TAG));

        // set default name
        nmsStack.setHoverName(new TextComponent("item." + identifier)
                .setStyle(Style.EMPTY.withItalic(false)));

        // initialize display
        displayStrategy.makeItem(nmsStack, state, tag);

        // return stack
        return stack;
    }

    /* ------ Internal ------- */

    private void checkBuilt() {
        if (!isBuilt)
            throw new IllegalStateException("item " + identifier + " has not been built yet!");
    }

     /////////////////////////////

    @Override
    public String toString() {
        return identifier.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CarbonItem<?> item = (CarbonItem<?>) o;
        return Objects.equals(item.identifier, identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
    
}
