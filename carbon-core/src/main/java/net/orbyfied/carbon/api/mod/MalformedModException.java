package net.orbyfied.carbon.api.mod;

import net.orbyfied.carbon.core.mod.LoadedMod;

public class MalformedModException extends ModLoaderException {

    public MalformedModException(LoadedMod mod, String s) {
        super(mod, s);
    }

    public MalformedModException(LoadedMod mod, Exception e) {
        super(mod, e);
    }

    public MalformedModException(LoadedMod mod, String s, Exception e) {
        super(mod, s, e);
    }

}
