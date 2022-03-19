package com.github.orbyfied.carbon.content;

import com.github.orbyfied.carbon.content.pack.SourcedAsset;
import com.github.orbyfied.carbon.element.RegistrableElement;
import com.github.orbyfied.carbon.util.resource.ResourceHandle;
import org.bukkit.Material;

public interface ModelHolder<T extends RegistrableElement> {

    int getCustomModelDataOffset();

    SourcedAsset getModel(int off);

    SourcedAsset[] getModels();

    // utility to make life easier
    default int registerAllAndGetOffset(
            Material base,
            CMDRegistryService<T> service,
            int amount) {
        if (amount == 0) return -1;
        CMDRegistryService<T>.BaseEditing edit = service.edit(base);
        int off = 0;
        int start = edit.next(this);
        for (; off < amount; off++)
            edit.next(this);
        return start;
    }

}
