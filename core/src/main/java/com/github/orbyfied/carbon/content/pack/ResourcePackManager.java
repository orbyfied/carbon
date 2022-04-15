package com.github.orbyfied.carbon.content.pack;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.bootstrap.CarbonBranding;
import com.github.orbyfied.carbon.config.AbstractConfiguration;
import com.github.orbyfied.carbon.config.Configurable;
import com.github.orbyfied.carbon.config.Configure;
import com.github.orbyfied.carbon.content.AssetPreparingService;
import com.github.orbyfied.carbon.content.pack.asset.PackMetaBuilder;
import com.github.orbyfied.carbon.content.pack.host.FilebinPackHost;
import com.github.orbyfied.carbon.content.pack.host.PackHostProvider;
import com.github.orbyfied.carbon.content.pack.host.PackHostServer;
import com.github.orbyfied.carbon.content.pack.service.JsonPackService;
import com.github.orbyfied.carbon.content.pack.service.MinecraftAssetService;
import com.github.orbyfied.carbon.core.mod.LoadedMod;
import com.github.orbyfied.carbon.logging.BukkitLogger;
import com.github.orbyfied.carbon.platform.ResourcePackProxy;
import com.github.orbyfied.carbon.process.Process;
import com.github.orbyfied.carbon.process.impl.ParallelTask;
import com.github.orbyfied.carbon.process.impl.QueuedTickExecutionService;
import com.github.orbyfied.carbon.process.impl.SyncTask;
import com.github.orbyfied.carbon.registry.Registry;
import com.github.orbyfied.carbon.util.IOUtil;
import com.github.orbyfied.carbon.util.resource.ResourceHandle;
import net.md_5.bungee.api.ChatColor;
import org.zeroturnaround.zip.ZipUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

public class ResourcePackManager {

    public static final String ASSET_REVISION_FN = ".assetrev";

    /**
     * The main Carbon instance.
     */
    private final Carbon main;

    /**
     * The logger.
     */
    private final BukkitLogger logger;

    public final Path packDir;

    public final Path packSrcDirectory;

    public final Path packPkgFile;

    protected PackHostProvider hostServer;

    protected Configurable<HostConfig> hostConfig = Configurable.of("resource-pack-host", HostConfig::new);

    protected Configurable<BuilderConfig> builderConfig = Configurable.of("resource-pack-builder", BuilderConfig::new);

    public ResourcePackManager(Carbon main) {
        this.main   = main;
        this.logger = main.getLogger("ResourcePack");
        this.packDir = main.getFileInDirectory("resource_pack");
        this.packSrcDirectory = main.getFileInDirectory("resource_pack/src/");
        this.packPkgFile      = main.getFileInDirectory("resource_pack/pack.zip");

        main.getConfigurationHelper()
                .addConfigurable(hostConfig)
                .addConfigurable(builderConfig);
    }

    public Carbon getMain() {
        return main;
    }

    public BukkitLogger getLogger() {
        return logger;
    }

    public PackHostProvider getHostServer() {
        return hostServer;
    }

