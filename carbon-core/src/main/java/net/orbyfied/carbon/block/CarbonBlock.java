package net.orbyfied.carbon.block;

import net.orbyfied.carbon.element.RegistrableElement;
import net.orbyfied.carbon.item.ItemStateAllocator;
import net.orbyfied.carbon.registry.Identifiable;
import net.orbyfied.carbon.registry.Identifier;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;


public class CarbonBlock<S extends CarbonBlockState> extends RegistrableElement {

    /**
     * The identifier of this block.
     * Storing it as a variable makes
     * implementation easier.
     */
    protected final Identifier identifier;

    // components
    protected HashMap<Class<? extends BlockComponent<S>>, BlockComponent<S>> componentsMapped = new HashMap<>();
    protected ArrayList<BlockComponent<S>> componentsLinear = new ArrayList<>();

    /**
     * The allocator for the block states.
     */
    protected BlockStateAllocator<S> stateAllocator;

    /**
     * The runtime state type.
     */
    protected final Class<S> runtimeStateType;

    /**
     * Has the block element been built.
     */
    protected boolean isBuilt = false;

    // base nms block and material
    protected Block baseBlock;
    protected Material baseMaterial;

    /**
     * Internal Constructor.
     * It is supposed to be called
     * using {@code super(...)} inside
     * of a block class to supply an identifier.
     * @param id The identifier of this block.
     */
    public CarbonBlock(Identifier id, Class<S> runtimeStateType) {
        this.identifier = id;
        this.runtimeStateType = runtimeStateType;
    }

    /**
     * @see Identifiable#getIdentifier()
     */
    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

}
