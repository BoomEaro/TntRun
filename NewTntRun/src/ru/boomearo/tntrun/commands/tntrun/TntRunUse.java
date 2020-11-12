package ru.boomearo.tntrun.commands.tntrun;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.Region;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.commands.CmdInfo;
import ru.boomearo.tntrun.exceptions.TntRunException;
import ru.boomearo.tntrun.managers.ArenaManager;
import ru.boomearo.tntrun.objects.Arena;
import ru.boomearo.tntrun.objects.region.CuboidRegion;
import ru.boomearo.tntrun.objects.TntPlayer.PlayingPlayer;

public class TntRunUse {


    @CmdInfo(name = "createarena", description = "Создать арену с указанным названием.", usage = "/tntrun createarena <название>", permission = "tntrun.admin")
    public boolean createarena(CommandSender cs, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("Данная команда только для игроков.");
            return true;
        }
        if (args.length < 1 || args.length > 1) {
            return false;
        }
        String arena = args[0];
        Player pl = (Player) cs;
        
        BukkitPlayer bPlayer = BukkitAdapter.adapt(pl);
        LocalSession ls = WorldEdit.getInstance().getSessionManager().get(bPlayer);
        Region re = ls.getSelection(ls.getSelectionWorld());
        if (re == null) {
            pl.sendMessage("Выделите регион!");
            return true;
        }

        List<Location> spawnPoints = new ArrayList<Location>();
        Location plLoc = pl.getLocation();
        spawnPoints.add(plLoc);

        try {
            Arena newArena = new Arena(arena, 2, 15, 300, pl.getWorld(), new CuboidRegion(re.getMaximumPoint(), re.getMinimumPoint(), pl.getWorld()), spawnPoints, pl.getLocation(), null);
            
            ArenaManager am = TntRun.getInstance().getArenaManager();
            am.addArena(newArena);

            am.saveArenas();

            pl.sendMessage("Арена " + arena + " успаешно создана!");
        }
        catch (Exception e) {
            pl.sendMessage(e.getMessage());
        }
        
        return true;
    }

    @CmdInfo(name = "join", description = "Присоединиться к указанной арене.", usage = "/tntrun join <арена>", permission = "")
    public boolean join(CommandSender cs, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("Данная команда только для игроков.");
            return true;
        }
        if (args.length < 1 || args.length > 1) {
            return false;
        }
        String arena = args[0];
        Player pl = (Player) cs;

        try {
            TntRun.getInstance().getArenaManager().joinArena(pl, new PlayingPlayer(), arena);
            
            pl.sendMessage("Вы присоединились к арене " + arena + "!");
        } 
        catch (TntRunException e) {
            pl.sendMessage(e.getMessage());
        }
        
        return true;
    }
    
    @CmdInfo(name = "leave", description = "Покинуть игру.", usage = "/tntrun leave", permission = "")
    public boolean leave(CommandSender cs, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("Данная команда только для игроков.");
            return true;
        }
        if (args.length < 0 || args.length > 0) {
            return false;
        }
        Player pl = (Player) cs;

        try {
            TntRun.getInstance().getArenaManager().leaveArena(pl);
            
            pl.sendMessage("Вы покинули игру!");
        } 
        catch (TntRunException e) {
            pl.sendMessage(e.getMessage());
        }
        
        return true;
    }
}
