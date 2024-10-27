package wtf.n1zamu.presents.database.impl;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.n1zamu.presents.NPresents;
import wtf.n1zamu.presents.database.IDataBase;
import wtf.n1zamu.presents.database.object.Present;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YAMLDatabase implements IDataBase {
    private final JavaPlugin plugin;
    private File dataFile;
    private FileConfiguration dataConfig;
    private List<Present> presents = new ArrayList<>();

    public YAMLDatabase() {
        this.plugin = NPresents.getInstance();
    }

    @Override
    public void connect() {
        dataFile = new File(plugin.getDataFolder(), "presents.yml");
        if (!dataFile.exists()) {
            dataFile.getParentFile().mkdirs();
            try {
                dataFile.createNewFile();
                dataConfig = YamlConfiguration.loadConfiguration(this.dataFile);
                dataConfig.set("presents", new ArrayList<>());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(this.dataFile);

        if (dataConfig.getList("presents") != null) {
            List<Map<String, Object>> presentMaps = (List<Map<String, Object>>) dataConfig.getList("presents");
            List<Present> presents = new ArrayList<>();
            for (Map<String, Object> presentMap : presentMaps) {
                Present clan = Present.deserialize(presentMap);

                presents.add(clan);
            }
            this.presents = presents;
        }
    }

    @Override
    public void disconnect() {
        try {
            if (presents.isEmpty()) {
                dataConfig.save(dataFile);
                return;
            }
            List<Map<String, Object>> presentMap = new ArrayList<>();
            for (Present present : presents) {
                presentMap.add(present.serialize());
            }
            dataConfig.set("presents", presentMap);
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clear() {
        presents.clear();
    }

    @Override
    public int getTotalPresents() {
        return presents.size();
    }

    @Override
    public int getPlayerPresents(Player player) {
        int playerPresents = 0;
        for (Present present : presents) {
            if (present.getPlayers().contains(player.getName()))
                playerPresents += 1;
        }
        return playerPresents;
    }

    @Override
    public String getCommand(Location location) {
        for (Present present : presents) {
            if (location.getBlock().getX() == present.getX()
                    && location.getBlock().getY() == present.getY()
                    && location.getBlock().getZ() == present.getZ()) {
                return present.getCommand();
            }
        }
        return "";
    }

    @Override
    public void addCommand(Location location, String command) {
        presents.forEach(present -> {
            if (location.getBlock().getX() == present.getX()
                    && location.getBlock().getY() == present.getY()
                    && location.getBlock().getZ() == present.getZ()) {
                present.setCommand(command);
            }
        });
    }

    @Override
    public void addPresent(Location location, Player player) {
        for (Present present : presents) {
            if (location.getBlock().getX() == present.getX()
                    && location.getBlock().getY() == present.getY()
                    && location.getBlock().getZ() == present.getZ()) {
                present.getPlayers().add(player.getName());
            }
        }
    }

    @Override
    public void removePresent(Location location) {
        presents.removeIf(present -> location.getBlock().getX() == present.getX()
                && location.getBlock().getY() == present.getY()
                && location.getBlock().getZ() == present.getZ());
    }

    @Override
    public boolean isPresentCollected(Location location, Player player) {
        for (Present present : presents) {
            if (location.getBlock().getX() == present.getX()
                    && location.getBlock().getY() == present.getY()
                    && location.getBlock().getZ() == present.getZ()
                    && present.getPlayers().contains(player.getName())) {
                return true;
            }
        }
        return false;
    }
}