    public CompletableFuture<ResourcePackManager> build() {

        final CompletableFuture<ResourcePackManager> future = new CompletableFuture<>();

        new Thread(() -> {

            int modCount = main.getModLoader().getMods().size();
            logger.info("Building resource pack for " + modCount + " mods.");

            // delete old resource pack and create new
            try {

                // delete old
                Files.deleteIfExists(packPkgFile);
                IOUtil.deleteDirectory(packSrcDirectory);

                // create new
                Files.createDirectories(packSrcDirectory);

            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            // prepare assets
            logger.stage("Prepare");

            // create pack builder
            ResourcePackBuilder b = new ResourcePackBuilder(this, packSrcDirectory);

            // add pack builder services
            b.addService(new JsonPackService(b));
            b.addService(new MinecraftAssetService(b));

            // collect asset building services
            ArrayList<AssetPreparingService> serviceList = new ArrayList<>();
            for (Registry<?> reg : main.getRegistries())
                reg.getServicesOf(AssetPreparingService.class,
                        serviceList);

            logger.debugc("Found " + serviceList.size() + " asset providing registry services.");
            logger.debugc("Preparing assets.");

            // prepare assets from services
            for (AssetPreparingService s : serviceList)
                s.prepareAssets(b);

            // copy assets from mods
            for (LoadedMod mod : main.getModLoader().getMods()) {
                // get mod class
                Class<?> modClass = mod.getPluginClass();

                // add builder
                b.asset(new PackElementBuilder(b) {

                    @Override
                    public void write() {
                        IOUtil.copyFromJar(
                                modClass,
                                "/assets/" + mod.getId(),
                                b.srcDir.resolve("assets/" + mod.getId())
                        );
                    }

                    @Override
                    public void read() { }

                }).setName("CopyAssets(" + mod.getId() + ")");
            }

            // prepare mcmeta
            b.asset(new PackMetaBuilder(b))
                    .setDescription(ChatColor.DARK_GRAY + "Generated by " + CarbonBranding.PREFIX +
                            ChatColor.WHITE + " for " + ChatColor.YELLOW + modCount +
                            ChatColor.WHITE + " mods.")
                    .setPackFormat(main.getPlatform().getResourcePackProxy().getPackFormat()
            );

            // prepare copy pack.png
            b.asset(new CopyAssetBuilder(b, PackResource.of("packpng", p -> p.resolve("pack.png"))))
                    .setSource(ResourceHandle.ofModuleResource("/carbon/content.pack/pack.png"));


            logger.debugc("Prepared " + b.assets.size() + " assets.");
            logger.debugc("Building pack on " + builderConfig.getConfiguration().threads + " threads.");

            BiConsumer<Process<PackElementBuilder>, PackElementBuilder> worker =
                    (proc, builder) -> {
                // get file path
                logger.debugc(() -> prefixwt("Building resource " + ChatColor.RED + builder.getName()));
                try {
                    builder.write();
                } catch (Exception e) {
                    // log error
                    logger.err(prefixwt("Error while building " + builder.getName() + ": " + e));
                    e.printStackTrace();
                }
            };

            // the starting time
            AtomicLong t1 = new AtomicLong();

            // create process
            Process<PackElementBuilder> process = main.getProcessManager()
                    .process(worker);

            // add tasks
            process.addTasks(
                    // collect the start time
                    // download minecraft jar
                    new SyncTask<PackElementBuilder, Process<PackElementBuilder>>().runnable((proc, __) -> {
                        t1.set(System.currentTimeMillis());

                        // get resource pack proxy
                        ResourcePackProxy rpp = main.getPlatform().getResourcePackProxy();

                        // check if assets were already downloaded
                        Path assetsFolder = rpp.getMinecraftAssetsFolder(packDir);
                        if (Files.exists(assetsFolder) &&
                                // check revision
                                Objects.equals(IOUtil.readFileToUtf8(assetsFolder.resolve(ASSET_REVISION_FN)), Integer.toString(rpp.getAssetsRevision())))
                            return;

                        logger.info("Downloading Minecraft assets. This only needs to run once per version.");

                        try {
                            OutputStream os;
                            InputStream  is;

                            // get minecraft jar url
                            URL url = new URL(rpp.getMinecraftJarUrl());

                            // get temp file
                            Path tempJar = rpp.getMinecraftJarFile(packDir);
                            if (!Files.exists(tempJar)) {
                                if (!Files.exists(tempJar.getParent()))
                                    Files.createDirectories(tempJar.getParent());
                                Files.createFile(tempJar);

                                // open streams and write
                                os = Files.newOutputStream(tempJar);
                                is = url.openStream();

                                is.transferTo(os);

                                os.close();
                                is.close();
                            }

                            // create assets folder
                            if (Files.exists(assetsFolder))
                                IOUtil.deleteDirectory(assetsFolder);
                            Files.createDirectories(assetsFolder);

                            // assign assets folder
                            MinecraftAssetService mcAssetService = b.getService(MinecraftAssetService.class);
                            mcAssetService.setAssetsPath(assetsFolder);

                            // write .rev file
                            Path revFile = assetsFolder.resolve(ASSET_REVISION_FN);

                            os = Files.newOutputStream(revFile);
                            os.write(String.valueOf(rpp.getAssetsRevision()).getBytes(StandardCharsets.UTF_8));
                            os.close();

                            // unzip assets
                            JarInputStream jis = new JarInputStream(Files.newInputStream(tempJar));
                            Path mcAssets = assetsFolder.resolve("assets/minecraft");
                            Files.createDirectories(mcAssets);

                            ZipEntry entry;
                            while ((entry = jis.getNextEntry()) != null) {
                                String n = entry.getName();
                                if (!mcAssetService.shouldExtract(n))
                                    continue;

                                // create file on disk
                                Path assetFile = assetsFolder.resolve(n);
                                if (!Files.exists(assetFile.getParent()))
                                    Files.createDirectories(assetFile.getParent());
                                Files.createFile(assetFile);

                                // read entry and write to file
                                OutputStream afos = Files.newOutputStream(assetFile);
                                IOUtil.extractFile(jis, afos);

                            }

                            jis.close();
                        } catch (Exception e) {
                            // TODO: better error handling
                            e.printStackTrace();
                            return;
                        }

                    }),

                    // build all assets using worker
                    new ParallelTask<PackElementBuilder, Process<PackElementBuilder>>()
                            .threads(builderConfig.getConfiguration().threads)
                            .joined(true)
                            .addWork(b.assets),

                    // package and print information
                    new SyncTask<PackElementBuilder, Process<PackElementBuilder>>().runnable((proc, __) -> {
                        // debug
                        long tms1 = System.currentTimeMillis() - t1.get();
                        logger.debugc("Built unpacked resource pack in " + tms1 + "ms");

                        // package resource pack
                        logger.info("Packaging resource pack");
//                        IOUtil.zipFilesInFolder(packSrcDirectory, packPkgFile);
                        ZipUtil.pack(
                                packSrcDirectory.toFile(),
                                packPkgFile.toFile()
                        );

                        // print info
                        long tms = System.currentTimeMillis() - t1.get();
                        logger.info("Built and packaged resource pack in " + tms + "ms");

                        // call future
                        future.complete(this);

                    })
            );

            // set up executor service
            QueuedTickExecutionService service = new QueuedTickExecutionService(main.getProcessManager());
            QueuedTickExecutionService.TickLoop tickLoop = service.tickLoop(true, null);

            // set up and run process
            logger.stage("Build");
            process
                    .whenDone(tickLoop::end) // end tick loop when finished
                    .run(service);

            // run the tick loop
            tickLoop.run();

        }, "Carbon.ResourcePackBuilder").start();

        return future;

    }

    public ResourcePackManager startHost() {

        // start server
        logger.stage("Host");
        logger.info("Starting resource pack host provider.");

        try {

            String method = hostConfig.getConfiguration().strategy.toLowerCase();
            switch (method) {
                case "http" -> hostServer = new PackHostServer(this);
                case "filebin" -> hostServer = new FilebinPackHost(this, true);
                default -> {
                    logger.err("Invalid host provider method: " + method.toUpperCase());
                    return this;
                }
            }

            logger.debugc("Using host provider method: " + method.toUpperCase());

            hostServer.host(packPkgFile, 0);
            hostServer.start();
            hostServer.sendPackToAllPlayers(0, true);

        } catch (Exception e) {
            logger.err("Error occurred while starting pack host: " + e);
            e.printStackTrace();
        }

        return this;

    }

    ///////////////////////////////////////////////////

    class BuilderConfig extends AbstractConfiguration {

        public BuilderConfig(Configurable<?> configurable) {
            super(configurable);
        }

        @Configure
        public int threads;

    }

    class HostConfig extends AbstractConfiguration {

        public HostConfig(Configurable<?> configurable) {
            super(configurable);
        }

        @Configure
        public String strategy;

    }

    ///////////////////////////////////////////////////

    private String prefixwt(String s) {
        return ChatColor.DARK_GRAY + "{ " + Thread.currentThread().getName() + " }" + ChatColor.WHITE + " " + s;
    }

}
