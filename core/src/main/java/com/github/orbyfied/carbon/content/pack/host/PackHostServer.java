package com.github.orbyfied.carbon.content.pack.host;

import com.github.orbyfied.carbon.bootstrap.CarbonBranding;
import com.github.orbyfied.carbon.content.pack.ResourcePackManager;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Class for hosting and sending
 * the resource pack.
 */
public class PackHostServer implements Listener {

    /**
     * The port for the server.
     */
    private static int PORT;

    static {
        PORT = Bukkit.getServer().getPort() + 1;
    }

    /**
     * The parent resource pack manager.
     */
    final ResourcePackManager manager;

    /**
     * The HTTP server instance.
     */
    HttpServer server;

    /**
     * The server executor pool.
     */
    Executor exec = Executors.newFixedThreadPool(1);

    /** Constructor. */
    public PackHostServer(ResourcePackManager manager) {
        this.manager = manager;
    }

    /**
     * The URL assigned to the created
     * resource pack. Done in {@link PackHostServer#start()}
     */
    String packDownloadLoc;

    /**
     * The resource pack hash.
     * For security.
     */
    byte[] hash;

    /**
     * If the players should be forced
     * to download the resource pack.
     */
    boolean forcePack = true;

    public String getLocalPackDownloadUrl() {
        return "127.0.0.1:" + PORT + packDownloadLoc;
    }

    public String getPackDownloadFor(Player player) {
        String s = getLocalPackDownloadUrl();
        return s;
    }

    /**
     * Creates, assigns and starts the server.
     */
    public void start() {

        // get resource pack (renamed)
        final Path resourcePack = manager.packPkgFileNamed;

        try {
            // create hash
            hash = MessageDigest
                    .getInstance("sha-1")
                    .digest(Files.newInputStream(resourcePack).readAllBytes());

            // create and set up server
            server = HttpServer.create();
            server.setExecutor(exec);
            server.bind(new InetSocketAddress(PORT), 0);

            // create and bind download url
            packDownloadLoc = "/carbon/rp_d/" + resourcePack.getFileName().toString().split("\\.")[0] +
                        "-" + toHexString(hash[0]) + toHexString(hash[1]) + toHexString(hash[3]) + toHexString(hash[4]) + ".zip";
            server.createContext(packDownloadLoc, exchange -> {
                // check request method for GET
                if (!exchange.getRequestMethod().equals("GET"))
                    return;

                // set zip content type
                Headers headers = exchange.getResponseHeaders();
                headers.add("Content-Type", "application/zip");

                // write file
                OutputStream out = exchange.getResponseBody();
                exchange.sendResponseHeaders(200, Files.size(resourcePack));
                InputStream fis = Files.newInputStream(resourcePack);
                fis.transferTo(out);
                out.flush();
                out.close();
            });

            // start server
            server.start();

            // log
            manager.getLogger().ok("Successfully started HTTP server on " +
                    "127.0.0.1:" + PORT + " (pack url: " + getLocalPackDownloadUrl() + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Stops the HTTP server.
     */
    public void stop() {
        // stop lol
        server.stop(0);
    }

    /**
     * Sends the resource pack to the
     * player with a fancy message.
     * @param player The player to send it to.
     * @return If it was accepted.
     */
    public void sendPackToPlayer(Player player) {
        // create prompt
        int modCount = manager.getMain().getModLoader().getMods().size();
        String prompt = ChatColor.GOLD + "" + ChatColor.BOLD + "⚠ " +
            CarbonBranding.PREFIX + ChatColor.DARK_GRAY + " mod resources. " +
            ChatColor.GOLD + "" + ChatColor.BOLD + "⚠ \n" +
            ChatColor.GRAY + "This resource pack contains the resources of " +
            ChatColor.YELLOW + modCount + ChatColor.GRAY + " mods.";
        if (!forcePack)
            prompt += ChatColor.RED + "\n⚠ By declining you will miss out on unique and " +
                    "engaging content.";
        else
            prompt += ChatColor.RED + "\n⚠ This resource pack is required to play on this server.";

        // send resource pack
        player.setResourcePack(
                getPackDownloadFor(player),
                null,
                prompt,
                forcePack
        );

    }

    /* ---- PLAYERS ---- */

    @EventHandler
    void onJoin(PlayerJoinEvent event) {
        // send pack
        sendPackToPlayer(event.getPlayer());
    }

    public void sendPackToAllPlayers() {
        for (Player p : Bukkit.getOnlinePlayers())
            sendPackToPlayer(p);
    }

    ////////////////////////////////////////

    private static String toHexString(byte b) {
        return Integer.toUnsignedString(Byte.toUnsignedInt(b), 16);
    }

}
