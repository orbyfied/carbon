package net.orbyfied.carbon.content.pack.host;

import net.orbyfied.carbon.config.AbstractConfiguration;
import net.orbyfied.carbon.config.Configurable;
import net.orbyfied.carbon.config.Configure;
import net.orbyfied.carbon.content.pack.ResourcePackManager;
import org.bukkit.entity.Player;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class FilebinPackHost
        extends PackHostProvider
        implements Configurable<FilebinPackHost.Config> {

    public static final String FILEBIN_API_ROUTE =
            "https://filebin.net";

    public FilebinPackHost(ResourcePackManager manager, boolean constructEvents) {
        super(manager, constructEvents);
        manager.getMain().getConfigurationHelper()
                .addConfigurable(this);
        manager.getMain().getConfigurationHelper().loadOne(this);
    }

    final List<byte[]> hashes = new ArrayList<>();
    final List<String> urls   = new ArrayList<>();

    HttpClient client = HttpClient.newBuilder()
            .executor(Executors.newFixedThreadPool(2))
            .build();

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public int host(Path pack, int id) {
        id = urls.size();

        String apiUrl = FILEBIN_API_ROUTE +
                "/" + config.bin + "/Carbon-Mod-Resources.zip";

        try {

            // create hash
            hashes.add(MessageDigest
                    .getInstance("sha-1")
                    .digest(Files.newInputStream(pack).readAllBytes()));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .POST(HttpRequest.BodyPublishers.ofFile(pack))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 201)
                logger.err("Failed to upload resource pack to filebin (code: " +
                        response.statusCode() + "): " + response.body() + " { " + apiUrl + " }");

        } catch (Exception e) {
            e.printStackTrace();
        }

        urls.add(apiUrl);

        return id;
    }

    @Override
    public byte[] getHashOf(int artifactId) {
        return null;
//        return hashes.get(artifactId);
    }

    @Override
    public String getLocalDownloadUrl(int artifactId) {
        return urls.get(artifactId);
    }

    @Override
    public String getDownloadUrlFor(int artifactId, Player player) {
        return urls.get(artifactId);
    }

    @Override
    public String getConfigurationPath() {
        return "resource-pack-host.settings";
    }

    protected Config config = new Config(this);

    @Override
    public Config getConfiguration() {
        return config;
    }

    class Config extends AbstractConfiguration {

        public Config(Configurable<?> configurable) {
            super(configurable);
        }

        @Configure
        public String bin;

    }

}
