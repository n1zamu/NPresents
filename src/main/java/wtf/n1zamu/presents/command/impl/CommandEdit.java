package wtf.n1zamu.presents.command.impl;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import wtf.n1zamu.presents.command.ICommand;
import wtf.n1zamu.presents.util.ConfigUtil;

import java.util.ArrayList;
import java.util.List;

public class CommandEdit implements ICommand {
    public static List<String> players = new ArrayList<>();

    @Override
    public String getName() {
        return "edit";
    }

    @Override
    public void execute(@NotNull Player player, @NotNull String[] args) {
        if (!player.hasPermission("nPresent.edit") && !player.hasPermission("nPresent.admin")) {
            player.sendMessage(ConfigUtil.getColoredByPath("messages.noPerm"));
            return;
        }

        if (args.length != 1) return;
        if (players.contains(player.getName())) {
            player.sendMessage(ConfigUtil.getColoredByPath("messages.editCanceled"));
            players.remove(player.getName());
        } else {
            player.sendMessage(ConfigUtil.getColoredByPath("messages.editEnabled"));
            players.add(player.getName());
        }
    }
}
