package net.orbyfied.carbon.api.mod;

import net.orbyfied.carbon.core.mod.LoadedMod;

public class ModLoaderException extends RuntimeException {

    private LoadedMod mod;

    public ModLoaderException(LoadedMod mod, String s) {
        super(s);
        this.mod = mod;
    }

    public ModLoaderException(LoadedMod mod, Throwable e) {
        super(e);
        this.mod = mod;
    }

    public ModLoaderException(LoadedMod mod, String s, Throwable e) {
        super(s, e);
        this.mod = mod;
    }

    @Override
    public String getMessage() {
        return "for mod " + mod.asAvailableString() + ": " + super.getMessage();
    }
}
