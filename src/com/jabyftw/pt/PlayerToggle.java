package com.jabyftw.pt;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Rafael
 */
public class PlayerToggle extends JavaPlugin implements Listener {

    private ItemStack matE, matD;
    private boolean give;
    private List<Player> players = new ArrayList();

    @Override
    public void onEnable() {
        FileConfiguration config = getConfig();
        config.addDefault("config.toggleMaterialEnabled", "ink_sack;10");
        config.addDefault("config.toggleMaterialDisabled", "ink_sack;8");
        config.addDefault("config.giveOnJoin", true);
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
        matE = getItemStack(config.getString("config.toggleMaterialEnabled"));
        matD = getItemStack(config.getString("config.toggleMaterialDisabled"));
        give = config.getBoolean("config.giveOnJoin");
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
            if (e.getItem() != null && (e.getItem().getType().equals(matE.getType()) || e.getItem().getType().equals(matD.getType()))) {
                togglePlayerView(p, e.getItem());
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player online = e.getPlayer();
        for (Player p : players) {
            if (p.canSee(online) && !online.hasPermission("pt.exception")) {
                p.hidePlayer(online);
            }
        }
        if (give && online.hasPermission("pt.toggle") && (!online.getInventory().contains(matE.getType()) || !online.getInventory().contains(matD.getType()))) {
            online.getInventory().addItem(matD);
        }
    }

    private void togglePlayerView(Player p, ItemStack is) {
        if (!players.contains(p)) {
            for (Player online : getServer().getOnlinePlayers()) {
                if (p.canSee(online) && !online.hasPermission("pt.exception")) {
                    p.hidePlayer(online);
                }
            }
            players.add(p);
            is.setType(matE.getType());
            is.setDurability(matE.getDurability());
        } else {
            for (Player online : getServer().getOnlinePlayers()) {
                if (!p.canSee(online)) {
                    p.showPlayer(online);
                }
            }
            players.remove(p);
            is.setType(matD.getType());
            is.setDurability(matD.getDurability());
        }
    }

    private ItemStack getItemStack(String string) {
        String[] s = string.split(";");
        return new ItemStack(Material.valueOf(s[0].toUpperCase()), 1, Short.valueOf(s[1]));
    }
}
