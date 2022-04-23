package com.github.orbyfied.carbon.bootstrap;

import com.github.orbyfied.carbon.Carbon;
import com.github.orbyfied.carbon.content.CMDRegistryService;
import com.github.orbyfied.carbon.core.CarbonJavaAPI;
import com.github.orbyfied.carbon.core.Service;
import com.github.orbyfied.carbon.core.ServiceManager;
import com.github.orbyfied.carbon.crafting.Recipe;
import com.github.orbyfied.carbon.crafting.type.RecipeType;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.registry.Registry;
import com.github.orbyfied.carbon.util.IOUtil;
import com.github.orbyfied.carbon.util.functional.Verbose;
import org.bukkit.Bukkit;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CarbonReport {

    protected List<Writer> outputs = new ArrayList<>();
    protected boolean hasVerbose = false;

    protected Date time;
    protected String message;
    protected List<Throwable> errors = new ArrayList<>();
    protected String details;

    protected Map<String, Object> properties = new HashMap<>();

    public CarbonReport withOutput(Writer w) {
        outputs.add(w);
        if (w instanceof Verbose)
            hasVerbose = true;
        return this;
    }

    public Date getTime() {
        return time;
    }

    public CarbonReport setTime() {
        time = new Date();
        return this;
    }

    public CarbonReport setTime(Date date) {
        time = date;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public CarbonReport setMessage(String ms) {
        message = ms;
        return this;
    }

    public String getDetails() {
        return details;
    }

    public CarbonReport setDetails(String d) {
        details = d;
        return this;
    }

    public CarbonReport setProperty(String key, Object val) {
        properties.put(key, val);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T getProperty(String key) {
        return (T) properties.get(key);
    }

    public List<Throwable> getErrors() {
        return errors;
    }

    public CarbonReport setErrors(List<Throwable> errs) {
        errors = errs;
        return this;
    }

    public CarbonReport setErrors(Throwable... errs) {
        errors = new ArrayList<>(Arrays.asList(errs));
        return this;
    }

    public CarbonReport withError(Throwable err) {
        errors.add(err);
        return this;
    }

    public CarbonReport withErrors(Throwable... errs) {
        errors.addAll(Arrays.asList(errs));
        return this;
    }

    public StringBuilder createString(StringBuilder b) {
        b.append("===== Report Overview =====\n");
        b.append("Installation: Carbon v")
                .append(Carbon.VERSION)
                .append(" @ ")
                .append(Bukkit.getVersion())
                .append("\n");
        b.append("\n");
        b.append("Message: ").append(message).append("\n");
        b.append("DateTime: ").append(time).append("\n\n");
        if (details != null)
            b.append(details).append("\n\n");
        if (errors != null && errors.size() != 0) {
            b.append("Errors:\n");
            for (Throwable t : errors) {
                StringWriter stw = new StringWriter();
                t.printStackTrace(new PrintWriter(stw));
                String stacktrace = stw.toString();
                b.append(stacktrace).append("\n");
            }
        }

        return b;
    }

    public StringBuilder createStringVerbose(StringBuilder b) {
        createString(b).append("\n");
        // append properties
        if (properties.size() != 0) {
            b.append("\nProperties:\n");
            for (Map.Entry<String, Object> entry : properties.entrySet())
                if (!entry.getKey().startsWith("--"))
                    b.append("- ").append(entry.getKey()).append(": ").append(entry.getValue());
            b.append("\n");
        }

        dumpInfo(b).append("\n");
        return b;
    }

    public CarbonReport write() {
        String simple = createString(new StringBuilder()).toString();
        String verbose = null;
        if (hasVerbose)
            verbose = createStringVerbose(new StringBuilder()).toString();
        for (Writer w : outputs) {
            if (w instanceof Verbose) {
                try {
                    w.append(verbose);
                    w.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    w.append(simple);
                    w.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return this;
    }

    public CarbonReport close() {
        for (Writer w : outputs) {
            try {
                w.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    public CarbonReport crash() {
        Bukkit.shutdown();
        return this;
    }

    public static final Writer STDOUT_WRITER = new PrintWriter(System.out);

    public static CarbonReport reportFileAndStdout() {
        Date now = new Date();
        Path file = Path.of("./logs/carbon-report-" +
                IOUtil.toIOFriendlyString(now) + ".log");
        return reportFileAndStdout(file).setTime(now);
    }

    public static CarbonReport reportFileAndStdout(Path file) {
        return reportFile(file).withOutput(STDOUT_WRITER);
    }

    public static CarbonReport reportFile() {
        Date now = new Date();
        Path file = Path.of("./logs/carbon-report-" +
                IOUtil.toIOFriendlyString(now) + ".log");
        return reportFile(file).setTime(now);
    }



    public static CarbonReport reportFile(Path file) {
        if (file == null)
            return reportFile();
        Writer fileWriter;
        try {
            if (file.getParent() != null)
                if (!Files.exists(file.getParent()))
                    Files.createDirectories(file.getParent());
            if (!Files.exists(file))
                Files.createFile(file);
            OutputStream os = Files.newOutputStream(file);
            fileWriter = new Verbose.VerboseOutputStreamWriter(os);
        } catch (Exception e) {
            e.printStackTrace();
            return new CarbonReport();
        }
        return new CarbonReport() {
            @Override
            public StringBuilder createString(StringBuilder b) {
                b.append("Full Crash Report: ").append(file.toAbsolutePath()).append("\n\n");
                return super.createString(b);
            }
        }
                .withOutput(fileWriter)
                .setProperty("--file", file);
    }

    //////////////////////////////////////////

    public static StringBuilder dumpInfo(StringBuilder b) {
        Carbon main = CarbonJavaAPI.get().getMain();

        // header
        b.append("+ //   CARBON DUMP   //\n");

        // core
        b.append("| @ Carbon v").append(Carbon.VERSION).append(" by orbyfied <https://github.com/orbyfied/carbon>\n");
        b.append("| @ Server: ").append(Bukkit.getVersion()).append("\n");
        b.append("| Init Stage: ").append(main.getInitializationStage()).append("\n");

        // services
        b.append("\n");
        ServiceManager serviceManager = main.getServiceManager();
        b.append("| Services Running: ").append(serviceManager.getAmount()).append("\n");
        for (Service s : serviceManager.getServices()) {
            b.append("|- ")
                    .append(s.getClass().getSimpleName())
                    .append(": ")
                    .append(s)
                    .append("\n");
        }

        // content
        b.append("\n");
        b.append("| Mods Loaded: ").append(main.getModLoader().length()).append("\n");
        b.append("| Registries Loaded: ").append(main.getRegistries().size()).append("\n");
        b.append("| Registries:\n");
        for (Registry<?> reg : main.getRegistries()) {
            b.append("|- ")
                    .append("registry/").append(reg.getIdentifier())
                    .append(" + len: ")
                    .append(reg.size())
                    .append(", servc: ")
                    .append(reg.getServicesSize())
                    .append(", compc: ")
                    .append(reg.getComponentsSize())
                    .append("\n");
        }

        {
            Registry<CarbonItem> itemRegistry = main.getRegistries().getByIdentifier("minecraft:items");
            if (itemRegistry != null) {
                CMDRegistryService<CarbonItem> itemCMDRegistryService = itemRegistry.getService(CMDRegistryService.class);
                b.append("| Item Registry CMD:\n");
                b.append("|- Base Materials Taken: ").append(itemCMDRegistryService.getCMDRegistry().size()).append("\n");
                b.append("|- Total CMDs Taken: ").append(itemCMDRegistryService.getTotalTaken()).append("\n");
            }
        }

        {
            Registry<RecipeType> recipeTypeRegistry = main.getRegistries().getByIdentifier("minecraft:recipe_types");
            if (recipeTypeRegistry != null) {
                b.append("| Recipe Types: ").append(recipeTypeRegistry.size()).append("\n");
                for (RecipeType t : recipeTypeRegistry) {
                    b.append("|- ")
                            .append(t.getIdentifier())
                            .append(": ")
                            .append(t)
                            .append("\n");
                }
            }
            Registry<Recipe> recipeRegistry = main.getRegistries().getByIdentifier("minecraft:recipes");
            if (recipeRegistry != null) {
                b.append("| Recipes Registered: ").append(recipeRegistry.size()).append("\n");
            }
        }
        return b;
    }

}
