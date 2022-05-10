package net.orbyfied.carbon.user;

import net.orbyfied.carbon.Carbon;
import net.orbyfied.carbon.bootstrap.CarbonReport;
import net.orbyfied.carbon.command.Context;
import net.orbyfied.carbon.command.Node;
import net.orbyfied.carbon.command.annotation.BaseCommand;
import net.orbyfied.carbon.command.annotation.CommandParameter;
import net.orbyfied.carbon.command.annotation.Subcommand;
import net.orbyfied.carbon.logging.BukkitLogger;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.nio.file.Path;

/**
 * The base command for Carbon.
 * @see CarbonUserEnvironment
 */
@BaseCommand(name = "carbon")
public class CarbonBaseCommand {

    private final CarbonUserEnvironment userEnv;

    /**
     * Reference to user environment logger.
     */
    private final BukkitLogger LOGGER;

    public CarbonBaseCommand(CarbonUserEnvironment env) {
        this.userEnv = env;
        this.LOGGER = userEnv.getLogger();
    }

    /* ---- Commands ----- */

    @Subcommand({ "info", "" }) // info command
    public void sendInfo(Context ctx, Node node) {

        CommandSender sender = ctx.sender();

        sender.sendMessage("Carbon v" + Carbon.VERSION + " by orbyfied");

    }

    @Subcommand("creative") // creativeinv subcommand
    public void openCreativeInventory(Context ctx, Node node) {

        CommandSender sender = ctx.sender();
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return;
        }

        Player player = (Player) sender;
        if (player.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "You must be in creative to use this command.");
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
                .setMessage("Requested dump report by " + ctx.sender() + " using '/carbon debug dumpReport'")
                .write()
                .close();

        Path realFile = r.getProperty("--file");

        CommandSender sender = ctx.sender();
        sender.sendMessage(ChatColor.WHITE + "Generated dump report at: " +
                ChatColor.AQUA + realFile.toAbsolutePath());

    }

    @Subcommand("configuration reload")
    public void reloadConfiguration(Context context, Node node) {
        CommandSender sender = context.sender();
        LOGGER.stage("Configuration");
        try {
            userEnv.main
                    .getConfigurationHelper()
                    .load();
            LOGGER.okc("Successfully reloaded configuration. Issued by " + ChatColor.WHITE + sender.getName());
            sender.sendMessage(ChatColor.GREEN + "Successfully reloaded configuration!");
        } catch (Exception e) {
            LOGGER.errc("Failed to reload configuration. Issued by " + ChatColor.WHITE + sender.getName());
            sender.sendMessage(ChatColor.RED + "Successfully reloaded configuration!");
            e.printStackTrace();
        }
    }

}
