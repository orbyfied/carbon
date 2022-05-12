package net.orbyfied.carbon.integration.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.integration.Integration;
import net.orbyfied.carbon.integration.IntegrationManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PapiIntegration extends Integration {

    public static final String PAPI_PLUGIN_NAME = "PlaceholderAPI";

    public PapiIntegration(Carbon main, IntegrationManager manager) {
        super(main, manager, "papi", "PlaceholderAPI");
    }

    Extension extension;

    @Override
    protected boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin(PAPI_PLUGIN_NAME) != null;
    }

    @Override
    protected void load() {
        this.extension = new Extension();
        extension.register();
    }

    @Override
    protected void enable() {

    }

    @Override
    protected void disable() {

    }

    class Extension extends PlaceholderExpansion {
        @Override
        public @NotNull String getIdentifier() {
            return "carbon";
        }

        @Override
        public @NotNull String getAuthor() {
            return "orbyfied";
        }

        @Override
        public @NotNull String getVersion() {
            return Carbon.VERSION.toString();
        }

        @Override
        public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
            return super.onRequest(player, params);
        }
    }

}
