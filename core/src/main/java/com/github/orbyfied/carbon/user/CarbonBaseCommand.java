package com.github.orbyfied.carbon.user;

import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.Node;
import com.github.orbyfied.carbon.command.annotation.BaseCommand;
import com.github.orbyfied.carbon.command.annotation.Subcommand;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@BaseCommand(name = "carbon")
public class CarbonBaseCommand {

    private final CarbonUserEnvironment userEnv;

    public CarbonBaseCommand(CarbonUserEnvironment env) {
        this.userEnv = env;
    }

    @Subcommand("info") // info subcommand
    public void info(Context ctx, Node node) {

        CommandSender sender = ctx.getSender();

    }

    @Subcommand("creativeinv") // creativeinv subcommand
    public void creativeInventory(Context ctx, Node node) {

        CommandSender sender = ctx.getSender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return;
        }

        Player player = (Player) sender;
        if (player.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "You need to be in creative to use this command.");
            return;
        }

        userEnv.getCreativeInventoryFactory()
                .create(player)
                .initialize()
                .open(player);

    }

}
