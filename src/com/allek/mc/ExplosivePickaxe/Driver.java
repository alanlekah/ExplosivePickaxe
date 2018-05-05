package com.allek.mc.ExplosivePickaxe;

import com.allek.mc.ExplosivePickaxe.command.PickaxeCommandExecutor;
import com.allek.mc.ExplosivePickaxe.listener.PickaxeListener;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

public class Driver extends JavaPlugin {

    // pickaxe lore constant
    public static final String PICKAXE_LORE = "Blast your way to the top!";
    // pickaxe type constant
    public static final Material PICKAXE_TYPE = Material.DIAMOND_PICKAXE;
    // display name
    public static final String PICKAXE_DISPLAY_NAME = ChatColor.ITALIC + "" + ChatColor.BLUE + "Explosive Pickaxe";

    // pickaxe permission per config (to use the plugin)
    public static final String PICKAXE_ACCESS_PERMISSION = "epick.give";

    // error if you cant break the block
    public static final String PICKAXE_BREAK_ERROR = ChatColor.RED + "" + ChatColor.ITALIC + "You do not have permission to break that!";

    // error if the player doesn't exist on the server
    public static final String INVALID_PLAYER_ERROR = ChatColor.RED + "Invalid player name!";


    private PluginLogger log;

    @Override
    public void onEnable() {
        // get the default plugin logger
        log = new PluginLogger(this);

        // get the WorldGuard plugin for block breaking permissions
        WorldGuardPlugin wgPlugin = getWorldGuard();

        if (wgPlugin == null) {
            log.log(Level.SEVERE, "Failed to load due to WorldGuard plugin!");
            getPluginLoader().disablePlugin(this);
        }

        // register the pickaxe listener
        this.getServer().getPluginManager().registerEvents(new PickaxeListener(wgPlugin), this);

        // register the explosive pickaxe executor
        getCommand("epick").setExecutor(new PickaxeCommandExecutor());

        // plugin loaded successfully
        log.info("Successfully loaded!");
    }


    /**
     * Gets the worldguard dependancy
     * @return
     */
    private WorldGuardPlugin getWorldGuard() {
        Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");

        // WorldGuard may not be loaded
        if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
            return null; // Maybe you want throw an exception instead
        }

        return (WorldGuardPlugin) plugin;
    }


}
