package carbon.test;

import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.Node;
import com.github.orbyfied.carbon.command.annotation.BaseCommand;
import com.github.orbyfied.carbon.command.annotation.CommandParameter;
import com.github.orbyfied.carbon.command.annotation.SubInitializer;
import com.github.orbyfied.carbon.command.annotation.Subcommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@BaseCommand(name = "test")
public class MyCommand {

    @Subcommand("sussy <minecraft:online_player_direct player>")
    public void sussy(Context ctx, Node cmd,

                      @CommandParameter("player") Player player) {
        player.sendMessage(ChatColor.WHITE + "Sussy Baka " + ChatColor.GOLD + "you are");
    }

    @Subcommand("sussy <minecraft:online_player_direct player> <system:int sus> baka")
    public void baka(Context ctx, Node cmd,

                     @CommandParameter("player") Player player,
                     @CommandParameter("sus") Integer sus) {
        player.sendMessage(ChatColor.WHITE + "Sussy Number: " + ChatColor.GOLD + sus);
    }

    @SubInitializer
    public void sussy(Node cmd) {
        System.out.println("INITIALIZED: " + cmd.getName());
    }

}
