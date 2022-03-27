package carbon.test;

import com.github.orbyfied.carbon.command.Context;
import com.github.orbyfied.carbon.command.Node;
import com.github.orbyfied.carbon.command.annotation.BaseCommand;
import com.github.orbyfied.carbon.command.annotation.CommandParameter;
import com.github.orbyfied.carbon.command.annotation.SubInitializer;
import com.github.orbyfied.carbon.command.annotation.Subcommand;

@BaseCommand(name = "test")
public class MyCommand {

    @Subcommand("sussy <system:string s>")
    public void sussy(Context ctx, Node cmd, @CommandParameter String s) {
        ctx.getSender().sendMessage("fucking string: " + s);
    }

    @SubInitializer
    public void sussy(Node cmd) {
        System.out.println("INITIALIZED: " + cmd.getName());
    }

}
