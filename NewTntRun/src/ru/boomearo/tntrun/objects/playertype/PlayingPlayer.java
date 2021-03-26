package ru.boomearo.tntrun.objects.playertype;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import ru.boomearo.gamecontrol.utils.ExpFix;
import ru.boomearo.tntrun.objects.ItemButton;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntTeam;

public class PlayingPlayer implements IPlayerType {
    
    private TntPlayer killer;
    
    @Override
    public void preparePlayer(TntPlayer player) {
        Player pl = player.getPlayer();
        
        pl.setFoodLevel(20);
        pl.setHealth(pl.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        
        pl.setGameMode(GameMode.ADVENTURE);
        pl.setFlying(false);
        pl.setAllowFlight(false);
        
        ExpFix.setTotalExperience(player.getPlayer(), 0);
        
        PlayerInventory inv = pl.getInventory();
        inv.clear();
        
        for (ItemButton ib : ItemButton.values()) {
            inv.setItem(ib.getSlot(), ib.getItem());
        }
        
        inv.setHeldItemSlot(0);
        
        TntTeam team = player.getTeam();
        Location loc = team.getSpawnPoint();
        if (loc != null) {
            pl.teleport(loc);
        }
    }
    
    public TntPlayer getKiller() {
        return this.killer;
    }
    
    public void setKiller(TntPlayer killer) {
        this.killer = killer;
    }
    
}