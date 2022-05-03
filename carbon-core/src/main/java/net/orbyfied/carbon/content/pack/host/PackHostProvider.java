package net.orbyfied.carbon.content.pack.host;

import net.orbyfied.carbon.bootstrap.CarbonBranding;
import net.orbyfied.carbon.content.pack.ResourcePackManager;
import net.orbyfied.carbon.logging.BukkitLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.nio.file.Path;

/**
 * Class that will implement resource
 * pack hosting.
 */
public abstract class PackHostProvider {

    /**
     * The resource pack manager.
     */
    protected final ResourcePackManager manager;

    /**
     * The logger for this provider.
     */
    protected final BukkitLogger logger;

    /**
     * The optional event listener.
     */
    protected EventListener listener;

    /** Constructor. */
    public PackHostProvider(final ResourcePackManager manager,
                            final boolean constructEvents) {
        this.manager = manager;
        this.logger  = manager.getLogger();
        if (constructEvents)
            Bukkit.getPluginManager().registerEvents(
                    listener = new EventListener(),
                    manager.getMain().getPlugin()
            );
    }

    /**
     * Should start the provider.
     * For example: To start an HTTP server.
     */
    public abstract void start();

    /**
     * Should stop the provider.
     * For example: To stop an HTTP server.
     */
    public abstract void stop();

    /**
     * Should host or upload the provided
     * file on the provider.
     * @param pack The pack file.
     * @param id The unique artifact ID to be used.
     * @return The unique ID for the upload.
     */
    public abstract int host(Path pack, int id);

    /**
     * Gets the, preferably precomputed,
     * hash of the specified artifact.
     * @param artifactId The artifact ID.
     * @return The hash of the artifact.
     */
    public abstract byte[] getHashOf(int artifactId);

    /**
     * Gets the local download URL for
     * the specified artifact.
     * @param artifactId The artifact.
     * @return The URL.
     */
    public abstract String getLocalDownloadUrl(int artifactId);

    /**
     * Gets the download URL for the
     * specified artifact targeted
     * at the provided player.
     * @param artifactId The artifact ID.
     * @param player The player.
     * @return The URL.
     */
    public abstract String getDownloadUrlFor(int artifactId, Player player);

    /**
     * Sends the specified artifact to
     * the specified player as resource pack.
     * @param artifactId The artifact to send.
     * @param player The player to send it to.
     * @param force If it should force the resource pack.
     */
    public void sendResourcePack(int artifactId, Player player, boolean force) {
        // get data
        String url  = getDownloadUrlFor(artifactId, player);
        byte[] hash = getHashOf(artifactId);

        // create prompt
        String prompt = createPrompt(artifactId, player, force);

        // send
        player.setResourcePack(
                url,
                hash,
                prompt,
                force
        );
    }

    /**
     * Sends the specified artifact
     * to all currently online players.
     * @param artifactId The artifact to send.
     * @param force Whether to force the pack to be applied.
     */
    public final void sendPackToAllPlayers(int artifactId, boolean force) {
        for (Player player : Bukkit.getOnlinePlayers())
            sendResourcePack(artifactId, player, force);
    }

    /**
     * Creates the prompt to show for the
     * specified artifact targeted at the
     * specified player. More of an internal method.
     * @see PackHostProvider#sendResourcePack(int, Player, boolean)
     * @return The formatted prompt text.
     */
    public String createPrompt(int artifactId, Player player, boolean force) {
        int modCount = manager.getMain().getModLoader().getMods().size();
        String prompt = ChatColor.GOLD + "" + ChatColor.BOLD + "⚠ " +
                CarbonBranding.PREFIX + ChatColor.DARK_GRAY + " mod resources. " +
                ChatColor.GOLD + "" + ChatColor.BOLD + "⚠ \n" +
                ChatColor.GRAY + "This resource pack contains the resources of " +
                ChatColor.YELLOW + modCount + ChatColor.GRAY + " mods.";
        if (!force)
            prompt += ChatColor.RED + "\n⚠ By declining you will miss out on unique and " +
                    "engaging content.";
        else
            prompt += ChatColor.RED + "\n⚠ This resource pack is required to play on this server.";
        return prompt;
    }

    /* ---- EVENTS ---- */

    public class EventListener implements Listener {

        public int artifactId = 0;
        public boolean force  = false;

        @EventHandler
        public void playerJoin(PlayerJoinEvent event) {
            sendResourcePack(artifactId, event.getPlayer(), force);
        }

    }

}
