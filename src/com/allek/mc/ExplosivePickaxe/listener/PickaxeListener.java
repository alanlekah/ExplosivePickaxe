package com.allek.mc.ExplosivePickaxe.listener;

import com.allek.mc.ExplosivePickaxe.Driver;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.RegionQuery;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.minecraft.server.v1_12_R1.EnumParticle;
import net.minecraft.server.v1_12_R1.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

public class PickaxeListener implements Listener {

    private final WorldGuardPlugin wgPlugin;

    private List<Player> explosionDamages = new ArrayList<>();


    public PickaxeListener(WorldGuardPlugin wgPlugin) {
        this.wgPlugin = wgPlugin;
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (!event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            return;

        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        ItemStack itemStack = inv.getItemInMainHand();

        // check if the item the player is using matches the explosive pickaxe
        if (isExplosivePickaxe(itemStack)) {

            short durability = itemStack.getDurability();

            Location loc = event.getClickedBlock().getLocation();

            // check WorldGuard to see if a block can be broken prior to exploding
            if (canBreak(player, loc)) {
                explosionDamages.add(event.getPlayer());

                PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_LARGE, true, (float) loc.getX(), (float) loc.getY(), (float) loc.getZ(), 0, 1, 0, 0, 7);
                ((CraftPlayer) event.getPlayer()).getHandle().playerConnection.sendPacket(packet);

                event.getPlayer().getWorld().createExplosion(loc, 1.0F, false);
            }
            else {
                player.sendMessage(Driver.PICKAXE_BREAK_ERROR);
            }


            // update the item stack durability back to full and set it in the hand again
            itemStack.setDurability(durability);
            inv.setItemInMainHand(itemStack);
        }
    }

    @EventHandler
    public void onPlayerDamageEvent(EntityDamageEvent event) {
        if (!event.getEntityType().equals(EntityType.PLAYER))
            return;


        Player player = (Player) event.getEntity();

        if (explosionDamages.contains(player)) {
            explosionDamages.remove(player);
            event.setDamage(0.0);
            event.setCancelled(true);
        }
    }


    public boolean isExplosivePickaxe(ItemStack itemStack) {
        return (itemStack.getType().equals(Driver.PICKAXE_TYPE) &&
                itemStack.hasItemMeta() &&
                itemStack.getItemMeta().hasLore() &&
                itemStack.getItemMeta().getLore().get(0).equals(Driver.PICKAXE_LORE) &&
                itemStack.getItemMeta().getDisplayName().equals(Driver.PICKAXE_DISPLAY_NAME));
    }

    public boolean canBreak(Player player, Location loc) {
        LocalPlayer localPlayer = wgPlugin.wrapPlayer(player);
        RegionContainer container = wgPlugin.getRegionContainer();
        RegionQuery query = container.createQuery();
        return !query.testState(loc, localPlayer, DefaultFlag.BLOCK_BREAK);
    }

}
