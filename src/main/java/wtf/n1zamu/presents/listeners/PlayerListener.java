package wtf.n1zamu.presents.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.scheduler.BukkitTask;
import wtf.n1zamu.presents.NPresents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerListener implements Listener {
    public static List<String> players = new ArrayList<>();
    public static Map<String, Integer> playerTime = new HashMap<>();
    private final Map<String, BukkitTask> playerTasks = new HashMap<>();
    private final boolean isCooldownOnJoin = NPresents.getInstance().getConfig().getBoolean("cooldownForNew");
    private final boolean isReloadOnQuit = NPresents.getInstance().getConfig().getBoolean("reloadOnQuit");
    private final int cooldownTime = NPresents.getInstance().getConfig().getInt("timeForBreak");
    private final JavaPlugin plugin;

    public PlayerListener() {
        this.plugin = NPresents.getInstance();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!isCooldownOnJoin) return;
        if (player.hasPermission("nPresent.bypass")) return;
        if (players.contains(player.getName())) return;

        playerTime.put(player.getName(), cooldownTime);

        BukkitTask task = playerTasks.get(player.getName());
        if (task != null) {
            task.cancel();
        }
        task = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            if (!playerTime.containsKey(player.getName())) {
                playerTasks.get(player.getName()).cancel();
                playerTasks.remove(player.getName());
                return;
            }

            int timeLeft = playerTime.get(player.getName());

            if (timeLeft == 0) {
                playerTime.remove(player.getName());
                players.add(player.getName());
                return;
            }

            if (Bukkit.getOnlinePlayers().contains(player)) {
                playerTime.put(player.getName(), timeLeft - 1);
            }
        }, 0, 20L);
        playerTasks.put(player.getName(), task);
    }


    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!isReloadOnQuit || !isCooldownOnJoin) return;
        if (player.hasPermission("nPresent.bypass")) return;
        if (!players.contains(player.getName())) return;
        players.remove(player.getName());
    }
}
