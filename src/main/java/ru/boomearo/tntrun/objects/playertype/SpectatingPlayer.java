package ru.boomearo.tntrun.objects.playertype;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;

import ru.boomearo.serverutils.utils.other.ExpFix;
import ru.boomearo.tntrun.objects.TntPlayer;
import ru.boomearo.tntrun.objects.TntTeam;

public class SpectatingPlayer implements IPlayerType {

    @Override
    public void preparePlayer(TntPlayer player) {
        Player pl = player.getPlayer();

        pl.setFoodLevel(20);
        pl.setHealth(pl.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

        pl.setGameMode(GameMode.SPECTATOR);

        ExpFix.setTotalExperience(player.getPlayer(), 0);

        pl.getInventory().clear();

        TntTeam team = player.getTeam();
        Location loc = team.getSpawnPoint();
        if (loc != null) {
            pl.teleport(loc);
        }
    }
}