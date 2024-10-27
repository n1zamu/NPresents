package wtf.n1zamu.presents.util;

import org.bukkit.ChatColor;
import wtf.n1zamu.presents.NPresents;

public class ConfigUtil {
    public static String getColoredByPath(String path) {
        return ChatColor.translateAlternateColorCodes('&', NPresents.getInstance().getConfig().getString(path));
    }
    public static String getRawByPath(String path) {
        return NPresents.getInstance().getConfig().getString(path);
    }
}
