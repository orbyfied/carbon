package carbon.test;

import com.github.orbyfied.carbon.api.CarbonModAPI;
import com.github.orbyfied.carbon.api.mod.CarbonMod;
import com.github.orbyfied.carbon.api.mod.CarbonModDescription;
import com.github.orbyfied.carbon.api.mod.CarbonModInitializer;
import com.github.orbyfied.carbon.core.mod.LoadedMod;
import org.bukkit.plugin.java.JavaPlugin;

@CarbonMod(id = "test", name = "Test", version = "1.0.0")
@CarbonModDescription("Hello guys!")
public class CarbonTestMod extends JavaPlugin implements CarbonModInitializer {

    @Override
    public void modLoaded(LoadedMod mod) {
        System.out.println("hi loading");
    }

    @Override
    public void modInitialize(CarbonModAPI api) {
        System.out.println("hi initialization");
    }

}
