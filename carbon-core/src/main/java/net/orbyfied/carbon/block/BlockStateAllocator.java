package net.orbyfied.carbon.block;

import net.orbyfied.carbon.item.CarbonItem;
import net.orbyfied.carbon.item.CarbonItemState;

@FunctionalInterface
public interface BlockStateAllocator<S extends CarbonBlockState> {

    S allocate(CarbonBlock<S> item);

    //////////////////////////////////////////////////

    BlockStateAllocator<CarbonBlockState> GENERIC = CarbonBlockState::new;

}
