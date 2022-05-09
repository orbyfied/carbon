package net.orbyfied.carbon.user;

import net.orbyfied.carbon.bootstrap.CarbonReport;
import net.orbyfied.carbon.command.Context;
import net.orbyfied.carbon.command.Node;
import net.orbyfied.carbon.command.annotation.BaseCommand;
import net.orbyfied.carbon.command.annotation.CommandParameter;
import net.orbyfied.carbon.command.annotation.Subcommand;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.file.Path;

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

    @Subcommand("creative") // creativeinv subcommand
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

    @Subcommand("debug dumpreport <system:string path>")
    public void debugListServices(Context ctx, Node node,

                                  @CommandParameter("path") String p) {

        Path path = null;
        if (p != null)
            path = Path.of(p);

        CarbonReport r = CarbonReport.reportFile(path)
                .setTime()
                .setMessage("Requested dump report by " + ctx.getSender() + " using '/carbon debug dumpReport'")
                .write()
                .close();

        Path realFile = r.getProperty("--file");

        CommandSender sender = ctx.getSender();
        sender.sendMessage(ChatColor.WHITE + "Generated dump report at: " +
                ChatColor.AQUA + realFile.toAbsolutePath());

    }

}
