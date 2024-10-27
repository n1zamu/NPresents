package wtf.n1zamu.presents;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import wtf.n1zamu.presents.command.Command;
import wtf.n1zamu.presents.database.IDataBase;
import wtf.n1zamu.presents.database.impl.SQLiteDataBase;
import wtf.n1zamu.presents.database.impl.YAMLDatabase;
import wtf.n1zamu.presents.listeners.BlockListener;
import wtf.n1zamu.presents.listeners.PlayerListener;

import java.util.Arrays;

public class NPresents extends JavaPlugin {
    private static NPresents INSTANCE;
    private NamespacedKey KEY;
    private String VERSION;
    private IDataBase dataBase;

    public void onEnable() {
        INSTANCE = this;
        saveDefaultConfig();
        KEY = new NamespacedKey("npresents", "item");
        VERSION = this.getDescription().getVersion();
        dataBase = getDataBase(getConfig().getString("database"));
        dataBase.connect();

        Arrays.asList(
                "  §7[§bN§fPresents§7] §fVERSION §a" + VERSION,
                "  §7[§bN§fPresents§7] §fhttps://github.com/n1zamu",
                "  §7[§bN§fPresents§7] §fEnjoy your use! I'd like if you star my repository!"
        ).forEach(line -> Bukkit.getLogger().info(line));
        this.getCommand("nPresent").setExecutor(new Command());
        this.getCommand("nPresent").setTabCompleter(new Command());
        Arrays.asList(new PlayerListener(), new BlockListener())
                .forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    public void onDisable() {
        Bukkit.getLogger().info("§7[§bN§fPresents§7] §fDisabling...");
        dataBase.disconnect();
        BlockListener.ANIMATION_STANDS.forEach(Entity::remove);
    }

    private IDataBase getDataBase(String id) {
        if (id.equals("SQLite")) {
            return new SQLiteDataBase();
        }
        return new YAMLDatabase();
    }

    public NamespacedKey getKEY() {
        return KEY;
    }

    public IDataBase getDataBase() {
        return dataBase;
    }

    public static NPresents getInstance() {
        return INSTANCE;
    }
}
