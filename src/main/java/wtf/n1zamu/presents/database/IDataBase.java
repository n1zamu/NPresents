package wtf.n1zamu.presents.database;

import org.bukkit.Location;
import org.bukkit.entity.Player;


public interface IDataBase {
    void connect();

    void disconnect();

    void clear();
    int getTotalPresents();
    int getPlayerPresents(Player player);

    String getCommand(Location location);

    void addCommand(Location location, String command);

    void addPresent(Location location, Player player);

    void removePresent(Location location);

    boolean isPresentCollected(Location location, Player player);
}
