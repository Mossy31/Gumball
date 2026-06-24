package com.elonmuschio.gumball;

import com.elonmuschio.gumball.commands.GumballCommand;
import com.elonmuschio.gumball.commands.GumballTab;
import com.elonmuschio.gumball.events.MachineListener;
import java.util.Objects;
import org.bukkit.plugin.java.JavaPlugin;

public final class Gumball extends JavaPlugin {
    private Config config;
    private Messages messages;
    private Platform platform;
    private Machines machines;
    private Cooldowns cooldowns;

    @Override
    public void onEnable() {
        config = new Config(this);
        messages = new Messages(this);
        platform = new Platform(this);
        machines = new Machines(this);
        cooldowns = new Cooldowns(this);

        Objects.requireNonNull(getCommand("gumball")).setExecutor(new GumballCommand(this));
        Objects.requireNonNull(getCommand("gumball")).setTabCompleter(new GumballTab());
        getServer().getPluginManager().registerEvents(new MachineListener(this), this);
    }

    public Config config() {
        return config;
    }

    public Messages messages() {
        return messages;
    }

    public Platform platform() {
        return platform;
    }

    public Machines machines() {
        return machines;
    }

    public Cooldowns cooldowns() {
        return cooldowns;
    }
}
