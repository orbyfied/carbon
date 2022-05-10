package net.orbyfied.carbon.user;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.command.annotation.BaseAnnotationProcessor;
import net.orbyfied.carbon.logging.BukkitLogger;

/**
 * Class for managing the user/administrator features
 * of Carbon and providing an environment for them.
 */
public class CarbonUserEnvironment {

    /**
     * Main instance reference.
     */
    protected final Carbon main;

    /**
     * The user environment logger.
     */
    protected final BukkitLogger logger;

    public Carbon getMain() {
        return main;
    }

    public BukkitLogger getLogger() {
        return logger;
    }

    public CarbonUserEnvironment(Carbon main) {
        this.main   = main;
        this.logger = main.getLogger("User");
    }

    /* ---- Features ---- */

    private void initFeatures() {
        // creative inventory
        this.creativeInventoryManager = new CreativeInventoryManager(this);
        // carbon command
        new BaseAnnotationProcessor(main.getCommandEngine(), (command = new CarbonBaseCommand(this))).compile().register();
    }

    public void enable() {
        initFeatures();

        main.getCommandEngine().enablePlatform();
        creativeInventoryManager.enable();
    }

    public void disable() {
        main.getCommandEngine().disablePlatform();
        creativeInventoryManager.disable();
    }

    /* Carbon Command */

    protected CarbonBaseCommand command;

    public CarbonBaseCommand getCarbonCommand() {
        return command;
    }

    /* Creative Inventory */

    protected CreativeInventoryManager creativeInventoryManager;

    public CreativeInventoryManager getCreativeInventoryFactory() {
        return creativeInventoryManager;
    }

}
