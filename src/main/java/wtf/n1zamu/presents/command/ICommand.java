package wtf.n1zamu.presents.command;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ICommand {
   String getName();

   void execute(@NotNull Player player, @NotNull String[] args);
}
