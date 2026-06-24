package com.elonmuschio.gumball;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Messages {
    private final Gumball plugin;
    private FileConfiguration messages;

    public Messages(Gumball plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(file);
        InputStream defaults = plugin.getResource("messages.yml");
        if (defaults != null) {
            messages.setDefaults(YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defaults, StandardCharsets.UTF_8)));
        }
    }

    public String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public String raw(String path) {
        return messages.getString(path, "&cMissing: " + path);
    }

    public String get(String path) {
        return color(raw("prefix") + raw(path));
    }

    public String var(String path, Map<String, String> placeholders) {
        String text = raw("prefix") + raw(path);
        for (Map.Entry<String, String> e : placeholders.entrySet()) {
            text = text.replace(e.getKey(), e.getValue());
        }
        return color(text);
    }

    public List<String> formatLore(List<String> lore, Map<String, String> placeholders) {
        List<String> out = new ArrayList<>();
        for (String line : lore) {
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                line = line.replace(e.getKey(), e.getValue());
            }
            out.add(color(line));
        }
        return out;
    }

    public String effectName(String key) {
        return messages.getString("effects." + key, key);
    }

    public String itemName(String key) {
        String custom = messages.getString("items." + key);
        return custom != null ? custom : prettify(key);
    }

    private String prettify(String key) {
        StringBuilder sb = new StringBuilder();
        for (String word : key.toLowerCase().split("_")) {
            if (word.isEmpty()) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return sb.toString();
    }

    public String formatTime(long millis) {
        if (millis <= 0) {
            return "0" + raw("time.second");
        }
        long seconds = millis / 1000L;
        long hours = seconds / 3600L;
        long minutes = seconds % 3600L / 60L;
        long secs = seconds % 60L;
        StringBuilder sb = new StringBuilder();
        if (hours > 0) {
            sb.append(hours).append(raw("time.hour")).append(" ");
        }
        if (minutes > 0) {
            sb.append(minutes).append(raw("time.minute")).append(" ");
        }
        if (secs > 0 || sb.length() == 0) {
            sb.append(secs).append(raw("time.second"));
        }
        return sb.toString().trim();
    }
}
