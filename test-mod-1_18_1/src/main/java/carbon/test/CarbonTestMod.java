package carbon.test;

import com.github.orbyfied.carbon.api.CarbonModAPI;
import com.github.orbyfied.carbon.api.mod.CarbonMod;
import com.github.orbyfied.carbon.api.mod.CarbonModDescription;
import com.github.orbyfied.carbon.api.mod.CarbonModInitializer;
import com.github.orbyfied.carbon.config.*;
import com.github.orbyfied.carbon.core.mod.LoadedMod;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.registry.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Path;

@CarbonMod(id = "test", name = "Test", version = "1.0.0")
//hello world. This code is Dylan the Spence Aproved 10%
@CarbonModDescription("Hello guys!")
public class CarbonTestMod extends JavaPlugin implements CarbonModInitializer {

    @Override
    public void modLoaded(LoadedMod mod) {
        getLogger().info("------- LOADING");
    }

    @Override
    public void modInitialize(CarbonModAPI api) {
        CarbonItem<CarbonItemState<?>> item = new CarbonItem<>(Identifier.of("among:us"));

        api.getEnvironmentAPI().getRegistries()
                .<Registry<CarbonItem<?>>>getByIdentifier("minecraft:items")
                .register(item);
    }

    @Override
    public void onEnable() {
        // run tests
        try {

            for (Method m : this.getClass().getDeclaredMethods()) {
                if (!m.isAnnotationPresent(Test.class)) continue;
                m.setAccessible(true);
                getLogger().info("(T) Running test " + m.getName());
                m.invoke(this);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Path getDFFile(String p) {
        return new File(getDataFolder(), p).toPath();
    }

    /* ---- Tests: Configuration (1) ---- */

    @Test
    public void testConfig1() {
        MyConfigurable  c  = new MyConfigurable();
        MyConfigurable2 c2 = new MyConfigurable2();

        ConfigurationHelper<YamlConfiguration> helper =
                ConfigurationHelper.newYamlFileConfiguration(getDFFile("config.yml"), "/config.yml")
                .child("hello.guys", () -> ConfigurationHelper.newYamlFileConfiguration(getDFFile("hello.yml"), "/hello-defaults.yml"));

        helper.addConfigurable(c).addConfigurable(c2);
        helper.load();

        System.out.println(c.getConfiguration().myInt);
        System.out.println(c2.getConfiguration().hello);
    }

    static class MyConfigurable2 implements Configurable<MyConfigurable2.Config> {

        public static class Config extends AbstractConfiguration {

            public Config(Configurable<?> configurable) {
                super(configurable);
            }

            @Configure
            public String hello = "hi!";

        }

        final MyConfigurable2.Config cfg = new MyConfigurable2.Config(this);

        @Override
        public String getConfigurationPath() {
            return "hello.guys/my.configuration2";
        }

        @Override
        public MyConfigurable2.Config getConfiguration() {
            return cfg;
        }

    }

    static class MyConfigurable implements Configurable<MyConfigurable.Config> {

        public static class Config extends AbstractConfiguration {
            @Configure
            @Comment("Fuck.")
            public int myInt = 9;

            public Config(Configurable<?> configurable) {
                super(configurable);
            }
        }

        final Config cfg = new Config(this);

        @Override
        public String getConfigurationPath() {
            return "my.configuration";
        }

        @Override
        public Config getConfiguration() {
            return cfg;
        }

    }

}
