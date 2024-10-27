package wtf.n1zamu.presents.command;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;
import wtf.n1zamu.presents.command.impl.CommandClear;
import wtf.n1zamu.presents.command.impl.CommandEdit;
import wtf.n1zamu.presents.command.impl.CommandGive;
import wtf.n1zamu.presents.command.impl.CommandReload;
import org.jetbrains.annotations.NotNull;
import wtf.n1zamu.presents.util.ConfigUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Command implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) return false;
        Player p = (Player) sender;
        if (args.length == 0) {
            p.sendMessage(ConfigUtil.getColoredByPath("messages.usage"));
            return false;
        }
        Arrays.asList(new CommandGive(), new CommandReload(), new CommandEdit(), new CommandClear()).forEach(iCommand -> {
            if (args[0].equalsIgnoreCase(iCommand.getName())) iCommand.execute(p, args);
        });
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NonNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NonNull String alias, @NonNull String[] args) {
        List<String> commands = new ArrayList<>();
        Arrays.asList(new CommandGive(), new CommandReload(), new CommandClear(), new CommandEdit()).forEach(iCommand -> commands.add(iCommand.getName()));
        if (args.length == 0) return commands;
        if (args[0].equalsIgnoreCase("give")) {
            List<String> playerNames = new ArrayList<>();
            Bukkit.getOnlinePlayers().forEach(player -> playerNames.add(player.getName()));
            if (args.length == 2) {
                return playerNames;
            }
            if (args.length >= 3) {
                return Collections.singletonList("<command>");
            }
        }
        if (Arrays.asList("reload", "edit", "clear").contains(args[0]))
            return Collections.emptyList();
        return commands;
    }
}
