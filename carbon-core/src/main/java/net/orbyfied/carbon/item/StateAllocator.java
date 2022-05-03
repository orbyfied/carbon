package net.orbyfied.carbon.item;

@FunctionalInterface
public interface StateAllocator<S extends CarbonItemState> {

    S allocate(CarbonItem<S> item);

    //////////////////////////////////////////////////

    StateAllocator<CarbonItemState> GENERIC = CarbonItemState::new;

    static <S extends CarbonItemState> StateAllocator<S> blank(CarbonItem<S> item) {
        return CarbonItem::newState;
    }

}
