package wtf.n1zamu.presents.listeners;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import wtf.n1zamu.presents.NPresents;
import wtf.n1zamu.presents.command.impl.CommandEdit;
import wtf.n1zamu.presents.util.ConfigUtil;
import wtf.n1zamu.presents.util.TimeFormatUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BlockListener implements Listener {
    public static List<ArmorStand> ANIMATION_STANDS = new ArrayList<>();
    private final List<Location> RESERVED_FOR_ANIMATIONS = new ArrayList<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        if (location.getBlock().getType() != Material.PLAYER_HEAD) {
            return;
        }
        if (NPresents.getInstance().getDataBase().getCommand(location).isEmpty()) {
            return;
        }
        if (CommandEdit.players.contains(event.getPlayer().getName())) {
            NPresents.getInstance().getDataBase().removePresent(location);
            event.getPlayer().sendMessage(ConfigUtil.getColoredByPath("messages.presentRemoved"));
            return;
        }
        event.setCancelled(true);
        if (NPresents.getInstance().getConfig().getBoolean("cooldownForNew")
                && !event.getPlayer().hasPermission("nPresent.bypass")
                && !PlayerListener.players.contains(event.getPlayer().getName())) {
            if (PlayerListener.playerTime.containsKey(event.getPlayer().getName())) {
                event.getPlayer().sendMessage(ConfigUtil.getColoredByPath("messages.cantBreak").replace("%time%",
                        TimeFormatUtil.getFormattedCooldown(PlayerListener.playerTime.get(event.getPlayer().getName()) * 1000)));
            } else {
                event.getPlayer().sendMessage(ConfigUtil.getColoredByPath("messages.rejoinToStart"));
            }
            return;
        }

        if (NPresents.getInstance().getDataBase().isPresentCollected(location, event.getPlayer())) {
            event.getPlayer().sendMessage(ConfigUtil.getColoredByPath("messages.alreadyCollected")
                    .replace("%totalPresents%", String.valueOf(NPresents.getInstance().getDataBase().getTotalPresents()))
                    .replace("%playerPresents%", String.valueOf(NPresents.getInstance().getDataBase().getPlayerPresents(event.getPlayer()))));
            return;
        }

        if (RESERVED_FOR_ANIMATIONS.contains(location)) return;

        NPresents.getInstance().getDataBase().addPresent(location, event.getPlayer());
        giveReward(event.getPlayer(), location);
    }

    @EventHandler
    public final void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        if (block.getType() != Material.PLAYER_HEAD) {
            return;
        }

        ItemStack itemStack = event.getItemInHand();
        if (!itemStack.hasItemMeta()) {
            return;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        if (!persistentDataContainer.has(NPresents.getInstance().getKEY(), PersistentDataType.STRING)) {
            return;
        }
        String string = persistentDataContainer.get(NPresents.getInstance().getKEY(), PersistentDataType.STRING);
        if (string == null) {
            return;
        }

        if (!event.getPlayer().hasPermission("npresent.give") && !event.getPlayer().hasPermission("npresent.admin")) {
            return;
        }

        Skull skull = (Skull) block.getState();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", ConfigUtil.getRawByPath("present.texture")));

        try {
            Field profileField = skull.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skull, profile);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Bukkit.getLogger().info(e.getCause().getMessage());
        }

        skull.update();
        NPresents.getInstance().getDataBase().addCommand(block.getLocation(), string);
    }

    private void giveReward(Player player, Location location) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), NPresents.getInstance().getDataBase().getCommand(location).replace("%player%", player.getName()));

        if (NPresents.getInstance().getConfig().getBoolean("finalReward")
                && NPresents.getInstance().getDataBase().getTotalPresents() == NPresents.getInstance().getDataBase().getPlayerPresents(player)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), NPresents.getInstance().getConfig().getString("finalRewardCommand").replace("%player%", player.getName()));
            player.sendMessage(ConfigUtil.getColoredByPath("messages.finalRewardMessage"));
            NPresents.getInstance().getConfig().getStringList("messages.finalRewardMessage")
                    .forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line)));
        }

        if (NPresents.getInstance().getConfig().getBoolean("reward.sendTitle")) {
            player.sendTitle(
                    ConfigUtil.getColoredByPath("reward.titleName")
                            .replace("%totalPresents%", String.valueOf(NPresents.getInstance().getDataBase().getTotalPresents()))
                            .replace("%playerPresents%", String.valueOf(NPresents.getInstance().getDataBase().getPlayerPresents(player))),
                    ConfigUtil.getColoredByPath("reward.titleSubname")
                            .replace("%totalPresents%", String.valueOf(NPresents.getInstance().getDataBase().getTotalPresents()))
                            .replace("%playerPresents%", String.valueOf(NPresents.getInstance().getDataBase().getPlayerPresents(player))));
        }
        if (NPresents.getInstance().getConfig().getBoolean("reward.sendMessage")) {
            if (NPresents.getInstance().getDataBase().getTotalPresents() != NPresents.getInstance().getDataBase().getPlayerPresents(player)) {
                NPresents.getInstance().getConfig().getStringList("reward.message").forEach(line -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', line
                        .replace("%totalPresents%", String.valueOf(NPresents.getInstance().getDataBase().getTotalPresents()))
                        .replace("%playerPresents%", String.valueOf(NPresents.getInstance().getDataBase().getPlayerPresents(player))))));
            }
        }
        if (NPresents.getInstance().getConfig().getBoolean("reward.playSound")) {
            player.playSound(player.getLocation(), Sound.valueOf(NPresents.getInstance().getConfig().getString("reward.sound")), 1, 1);
        }
        if (NPresents.getInstance().getConfig().getBoolean("reward.animation")) {
            playAnimation(location);
        }
    }

    private void playAnimation(Location location) {
        RESERVED_FOR_ANIMATIONS.add(location);
        final ArmorStand armorStand = (ArmorStand) location.add(0.5, 0, 0.5).getWorld().spawnEntity(location.subtract(0.0D, 1.3D, 0.0D), EntityType.ARMOR_STAND);
        armorStand.setCustomNameVisible(true);
        armorStand.setGravity(false);
        armorStand.setInvulnerable(true);
        armorStand.setVisible(false);
        armorStand.setRemoveWhenFarAway(false);
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);

        SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", NPresents.getInstance().getConfig().getString("present.texture")));

        try {
            Field profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        item.setItemMeta(skullMeta);

        armorStand.setCustomNameVisible(false);
        armorStand.getEquipment().setHelmet(item);
        ANIMATION_STANDS.add(armorStand);
        BukkitRunnable runnable = new BukkitRunnable() {
            private final double initialHeight = armorStand.getLocation().getY();

            public void run() {
                for (double i = 0.0D; i < 2 * Math.PI; i += 0.19634954084936207D) {
                    double x = Math.cos(i) * 0.5D;
                    double y = Math.sin(i) * 0.9D;
                    double z = Math.sin(i) * 0.5D;
                    armorStand.getWorld().spawnParticle(Particle.valueOf(NPresents.getInstance().getConfig().getString("reward.animationParticle")), armorStand.getLocation().add(x, y, z), 1, 0.0D, 0.0D, 0.0D, 0.0D);
                }
                armorStand.getWorld().spawnParticle(Particle.FLAME, armorStand.getLocation(), 1);
                armorStand.teleport(armorStand.getLocation().add(0.0D, 0.04D, 0.0D));
                armorStand.setRotation(armorStand.getLocation().getYaw() + 20.0F, armorStand.getLocation().getPitch() + 10.0F);
                armorStand.setHeadPose(new EulerAngle(armorStand.getHeadPose().getX() + 0.05D, armorStand.getHeadPose().getY(), armorStand.getHeadPose().getZ() + 0.05D));

                if (armorStand.getLocation().getY() - initialHeight >= 4.0D) {
                    armorStand.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, armorStand.getLocation(), 3);
                    armorStand.getWorld().spawnParticle(Particle.EXPLOSION_NORMAL, armorStand.getLocation(), 1);
                    armorStand.getWorld().playSound(armorStand.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0F, 1.0F);

                    armorStand.remove();
                    RESERVED_FOR_ANIMATIONS.remove(location);
                    this.cancel();
                }
            }
        };

        runnable.runTaskTimer(NPresents.getInstance(), 0L, 1L);
    }
}
