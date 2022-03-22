package com.github.orbyfied.carbon.content.pack.host;

import com.github.orbyfied.carbon.bootstrap.CarbonBranding;
import com.github.orbyfied.carbon.config.Configurable;
import com.github.orbyfied.carbon.content.pack.ResourcePackManager;
import com.google.common.graph.Network;
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
import java.net.NetworkInterface;
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
public class PackHostServer extends PackHostProvider {

    /**
     * The port for the server.
     */
    private static int PORT;

    static {
        PORT = Bukkit.getServer().getPort() + 1;
    }

    /** Constructor. */
    public PackHostServer(ResourcePackManager manager) {
        super(manager, true);
    }

    /**
     * The HTTP server instance.
     */
    HttpServer server;

    /**
     * The server executor pool.
     */
    Executor exec = Executors.newFixedThreadPool(1);

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
     * The pack file.
     */
    Path packFile;

    /**
     * Creates, assigns and starts the server.
     */
    @Override
    public void start() {

        try {

            // create and set up server
            server = HttpServer.create();
            server.setExecutor(exec);
            server.bind(new InetSocketAddress(PORT), 0);

            // create and bind download url
            packDownloadLoc = "/carbon/rp_d/" + packFile.getFileName().toString().split("\\.")[0] +
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
                exchange.sendResponseHeaders(200, Files.size(packFile));
                InputStream fis = Files.newInputStream(packFile);
                fis.transferTo(out);
                out.flush();
                out.close();
            });

            // start server
            server.start();

            // log
            manager.getLogger().ok("Successfully started HTTP server on " +
                    "127.0.0.1:" + PORT + " (pack url: " + getLocalDownloadUrl(0) + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Stops the HTTP server.
     */
    @Override
    public void stop() {
        // stop lol
        server.stop(0);
    }

    @Override
    public int host(Path pack, int id) {
        packFile = pack;
        try {
            // create hash
            hash = MessageDigest
                    .getInstance("sha-1")
                    .digest(Files.newInputStream(packFile).readAllBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public byte[] getHashOf(int artifactId) {
        if (artifactId != 0)
            throw new UnsupportedOperationException("only one artifact supported");
        return hash;
    }

    @Override
    public String getLocalDownloadUrl(int artifactId) {
        if (artifactId != 0)
            throw new UnsupportedOperationException("only one artifact supported");
        return "127.0.0.1:" + PORT + packDownloadLoc;
    }

    @Override
    public String getDownloadUrlFor(int artifactId, Player player) {
        if (artifactId != 0)
            throw new UnsupportedOperationException("only one artifact supported");
        try {
            NetworkInterface ni = NetworkInterface.getByInetAddress(
                    manager.getMain().getPlatform().getNetworkProxy().getConnectionOf(player).virtualHost.getAddress()
            );
            System.out.println(ni);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    ////////////////////////////////////////

    private static String toHexString(byte b) {
        return Integer.toUnsignedString(Byte.toUnsignedInt(b), 16);
    }

}
