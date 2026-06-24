package com.elonmuschio.gumball;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    private final Gumball plugin;

    public Config(Gumball plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
    }

    private FileConfiguration cfg() {
        return plugin.getConfig();
    }

    public int playerCooldown() {
        return cfg().getInt("settings.player-cooldown-seconds", 0);
    }

    public String animation() {
        return cfg().getString("settings.animation", "SPIN");
    }

    public String particleEffect() {
        return cfg().getString("settings.particle-effect", "GUMBALLS");
    }

    public boolean launchAnimation() {
        return cfg().getBoolean("settings.launch-animation", true);
    }

    public boolean displayTitle() {
        return cfg().getBoolean("settings.display-title", true);
    }

    public boolean shiftToRemove() {
        return cfg().getBoolean("settings.shift-to-remove", true);
    }

    public String soundType() {
        return cfg().getString("settings.sound.type", "");
    }

    public float soundVolume() {
        return (float) cfg().getDouble("settings.sound.volume", 1.0);
    }

    public float soundPitch() {
        return (float) cfg().getDouble("settings.sound.pitch", 1.0);
    }

    private String path(MachineColor color, String key) {
        return "colors." + color.name() + "." + key;
    }

    public List<String> lore(MachineColor color) {
        return cfg().getStringList(path(color, "lore"));
    }

    public Material paymentItem(MachineColor color) {
        String name = cfg().getString(path(color, "payment.item"), "");
        if (name == null || name.isEmpty()) {
            return null;
        }
        return Material.matchMaterial(name);
    }

    public int paymentAmount(MachineColor color) {
        return cfg().getInt(path(color, "payment.amount"), 0);
    }

    public String effect(MachineColor color) {
        return cfg().getString(path(color, "effect"), "SPEED").toUpperCase();
    }

    public int amplifier(MachineColor color) {
        return cfg().getInt(path(color, "amplifier"), 0);
    }

    public int duration(MachineColor color) {
        return cfg().getInt(path(color, "duration"), 60);
    }

    public int machineCooldown(MachineColor color) {
        return cfg().getInt(path(color, "machine-cooldown"), 0);
    }
}
