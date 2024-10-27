package wtf.n1zamu.presents.database.object;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SerializableAs(value = "present")
public class Present implements ConfigurationSerializable {
    private List<String> players;
    private String worldName, command;
    private int x, y, z;

    public Present(List<String> players, String worldName, String command, int x, int y, int z) {
        this.players = players;
        this.worldName = worldName;
        this.command = command;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("command", this.command);
        map.put("worldName", this.worldName);
        map.put("players", this.players);
        map.put("x", this.x);
        map.put("y", this.y);
        map.put("z", this.z);
        return map;
    }

    public static Present deserialize(Map<String, Object> map) {
        String command = (String) map.get("command");
        String world = (String) map.get("worldName");
        List<String> players = (List<String>) map.get("players");
        int x = (int) map.get("x");
        int y = (int) map.get("y");
        int z = (int) map.get("z");
        return new Present(players, world, command, x, y, z);
    }
}
