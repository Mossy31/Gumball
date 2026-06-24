package com.elonmuschio.gumball.events;

import com.elonmuschio.gumball.Gumball;
import com.elonmuschio.gumball.MachineColor;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

public class MachineListener implements Listener {
    private final Gumball plugin;

    public MachineListener(Gumball plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!plugin.machines().isMachineItem(item)) {
            return;
        }
        event.setCancelled(true);
        Player player = event.getPlayer();
        if (!player.hasPermission("gumball.place")) {
            player.sendMessage(plugin.messages().get("place-no-permission"));
            return;
        }
        plugin.machines().spawnMachine(event.getBlockPlaced(), plugin.machines().itemColor(item));
        if (player.getGameMode() != GameMode.CREATIVE) {
            plugin.platform().removeOneInHand(player);
        }
        player.sendMessage(plugin.messages().get("placed"));
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (!plugin.platform().isMainHand(event) || !plugin.machines().isMachineEntity(event.getRightClicked())) {
            return;
        }
        event.setCancelled(true);
        plugin.cooldowns().cleanup();
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        MachineColor color = plugin.machines().entityColor(entity);
        Location machineLoc = plugin.machines().standOf(entity, "machineBase").getLocation();

        if (plugin.cooldowns().isPlayerOnCooldown(player) && !player.hasPermission("gumball.player-cooldown.bypass")) {
            sendCooldown(player, "cooldown.player", plugin.cooldowns().remainingPlayer(player));
            return;
        }
        if (plugin.cooldowns().isMachineOnCooldown(machineLoc) && !player.hasPermission("gumball.machine-cooldown.bypass")) {
            sendCooldown(player, "cooldown.machine", plugin.cooldowns().remainingMachine(machineLoc));
            return;
        }
        if (!plugin.machines().charge(player, color)) {
            return;
        }
        plugin.cooldowns().applyPlayer(player);
        plugin.cooldowns().applyMachine(machineLoc, color);
        plugin.machines().animate(entity);
        if (plugin.config().launchAnimation()) {
            Location out = entity.getLocation().clone().add(0.0, 1.4, 0.0);
            plugin.platform().launchGumball(out, player, () -> plugin.machines().giveEffect(player, color));
        } else {
            plugin.machines().giveEffect(player, color);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!plugin.machines().isMachineEntity(event.getEntity())) {
            return;
        }
        event.setCancelled(true);
        if (!(event.getDamager() instanceof Player player) || event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
            return;
        }
        if (!player.hasPermission("gumball.remove")) {
            player.sendMessage(plugin.messages().get("remove-no-permission"));
            return;
        }
        if (plugin.config().shiftToRemove() && !player.isSneaking()) {
            player.sendMessage(plugin.messages().get("sneak-to-remove"));
            return;
        }
        MachineColor color = plugin.machines().entityColor(event.getEntity());
        Location loc = event.getEntity().getLocation();
        plugin.machines().removeMachine(event.getEntity());
        player.sendMessage(plugin.messages().get("removed"));
        if (player.getGameMode() != GameMode.CREATIVE) {
            ItemStack machine = plugin.machines().getMachineItem(color);
            if (!player.getInventory().addItem(machine).isEmpty()) {
                loc.getWorld().dropItem(loc, machine);
            }
        }
    }

    private void sendCooldown(Player player, String key, long remaining) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("%time%", plugin.messages().formatTime(remaining));
        player.sendMessage(plugin.messages().var(key, placeholders));
    }
}
