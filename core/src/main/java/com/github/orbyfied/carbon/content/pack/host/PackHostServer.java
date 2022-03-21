package com.github.orbyfied.carbon.content.pack.host;

import com.github.orbyfied.carbon.content.pack.ResourcePackManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PackHostServer {

    public static final int PORT = 25566;

    final ResourcePackManager manager;
    HttpServer server;

    Executor exec = Executors.newFixedThreadPool(1);

    public PackHostServer(ResourcePackManager manager) {
        this.manager = manager;
    }

    public void start() {
        final Path resourcePack = manager.packPkgFileNamed;

        try {
            server = HttpServer.create();
            System.out.println(server.getClass());
            server.setExecutor(exec);
            server.bind(new InetSocketAddress(PORT), 0);
            server.createContext("/carbon/rp_d/", exchange -> {
                if (!exchange.getRequestMethod().equals("GET"))
                    return;
                OutputStream out = exchange.getResponseBody();
                exchange.sendResponseHeaders(200, Files.size(resourcePack));
                InputStream fis = Files.newInputStream(resourcePack);
                fis.transferTo(out);
                out.flush();
                out.close();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
