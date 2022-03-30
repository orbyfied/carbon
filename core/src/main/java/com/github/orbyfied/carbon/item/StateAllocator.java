package com.github.orbyfied.carbon.item;

@FunctionalInterface
public interface StateAllocator<S extends CarbonItemState> {

    S allocate(CarbonItem<S> item);

    //////////////////////////////////////////////////

    StateAllocator<CarbonItemState> GENERIC = CarbonItemState::new;

}
