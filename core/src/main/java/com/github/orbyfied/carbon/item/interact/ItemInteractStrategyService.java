package com.github.orbyfied.carbon.item.interact;

import com.github.orbyfied.carbon.content.StrategyService;
import com.github.orbyfied.carbon.item.ItemInteractStrategy;

public class ItemInteractStrategyService extends StrategyService<ItemInteractStrategy> {

    public ItemInteractStrategyService() {
        super(ItemInteractStrategy.class);
    }

    /*
            // get item handle
            if (event.getItem() == null)
                return;
            ItemStack nmsStack = ItemUtil.getHandle(event.getItem());

            // get item
            CompoundTag tag = nmsStack.getTag();
            if (tag == null) return;
            if (!tag.contains(CarbonItem.ITEM_ID_TAG)) // check if it is custom item
                return;
            int id = tag.getInt(CarbonItem.ITEM_ID_TAG);

            CarbonItem<?> item = (CarbonItem<?>) registry.getComponent(ModElementRegistry.class).getLinear(id);
     */

}
