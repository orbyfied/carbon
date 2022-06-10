package net.orbyfied.carbon.block;

import net.orbyfied.carbon.element.AbstractElementComponent;
import net.orbyfied.carbon.world.BlockLocation;

public abstract class BlockComponent<S extends CarbonBlockState> extends
        AbstractElementComponent<CarbonBlock<S>> {

    public BlockComponent(CarbonBlock<S> element) {
        super(element);
    }

    public abstract void updateBlock(
            BlockLocation location,
            S state
    );

}
