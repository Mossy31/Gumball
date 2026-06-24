package com.elonmuschio.gumball;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Cooldowns {
    private final Map<UUID, Long> players = new HashMap<>();
    private final Map<String, Long> machines = new HashMap<>();
    private final Gumball plugin;

    public Cooldowns(Gumball plugin) {
        this.plugin = plugin;
    }

    public void cleanup() {
        long now = System.currentTimeMillis();
        players.values().removeIf(expires -> expires < now);
        machines.values().removeIf(expires -> expires < now);
    }

    private String key(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ();
    }

    public boolean isPlayerOnCooldown(Player player) {
        return players.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis();
    }

    public long remainingPlayer(Player player) {
        return Math.max(0L, players.getOrDefault(player.getUniqueId(), 0L) - System.currentTimeMillis());
    }

    public void applyPlayer(Player player) {
        int seconds = plugin.config().playerCooldown();
        if (seconds <= 0) {
            return;
        }
        players.put(player.getUniqueId(), System.currentTimeMillis() + seconds * 1000L);
    }

    public boolean isMachineOnCooldown(Location loc) {
        return machines.getOrDefault(key(loc), 0L) > System.currentTimeMillis();
    }

    public long remainingMachine(Location loc) {
        return Math.max(0L, machines.getOrDefault(key(loc), 0L) - System.currentTimeMillis());
    }

    public void applyMachine(Location loc, MachineColor color) {
        int seconds = plugin.config().machineCooldown(color);
        if (seconds <= 0) {
            return;
        }
        machines.put(key(loc), System.currentTimeMillis() + seconds * 1000L);
    }
}
