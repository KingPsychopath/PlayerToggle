package com.jabyftw.pt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Rafael
 */
public class PlayerToggle extends JavaPlugin implements Listener {

    private Material mat;
    private List<Player> players = new ArrayList();

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        config.addDefault("config.toggleMaterial", "watch");
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
        mat = Material.valueOf(config.getString("config.toggleMaterial").toUpperCase());
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().log(Level.INFO, "Enabled");
    }

    @Override
    public void onDisable() {
        getLogger().log(Level.INFO, "Disabled");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("pt.toggle")) {
            if (e.getItem() != null && e.getItem().getType().equals(mat)) {
                togglePlayerView(p);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player online = e.getPlayer();
        for (Player p : players) {
            if (p.canSee(online)) {
                p.hidePlayer(online);
            }
        }
    }

    private void togglePlayerView(Player p) {
        if (!players.contains(p)) {
            for (Player online : getServer().getOnlinePlayers()) {
                if (p.canSee(online)) {
                    p.hidePlayer(online);
                }
            }
            players.add(p);
        } else {
            for (Player online : getServer().getOnlinePlayers()) {
                if (!p.canSee(online)) {
                    p.showPlayer(online);
                }
            }
            players.remove(p);
        }
    }
}
