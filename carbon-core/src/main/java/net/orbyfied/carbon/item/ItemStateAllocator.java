package net.orbyfied.carbon.item;

@FunctionalInterface
public interface ItemStateAllocator<S extends CarbonItemState> {

    S allocate(CarbonItem<S> item);

    //////////////////////////////////////////////////

    ItemStateAllocator<CarbonItemState> GENERIC = CarbonItemState::new;

    static <S extends CarbonItemState> ItemStateAllocator<S> blank(CarbonItem<S> item) {
        return CarbonItem::allocateState;
    }

}
