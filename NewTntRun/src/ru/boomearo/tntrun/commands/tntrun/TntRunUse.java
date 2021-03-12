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

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.ConsoleGameException;
import ru.boomearo.gamecontrol.exceptions.PlayerGameException;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.commands.CmdInfo;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.region.CuboidRegion;

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
            TntArena newArena = new TntArena(arena, 2, 15, 300, pl.getWorld(), new CuboidRegion(re.getMaximumPoint(), re.getMinimumPoint(), pl.getWorld()), spawnPoints, pl.getLocation(), null);
            
            TntRunManager am = TntRun.getInstance().getTntRunManager();
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
            GameControl.getInstance().getGameManager().joinGame(pl, TntRun.class, arena);
        } 
        catch (PlayerGameException e) {
            pl.sendMessage(TntRunManager.prefix + "Ошибка: " + e.getMessage());
        }
        catch (ConsoleGameException e) {
            e.printStackTrace();
            pl.sendMessage(TntRunManager.prefix + "Произошла ошибка, сообщите администрации!");
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
            GameControl.getInstance().getGameManager().leaveGame(pl);
        } 
        catch (PlayerGameException e) {
            pl.sendMessage(TntRunManager.prefix + "Ошибка: " + e.getMessage());
        }
        catch (ConsoleGameException e) {
            e.printStackTrace();
            pl.sendMessage(TntRunManager.prefix + "Произошла ошибка, сообщите администрации!");
        }
        return true;
    }
    
    @CmdInfo(name = "list", description = "Показать список всех доступных арен.", usage = "/tntrun list", permission = "")
    public boolean list(CommandSender cs, String[] args) {
        if (args.length < 0 || args.length > 0) {
            return false;
        }
        
        final String sep = TntRunManager.prefix + "§8============================";
        cs.sendMessage(sep);
        for (TntArena arena : TntRun.getInstance().getTntRunManager().getAllArenas()) {
            cs.sendMessage(TntRunManager.prefix + "Арена: '§c" + arena.getName() + "§f'. Статус: " + arena.getState().getName() + "§f. Игроков: " + TntRunManager.getRemainPlayersArena(arena));
        }
        cs.sendMessage(sep);
        
        return true;
    }
}
