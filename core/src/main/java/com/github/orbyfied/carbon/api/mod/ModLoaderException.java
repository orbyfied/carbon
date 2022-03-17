package com.github.orbyfied.carbon.api.mod;

import com.github.orbyfied.carbon.core.mod.LoadedMod;

public class ModLoaderException extends RuntimeException {

    private LoadedMod mod;

    public ModLoaderException(LoadedMod mod, String s) {
        super(s);
        this.mod = mod;
    }

    public ModLoaderException(LoadedMod mod, Exception e) {
        super(e);
        this.mod = mod;
    }

    public ModLoaderException(LoadedMod mod, String s, Exception e) {
        super(s, e);
        this.mod = mod;
    }

    @Override
    public String getMessage() {
        return "for mod " + mod.asAvailableString() + ": " + super.getMessage();
    }
}
