package carbon.test;

import com.github.orbyfied.carbon.api.CarbonModAPI;
import com.github.orbyfied.carbon.api.mod.CarbonMod;
import com.github.orbyfied.carbon.api.mod.CarbonModDescription;
import com.github.orbyfied.carbon.api.mod.CarbonModInitializer;
import com.github.orbyfied.carbon.core.mod.LoadedMod;
import com.github.orbyfied.carbon.item.CarbonItem;
import com.github.orbyfied.carbon.item.CarbonItemState;
import com.github.orbyfied.carbon.registry.Identifier;
import com.github.orbyfied.carbon.registry.Registry;
import org.bukkit.plugin.java.JavaPlugin;

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
}
