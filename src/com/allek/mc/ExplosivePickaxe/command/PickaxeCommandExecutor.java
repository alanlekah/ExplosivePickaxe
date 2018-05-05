package com.allek.mc.ExplosivePickaxe.command;

import com.allek.mc.ExplosivePickaxe.Driver;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PickaxeCommandExecutor implements CommandExecutor {

    // error for non-players
    public static final String PLAYER_REQUIRED_COMMAND = ChatColor.RED + "Must be a player to use this command!";

    // error for permission issues
    public static final String PERMISSION_DENIED = ChatColor.RED + "You do not have the correct permission to issue this!";

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage(PLAYER_REQUIRED_COMMAND);
            return false;
        }

        if (!(s.equalsIgnoreCase("explosivepickaxe") || s.equalsIgnoreCase("ep") || s.equalsIgnoreCase("epick"))) {
            return true;
        }

        Player player = (Player) commandSender;

        if (!player.hasPermission(Driver.PICKAXE_ACCESS_PERMISSION) && !player.isOp()) {
            commandSender.sendMessage(PERMISSION_DENIED);
            return false;
        }

        for (String playerString : strings) {
            Player itemGranter = commandSender.getServer().getPlayer(playerString);
            if (itemGranter == null) {
                player.sendMessage(Driver.INVALID_PLAYER_ERROR);
                return true;
            }
            player.getInventory().addItem(getPickaxe());
        }

        if (strings.length == 0)
            // add pickaxe to caller's inventory
            player.getInventory().addItem(getPickaxe());

        // return
        return true;
    }

    public ItemStack getPickaxe() {
        ItemStack itemStack = new ItemStack(Driver.PICKAXE_TYPE);
        itemStack.setItemMeta(getPickaxeMeta(itemStack));
        itemStack.addUnsafeEnchantments(getPickaxeEnchantments());
        return itemStack;
    }

    public ItemMeta getPickaxeMeta(ItemStack pickaxe) {
        ItemMeta itemMeta = pickaxe.getItemMeta();
        itemMeta.setLore(Arrays.asList(Driver.PICKAXE_LORE));
        itemMeta.setDisplayName(Driver.PICKAXE_DISPLAY_NAME);
        return itemMeta;
    }

    public Map<Enchantment, Integer> getPickaxeEnchantments() {
        Map<Enchantment, Integer> enchants = new HashMap<>();
        enchants.put(Enchantment.DIG_SPEED, 4);
        enchants.put(Enchantment.LOOT_BONUS_BLOCKS, 3);
        return enchants;
    }

}
