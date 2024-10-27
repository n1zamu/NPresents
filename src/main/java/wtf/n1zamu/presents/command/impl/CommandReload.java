package wtf.n1zamu.presents.command.impl;

import org.bukkit.entity.Player;
import wtf.n1zamu.presents.NPresents;
import wtf.n1zamu.presents.command.ICommand;
import org.jetbrains.annotations.NotNull;
import wtf.n1zamu.presents.util.ConfigUtil;

public class CommandReload implements ICommand {
    public String getName() {
        return "reload";
    }

    public void execute(@NotNull Player player, @NotNull String[] args) {
        if (!player.hasPermission("nPresent.reload") && !player.hasPermission("nPresent.admin")) {
            player.sendMessage(ConfigUtil.getColoredByPath("messages.noPerm"));
            return;
        }
        if (args.length != 1) return;
        NPresents.getInstance().reloadConfig();
        player.sendMessage(ConfigUtil.getColoredByPath("messages.reloaded"));
    }
}
