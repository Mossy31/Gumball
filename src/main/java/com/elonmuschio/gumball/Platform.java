package com.elonmuschio.gumball;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Rotatable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Platform {
    private static final UUID SKIN_OWNER = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4");
    private final Gumball plugin;

    public Platform(Gumball plugin) {
        this.plugin = plugin;
    }

    private NamespacedKey key(String tag) {
        return new NamespacedKey(plugin, tag);
    }

    public ItemStack setNbt(ItemStack item, String tag, String value) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key(tag), PersistentDataType.STRING, value);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack setNbt(ItemStack item, String tag, int value) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(key(tag), PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
        return item;
    }

    public void setNbt(Entity entity, String tag, String value) {
        entity.getPersistentDataContainer().set(key(tag), PersistentDataType.STRING, value);
    }

    public void setNbt(Entity entity, String tag, int value) {
        entity.getPersistentDataContainer().set(key(tag), PersistentDataType.INTEGER, value);
    }

    public int getIntNbt(ItemStack item, String tag) {
        return item.getItemMeta().getPersistentDataContainer().getOrDefault(key(tag), PersistentDataType.INTEGER, -1);
    }

    public int getIntNbt(Entity entity, String tag) {
        return entity.getPersistentDataContainer().getOrDefault(key(tag), PersistentDataType.INTEGER, -1);
    }

    public String getStringNbt(ItemStack item, String tag) {
        return item.getItemMeta().getPersistentDataContainer().get(key(tag), PersistentDataType.STRING);
    }

    public String getStringNbt(Entity entity, String tag) {
        return entity.getPersistentDataContainer().get(key(tag), PersistentDataType.STRING);
    }

    public boolean hasNbt(ItemStack item) {
        return item.hasItemMeta() && !item.getItemMeta().getPersistentDataContainer().isEmpty();
    }

    public boolean hasNbt(Entity entity) {
        return !entity.getPersistentDataContainer().isEmpty();
    }

    public boolean isMainHand(PlayerEvent event) {
        if (event instanceof PlayerInteractEvent e) {
            return e.getHand() == EquipmentSlot.HAND;
        }
        if (event instanceof PlayerInteractEntityEvent e) {
            return e.getHand() == EquipmentSlot.HAND;
        }
        return false;
    }

    public void removeOneInHand(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType() == Material.AIR) {
            return;
        }
        if (hand.getAmount() <= 1) {
            player.getInventory().setItemInMainHand(null);
        } else {
            hand.setAmount(hand.getAmount() - 1);
        }
    }

    private PlayerProfile profile(String url) {
        try {
            PlayerProfile profile = Bukkit.createPlayerProfile(SKIN_OWNER, "Gumball");
            PlayerTextures textures = profile.getTextures();
            textures.setSkin(new URL(url));
            profile.setTextures(textures);
            return profile;
        } catch (Exception e) {
            throw new RuntimeException("Invalid skin URL: " + url, e);
        }
    }

    public ItemStack getSkull(String texture) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        if (texture == null || texture.isEmpty()) {
            return skull;
        }
        String url = texture.startsWith("http") ? texture : "http://textures.minecraft.net/texture/" + texture;
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwnerProfile(profile(url));
        skull.setItemMeta(meta);
        return skull;
    }

    public ArmorStand spawnArmorStand(Location loc, ItemStack helmet, float yaw, boolean small, boolean marker, boolean invulnerable, String tag) {
        ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setInvisible(true);
        stand.setMarker(marker);
        stand.setGravity(false);
        stand.setSmall(small);
        stand.setInvulnerable(invulnerable);
        stand.getEquipment().setHelmet(helmet);
        stand.setRotation(yaw, 0.0f);
        setNbt(stand, tag, 1);
        return stand;
    }

    public BlockFace blockFace(Block block) {
        BlockData data = block.getBlockData();
        if (data instanceof Rotatable rotatable) {
            return rotatable.getRotation();
        }
        return BlockFace.NORTH;
    }

    public void playSound(Location loc, String name, float volume, float pitch) {
        if (name == null || name.isEmpty() || loc.getWorld() == null) {
            return;
        }
        try {
            loc.getWorld().playSound(loc, name, volume, pitch);
        } catch (IllegalArgumentException ignored) {
        }
    }

    private PotionEffectType effectByName(String name) {
        return Registry.EFFECT.get(NamespacedKey.minecraft(name.toLowerCase()));
    }

    public PotionEffectType applyEffect(Player player, String effectName, int durationSeconds, int amplifier) {
        if (player == null || !player.isOnline()) {
            return null;
        }
        PotionEffectType type = effectByName(effectName);
        if (type == null) {
            return null;
        }
        player.addPotionEffect(new PotionEffect(type, 20 * durationSeconds, amplifier, false, true, true));
        return type;
    }

    public void launchGumball(Location from, Player target, Runnable onHit) {
        World world = from.getWorld();
        ArmorStand gumball = (ArmorStand) world.spawn(from, ArmorStand.class, stand -> {
            stand.setInvisible(true);
            stand.setMarker(true);
            stand.setSmall(true);
            stand.setGravity(false);
            stand.getEquipment().setHelmet(getSkull(Skin.randomGumball().getSkin()));
        });
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!gumball.isValid()) {
                    cancel();
                    return;
                }
                Location loc = gumball.getLocation();
                if (ticks++ >= 200 || !target.isOnline() || target.isDead()) {
                    explode(loc);
                    if (target.isOnline() && !target.isDead()) {
                        onHit.run();
                    }
                    finish();
                    return;
                }
                Location eye = target.getEyeLocation();
                Vector direction = eye.toVector().subtract(loc.toVector()).normalize().multiply(0.35);
                loc.add(direction);
                gumball.teleport(loc);
                world.spawnParticle(Particle.DUST, loc, 2, 0.05, 0.05, 0.05,
                        new Particle.DustOptions(Color.fromRGB(
                                ThreadLocalRandom.current().nextInt(256),
                                ThreadLocalRandom.current().nextInt(256),
                                ThreadLocalRandom.current().nextInt(256)), 1.2f));
                if (loc.distanceSquared(eye) <= 0.25) {
                    explode(loc);
                    onHit.run();
                    finish();
                }
            }

            private void explode(Location loc) {
                world.spawnParticle(Particle.FIREWORK, loc, 25, 0.3, 0.3, 0.3);
                world.spawnParticle(Particle.CRIT, loc, 20, 0.2, 0.2, 0.2);
                world.playSound(loc, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1.0f, 1.2f);
            }

            private void finish() {
                gumball.remove();
                cancel();
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }
}
