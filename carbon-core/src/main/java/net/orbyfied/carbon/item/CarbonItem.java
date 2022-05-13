package net.orbyfied.carbon.item;

import net.minecraft.network.chat.TranslatableComponent;
import net.orbyfied.carbon.element.RegistrableElement;
import net.orbyfied.carbon.registry.Identifiable;
import net.orbyfied.carbon.registry.Identifier;
import net.orbyfied.carbon.registry.Registry;
import net.orbyfied.carbon.util.mc.ItemUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.Item;
import net.orbyfied.carbon.util.nbt.Nbt;
import net.orbyfied.carbon.util.nbt.CompoundObjectTag;
import org.bukkit.Material;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
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

    protected Item baseItem;

    // TODO: this is really inefficient help me
    protected HashMap<Class<? extends ItemComponent<S>>, ItemComponent<S>> componentsMapped = new HashMap<>();
    protected ArrayList<ItemComponent<S>> componentsLinear = new ArrayList<>();

    protected StateAllocator<S> stateAllocator;

    protected final Class<S> runtimeStateType;

    protected boolean isBuilt = false;

    /**
     * Internal Constructor.
     * It is supposed to be called
     * using {@code super(...)} inside
     * of an item class to supply an identifier.
     * @param id The identifier of this block.
     * @param runtimeStateType The type of item state.
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

    public CarbonItem<S> setBaseMaterial(Material material) {
        this.baseMaterial = material;
        this.baseItem     = ItemUtil.getItem(material);
        return this;
    }

    @SuppressWarnings("unchecked")
    public CarbonItem<S> component(ItemComponent<S> component) {
        Class<? extends ItemComponent<S>> klass = (Class<? extends ItemComponent<S>>) component.getClass();
        componentsMapped.put(klass, component);
        componentsLinear.add(component);
        return this;
    }

    public <T extends ItemComponent<S>> CarbonItem<S> component(Function<CarbonItem<S>, T> constructor) {
        component(constructor.apply(this));
        return this;
    }

    public <T extends ItemComponent<S>> CarbonItem<S> component(Function<CarbonItem<S>, T> constructor,
                                                                BiConsumer<CarbonItem<S>, T> consumer) {
        T it = constructor.apply(this);
        component(it);
        consumer.accept(this, it);
        return this;
    }

    public <T extends ItemComponent<S>> CarbonItem<S> component(Class<T> tClass,
                                                                Function<CarbonItem<S>, T> constructor,
                                                                BiConsumer<CarbonItem<S>, T> consumer) {
        T it = component(tClass);
        if (it != null) {
            consumer.accept(this, it);
            return this;
        }


        it = constructor.apply(this);
        component(it);
        consumer.accept(this, it);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends ItemComponent<S>> T component(Class<T> tClass) {
        return (T) componentsMapped.get(tClass);
    }

    public StateAllocator<S> getStateAllocator() {
        return stateAllocator;
    }

    public CarbonItem<S> setStateAllocator(StateAllocator<S> stateAllocator) {
        this.stateAllocator = stateAllocator;
        return this;
    }

    @Override
    public CarbonItem<S> register(Registry registry) {
        super.register(registry);
        return this;
    }

    public CarbonItem<S> build() {
        // build components
        int l = componentsLinear.size();
        for (int i = 0; i < l; i++) {
            componentsLinear.get(i).build();
        }

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
     * Creates a new, default state for
     * this item.
     * @return The new state.
     */
    public S newState() {
        return stateAllocator.allocate(this);
    }

    /**
     * Allocates a new state.
     * @return
     */
    @SuppressWarnings("unchecked")
    public S allocateState() {
        return (S) new CarbonItemState<>(this);
    }

    public S loadState(ItemStack nmsStack) {
        // check if it is built
        checkBuilt();

        // get handle
        CompoundTag tag = nmsStack.getTag();
        if (tag == null)
            return null;
        CompoundObjectTag<S> stateTag = Nbt.getOrLoadObject(tag, ITEM_STATE_TAG);
        if (stateTag == null)
            return null;

        // return
        return stateTag.getObject();
    }

    /**
     * Creates a new item stack with
     * the default state.
     * @see CarbonItem#newState()
     * @return The item stack.
     */
    public net.minecraft.world.item.ItemStack newStack() {
        // check if it is built
        checkBuilt();

        // create base stack with the base material
        ItemStack nmsStack = new ItemStack(baseItem);
        CompoundTag tag = nmsStack.getOrCreateTag();

        // set item id
        tag.putString(ITEM_ID_TAG, identifier.toString());

        // write default state
        S state = newState();
        CompoundObjectTag<S> stag = Nbt.getOrCreateObject(tag, ITEM_STATE_TAG, () -> state);

        // set default name
        nmsStack.setHoverName(
                new TranslatableComponent("item." + identifier.getNamespace() + "." + identifier.getPath()).setStyle(
                        Style.EMPTY.withItalic(false)
                )
        );

        // compile stack
        CompiledStack compiledStack = new CompiledStack()
                .primitive(nmsStack)
                .trust(state);

        // update
        updateStack(compiledStack);

        // return stack
        return nmsStack;
    }

    @SuppressWarnings("unchecked")
    public CarbonItem updateStack(CompiledStack compiledStack) {
        // update components
        int l = componentsLinear.size();
        for (int i = 0; i < l; i++) {
            componentsLinear.get(i).updateStack(
                    compiledStack,
                    (S) compiledStack.getState(),
                    compiledStack.getStack().getOrCreateTag()
            );
        }

        return this;
    }

    /* ------ Internal ------- */

    private void checkBuilt() {
        if (!isBuilt)
            throw new IllegalStateException("item " + identifier + " has not been built yet!");
    }

    /////////////////////////////////

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
