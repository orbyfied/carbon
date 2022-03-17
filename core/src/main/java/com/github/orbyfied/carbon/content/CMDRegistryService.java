package com.github.orbyfied.carbon.content;

import com.github.orbyfied.carbon.element.RegistrableElement;
import com.github.orbyfied.carbon.registry.Registry;
import com.github.orbyfied.carbon.registry.RegistryService;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CMDRegistryService<T extends RegistrableElement>
        extends RegistryService<Registry<T>, T> {

    public CMDRegistryService(Registry<T> registry) {
        super(registry);
    }

    private HashMap<Material, ArrayList<ModelHolder<T>>> cmds = new HashMap<>(); // raw custom model data counter

    public HashMap<Material, ArrayList<ModelHolder<T>>> getCMDRegistry() {
        return cmds;
    }

    public int next(Material base, ModelHolder<T> holder) {
        ArrayList<ModelHolder<T>> list = cmds.computeIfAbsent(base, __ -> new ArrayList<>());
        int i = list.size();
        list.add(holder);
        return i;
    }

    public ModelHolder<T> getHolderOf(Material base, int off) {
        ArrayList<ModelHolder<T>> holders;
        if ((holders = cmds.get(base)) == null) return null;
        if (off >= holders.size() || off < 0) return null;
        return holders.get(off);
    }

    public Object getModelOf(Material base, int off) {
        ModelHolder<T> h = getHolderOf(base, off);
        return h.getModel(off - h.getCustomModelDataOffset());
    }

    public BaseEditing edit(Material base) {
        return new BaseEditing(base);
    }

    public class BaseEditing {

        private final Material base;
        private final ArrayList<ModelHolder<T>> list;

        public BaseEditing(Material base) {
            this.base = base;
            this.list = cmds.computeIfAbsent(base, __ -> new ArrayList<>());
        }

        public int next(ModelHolder<T> holder) {
            int i = list.size();
            list.add(holder);
            return i;
        }

    }

}
