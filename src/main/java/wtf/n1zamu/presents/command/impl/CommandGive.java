package wtf.n1zamu.presents.command.impl;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import wtf.n1zamu.presents.NPresents;
import wtf.n1zamu.presents.command.ICommand;
import org.jetbrains.annotations.NotNull;
import wtf.n1zamu.presents.util.ConfigUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CommandGive implements ICommand {
    public String getName() {
        return "give";
    }

    public void execute(@NotNull Player player, @NotNull String[] args) {
        if (!player.hasPermission("nPresent.give") && !player.hasPermission("nPresent.admin")) {
            player.sendMessage(ConfigUtil.getColoredByPath("messages.noPerm"));
            return;
        }

        if (args.length < 3) {
            player.sendMessage(ConfigUtil.getColoredByPath("messages.giveUsage"));
            return;
        }
        String[] remainingArgs = Arrays.copyOfRange(args, 2, args.length);

        String result = String.join(" ", remainingArgs);
        ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta customMeta = (SkullMeta) itemStack.getItemMeta();

        if (customMeta != null) {
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
            gameProfile.getProperties().put("textures", new Property("textures", NPresents.getInstance().getConfig().getString("present.texture")));
            try {
                Method method = customMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                method.setAccessible(true);
                method.invoke(customMeta, gameProfile);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
                ex.printStackTrace();
            }

            customMeta.addEnchant(Enchantment.DURABILITY, 1, false);
            customMeta.setDisplayName(ConfigUtil.getColoredByPath("present.name"));

            List<String> loreList = NPresents.getInstance().getConfig().getStringList("present.lore");
            List<String> coloredLore = new ArrayList<>(loreList.size());
            loreList.forEach(line -> coloredLore.add(ChatColor.translateAlternateColorCodes('&', line.replace("%command%", result))));
            customMeta.setLore(coloredLore);
            customMeta.getPersistentDataContainer().set(NPresents.getInstance().getKEY(), PersistentDataType.STRING, result);
        }

        Player p = Bukkit.getPlayerExact(args[1]);
        if (p == null || !p.isOnline()) return;

        itemStack.setItemMeta(customMeta);
        itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        player.getInventory().addItem(itemStack);
    }
}

