package com.elonmuschio.gumball;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;

public class Machines {
    private final Gumball plugin;

    public Machines(Gumball plugin) {
        this.plugin = plugin;
    }

    public ItemStack getMachineItem(MachineColor color) {
        Platform platform = plugin.platform();
        Messages messages = plugin.messages();
        ItemStack item = platform.getSkull(Skin.TOP.getSkin());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(messages.color(color.getCode() + messages.raw("item-name")));
        meta.setLore(buildLore(color));
        item.setItemMeta(meta);
        platform.setNbt(item, "gumball", 1);
        platform.setNbt(item, "id", UUID.randomUUID().toString());
        platform.setNbt(item, "color", color.name());
        return item;
    }

    private List<String> buildLore(MachineColor color) {
        Config config = plugin.config();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%effect%", plugin.messages().effectName(config.effect(color)));
        placeholders.put("%duration%", String.valueOf(config.duration(color)));
        placeholders.put("%amplifier%", String.valueOf(config.amplifier(color) + 1));
        placeholders.put("%payment%", paymentText(color));
        return plugin.messages().formatLore(config.lore(color), placeholders);
    }

    private String paymentText(MachineColor color) {
        Material item = plugin.config().paymentItem(color);
        int amount = plugin.config().paymentAmount(color);
        if (item == null || amount <= 0) {
            return plugin.messages().raw("free");
        }
        return amount + "x " + plugin.messages().itemName(item.name());
    }

    public boolean isMachineItem(ItemStack item) {
        return item != null && plugin.platform().hasNbt(item) && plugin.platform().getIntNbt(item, "gumball") == 1;
    }

    public MachineColor itemColor(ItemStack item) {
        String value = plugin.platform().getStringNbt(item, "color");
        MachineColor color = value == null ? null : MachineColor.parse(value);
        return color != null ? color : MachineColor.RED;
    }

    public boolean isMachineEntity(Entity entity) {
        if (!(entity instanceof ArmorStand) || !plugin.platform().hasNbt(entity)) {
            return false;
        }
        return plugin.platform().getIntNbt(entity, "machineTop") == 1
                || plugin.platform().getIntNbt(entity, "machineBase") == 1;
    }

    public MachineColor entityColor(Entity entity) {
        if (!isMachineEntity(entity)) {
            return MachineColor.RED;
        }
        String value = plugin.platform().getStringNbt(entity, "color");
        MachineColor color = value == null ? null : MachineColor.parse(value);
        return color != null ? color : MachineColor.RED;
    }

    public void spawnMachine(Block block, MachineColor color) {
        Platform platform = plugin.platform();
        Messages messages = plugin.messages();
        Location base = block.getLocation();
        BlockFace face = platform.blockFace(block);
        float yaw = faceToYaw(face);

        ArmorStand top = platform.spawnArmorStand(base.clone().add(0.5, -0.99, 0.5),
                platform.getSkull(Skin.TOP.getSkin()), yaw, false, false, false, "machineTop");
        platform.setNbt(top, "color", color.name());

        ArmorStand body = platform.spawnArmorStand(base.clone().add(0.5, -0.7, 0.5),
                platform.getSkull(color.getSkin()), yaw, true, false, false, "machineBase");
        platform.setNbt(body, "color", color.name());

        if (plugin.config().displayTitle()) {
            ArmorStand title = platform.spawnArmorStand(base.clone().add(0.5, 0.99, 0.5),
                    new ItemStack(Material.AIR), yaw, false, true, true, "machineTitle");
            title.setCustomNameVisible(true);
            title.setCustomName(messages.color(color.getCode() + messages.raw("title")));
        }
    }

    public boolean removeMachine(Entity entity) {
        Location center = entity.getLocation().add(0.5, 0.0, 0.5);
        boolean removed = false;
        for (Entity e : entity.getWorld().getNearbyEntities(center, 1.0, 1.0, 1.0)) {
            if (e instanceof ArmorStand && plugin.platform().hasNbt(e)
                    && (plugin.platform().getIntNbt(e, "machineTop") == 1 || plugin.platform().getIntNbt(e, "machineBase") == 1)) {
                e.remove();
                removed = true;
            }
        }
        if (removed) {
            for (Entity e : entity.getWorld().getNearbyEntities(center.clone().add(0.0, 1.0, 0.0), 1.0, 1.0, 1.0)) {
                if (e instanceof ArmorStand && plugin.platform().hasNbt(e) && plugin.platform().getIntNbt(e, "machineTitle") == 1) {
                    e.remove();
                }
            }
        }
        return removed;
    }

    public ArmorStand standOf(Entity entity, String tag) {
        Location center = entity.getLocation().add(0.5, 0.0, 0.5);
        for (Entity e : entity.getWorld().getNearbyEntities(center, 1.0, 1.0, 1.0)) {
            if (e instanceof ArmorStand && plugin.platform().hasNbt(e) && plugin.platform().getIntNbt(e, tag) == 1) {
                return (ArmorStand) e;
            }
        }
        return null;
    }

