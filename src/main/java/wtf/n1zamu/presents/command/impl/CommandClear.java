package wtf.n1zamu.presents.command.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wtf.n1zamu.presents.NPresents;
import wtf.n1zamu.presents.command.ICommand;
import wtf.n1zamu.presents.util.ConfigUtil;

public class CommandClear implements ICommand {
    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] args) {
        if (!player.hasPermission("nPresent.clear") && !player.hasPermission("nPresent.admin")) {
            player.sendMessage(ConfigUtil.getColoredByPath("messages.noPerm"));
            return;
        }

        if (args.length != 1) return;
        player.sendMessage(ConfigUtil.getColoredByPath("messages.successfullyCleared"));
        NPresents.getInstance().getDataBase().clear();
    }
}
