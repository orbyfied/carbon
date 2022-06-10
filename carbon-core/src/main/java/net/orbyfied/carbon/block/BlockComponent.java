package net.orbyfied.carbon.block;

import net.orbyfied.carbon.element.AbstractElementComponent;

public abstract class BlockComponent<S extends CarbonBlockState> extends
        AbstractElementComponent<CarbonBlock<S>> {

    public BlockComponent(CarbonBlock<S> element) {
        super(element);
    }

}