    public void animate(Entity entity) {
        ArmorStand top = standOf(entity, "machineTop");
        switch (plugin.config().animation().toUpperCase()) {
            case "SPIN" -> spin(top);
            case "SHAKE" -> shake(top);
            case "BOUNCE" -> bounce(top);
            default -> {
            }
        }
        switch (plugin.config().particleEffect().toUpperCase()) {
            case "SPIRAL" -> spiral(top);
            case "BURST" -> burst(top);
            case "GUMBALLS" -> gumballs(top);
            default -> {
            }
        }
        plugin.platform().playSound(entity.getLocation(), plugin.config().soundType(),
                plugin.config().soundVolume(), plugin.config().soundPitch());
    }

    public boolean charge(Player player, MachineColor color) {
        Material item = plugin.config().paymentItem(color);
        int amount = plugin.config().paymentAmount(color);
        if (item == null || amount <= 0) {
            return true;
        }
        if (countItem(player, item) < amount) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("%amount%", String.valueOf(amount));
            placeholders.put("%item%", plugin.messages().itemName(item.name()));
            player.sendMessage(plugin.messages().var("payment.not-enough", placeholders));
            return false;
        }
        player.getInventory().removeItem(new ItemStack(item, amount));
        player.updateInventory();
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%amount%", String.valueOf(amount));
        placeholders.put("%item%", plugin.messages().itemName(item.name()));
        player.sendMessage(plugin.messages().var("payment.paid", placeholders));
        return true;
    }

    public void giveEffect(Player player, MachineColor color) {
        Config config = plugin.config();
        String effect = config.effect(color);
        int duration = config.duration(color);
        PotionEffectType type = plugin.platform().applyEffect(player, effect, duration, config.amplifier(color));
        if (type == null) {
            return;
        }
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%effect%", plugin.messages().effectName(effect));
        placeholders.put("%duration%", String.valueOf(duration));
        player.sendMessage(plugin.messages().var("effect-received", placeholders));
    }

    private int countItem(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }

    private static float faceToYaw(BlockFace face) {
        return switch (face) {
            case SOUTH -> 180.0f;
            case WEST -> -90.0f;
            case EAST -> 90.0f;
            default -> 0.0f;
        };
    }

    private void spin(ArmorStand stand) {
        if (stand == null) {
            return;
        }
        EulerAngle original = stand.getHeadPose();
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (stand.isDead() || tick >= 30) {
                    stand.setHeadPose(original);
                    cancel();
                    return;
                }
                stand.setHeadPose(new EulerAngle(original.getX(), original.getY() + 0.41887902 * tick, original.getZ()));
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void shake(ArmorStand stand) {
        if (stand == null) {
            return;
        }
        EulerAngle original = stand.getHeadPose();
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (stand.isDead() || tick > 20) {
                    stand.setHeadPose(original);
                    cancel();
                    return;
                }
                stand.setHeadPose(original.setY(Math.sin(tick * 0.4) * 0.5));
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void bounce(ArmorStand stand) {
        if (stand == null) {
            return;
        }
        Location base = stand.getLocation().clone();
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (stand.isDead() || tick > 20) {
                    stand.teleport(base);
                    cancel();
                    return;
                }
                stand.teleport(base.clone().add(0.0, Math.sin(tick * 0.3) * 0.1, 0.0));
                tick++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void spiral(Entity stand) {
        if (stand == null) {
            return;
        }
        Location base = stand.getLocation().add(0.0, 1.2, 0.0);
        new BukkitRunnable() {
            double t = 0.0;

            @Override
            public void run() {
                t += 0.3926991;
                for (int i = 0; i < 2; i++) {
                    double x = Math.cos(t + i * Math.PI) * 0.3;
                    double z = Math.sin(t + i * Math.PI) * 0.3;
                    base.getWorld().spawnParticle(Particle.END_ROD, base.clone().add(x, t * 0.05, z), 1, 0, 0, 0, 0);
                }
                if (t > Math.PI * 4) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void burst(Entity stand) {
        if (stand == null) {
            return;
        }
        Location loc = stand.getLocation().add(0.0, 1.3, 0.0);
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (++ticks >= 50) {
                    cancel();
                    return;
                }
                loc.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, loc, 20, 0.3, 0.3, 0.3, 0.05);
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void gumballs(Entity stand) {
        if (stand == null) {
            return;
        }
        Location top = stand.getLocation().add(0.0, 2.0, 0.0);
        new BukkitRunnable() {
            int tick = 0;

            @Override
            public void run() {
                if (++tick > 20) {
                    cancel();
                    return;
                }
                top.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, top, 4, 0.25, 0.0, 0.25, 0.0,
                        new Particle.DustTransition(Color.RED, Color.FUCHSIA, 1.3f));
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }
}
