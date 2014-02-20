package com.jabyftw.pt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Rafael
 */
public class PlayerToggle extends JavaPlugin implements Listener {

    private ItemStack matE, matD;
    private boolean give;
    private final List<Player> players = new ArrayList();
    private final LinkedList<World> alwaysvisible = new LinkedList();

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        FileConfiguration config = getConfig();
        config.addDefault("config.toggleMaterialEnabled", "ink_sack;10;&eHiding players...");
        config.addDefault("config.toggleMaterialDisabled", "ink_sack;8;&4Showing players...");
        config.addDefault("config.giveOnJoin", true);
        String[] worlds = {"world_nether"};
        config.addDefault("config.alwaysVisibleWorld", Arrays.asList(worlds));
        config.options().copyDefaults(true);
        saveConfig();
        reloadConfig();
        matE = getItemStack(config.getString("config.toggleMaterialEnabled"));
        matD = getItemStack(config.getString("config.toggleMaterialDisabled"));
        give = config.getBoolean("config.giveOnJoin");
        for (String s : config.getStringList("config.alwaysVisibleWorld")) {
            alwaysvisible.add(getServer().getWorld(s));
        }
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().log(Level.INFO, "Enabled in {0}ms", (System.currentTimeMillis() - start));
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

    @EventHandler
    public void onChangeWorld(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        if (alwaysvisible.contains(p.getWorld()) && players.contains(p)) {
            for (Player online : getServer().getOnlinePlayers()) {
                if (!p.canSee(online)) {
                    p.showPlayer(online);
                }
            }
            players.remove(p);
        }
    }

    private void togglePlayerView(Player p, ItemStack is) {
        if (!alwaysvisible.contains(p.getWorld())) {
            if (!players.contains(p)) {
                for (Player online : getServer().getOnlinePlayers()) {
                    if (p.canSee(online) && !online.hasPermission("pt.exception")) {
                        p.hidePlayer(online);
                    }
                }
                players.add(p);
                is.setType(matE.getType());
                is.setItemMeta(matE.getItemMeta());
                is.setDurability(matE.getDurability());
            } else {
                for (Player online : getServer().getOnlinePlayers()) {
                    if (!p.canSee(online)) {
                        p.showPlayer(online);
                    }
                }
                players.remove(p);
                is.setType(matD.getType());
                is.setItemMeta(matD.getItemMeta());
                is.setDurability(matD.getDurability());
            }
        } else {
            is.setType(matD.getType());
            is.setItemMeta(matD.getItemMeta());
            is.setDurability(matD.getDurability());
        }
    }

    private ItemStack getItemStack(String string) {
        String[] s = string.split(";");
        ItemStack is = new ItemStack(Material.valueOf(s[0].toUpperCase()), 1, Short.valueOf(s[1]));
        ItemMeta im = is.getItemMeta();
        im.setDisplayName(s[2].replaceAll("&", "§"));
        is.setItemMeta(im);
        return is;
    }
}
