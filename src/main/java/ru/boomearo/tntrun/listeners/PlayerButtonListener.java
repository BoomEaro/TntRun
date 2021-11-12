package ru.boomearo.tntrun.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
    
}
