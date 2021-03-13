package ru.boomearo.tntrun.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.objects.ItemButton;
import ru.boomearo.tntrun.objects.TntPlayer;

public class PlayerButtonListener implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent e) {
        Player pl = e.getPlayer();
        TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
        if (tp != null) {
            e.setCancelled(true);
            
            Action ac = e.getAction();
            if (ac == Action.RIGHT_CLICK_AIR || ac == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = e.getItem();
                if (item != null) {
                    ItemButton ib = ItemButton.getButtonByItem(item);
                    if (ib != null) {
                        ib.getClick().click(tp);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerDropItemEvent(PlayerDropItemEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Player pl = e.getPlayer();
        TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
        if (tp != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onEntityPickupItemEvent(EntityPickupItemEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Entity en = e.getEntity();
        if (en instanceof Player) {
            Player pl = (Player) en;

            TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
            if (tp != null) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if (e.isCancelled()) {
            return;
        }
        HumanEntity en = e.getWhoClicked();
        if (en instanceof Player) {
            Player pl = (Player) en;
            
            TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
            if (tp != null) {
                e.setCancelled(true);
                e.setResult(Result.DENY);
            }
            
        }
    }
    
    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent e) {
        if (e.isCancelled()) {
            return;
        }
        HumanEntity en = e.getWhoClicked();
        if (en instanceof Player) {
            Player pl = (Player) en;
            
            TntPlayer tp = TntRun.getInstance().getTntRunManager().getGamePlayer(pl.getName());
            if (tp != null) {
                e.setCancelled(true);
                e.setResult(Result.DENY);
            }
            
        }
    }
    
}
