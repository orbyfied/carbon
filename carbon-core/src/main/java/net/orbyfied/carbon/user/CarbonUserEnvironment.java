package net.orbyfied.carbon.user;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.command.annotation.BaseAnnotationProcessor;

public class CarbonUserEnvironment {

    protected final Carbon main;

    protected final CreativeInventoryFactory creativeInventoryFactory;

    protected final CarbonBaseCommand command;

    public CarbonUserEnvironment(Carbon main) {
        this.main = main;

        new BaseAnnotationProcessor(main.getCommandEngine(), (command = new CarbonBaseCommand(this))).compile().register();
        this.creativeInventoryFactory = new CreativeInventoryFactory(this);
    }

    public void enable() {
        main.getCommandEngine().enablePlatform();
        creativeInventoryFactory.enable();
    }

    public void disable() {
        main.getCommandEngine().disablePlatform();
        creativeInventoryFactory.disable();
    }

    public Carbon getMain() {
        return main;
    }

    public CarbonBaseCommand getCarbonCommand() {
        return command;
    }

    public CreativeInventoryFactory getCreativeInventoryFactory() {
        return creativeInventoryFactory;
    }

}
