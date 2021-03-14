package ru.boomearo.tntrun.commands.tntrun;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
import ru.boomearo.tntrun.objects.TntTeam;
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
            pl.sendMessage(TntRunManager.prefix + "Выделите регион!");
            return true;
        }

        ConcurrentMap<Integer, TntTeam> teams = new ConcurrentHashMap<Integer, TntTeam>();
        
        int maxPlayers = 15;
        
        for (int i = 1; i <= maxPlayers; i++) {
            teams.put(i, new TntTeam(i, null));
        }
        
        try {
            TntArena newArena = new TntArena(arena, 2, maxPlayers, 300, pl.getWorld(), new CuboidRegion(re.getMaximumPoint(), re.getMinimumPoint(), pl.getWorld()), teams, pl.getLocation(), null);
            
            TntRunManager am = TntRun.getInstance().getTntRunManager();
            am.addArena(newArena);

            am.saveArenas();

            pl.sendMessage(TntRunManager.prefix + "Арена '§c" + arena + "§7' успешно создана!");
        }
        catch (Exception e) {
            pl.sendMessage(e.getMessage());
        }
        
        return true;
    }
    
    @CmdInfo(name = "setspawnpoint", description = "Установить точку спавна в указанной арене указанной команде.", usage = "/tntrun setspawnpoint <арена> <ид>", permission = "tntrun.admin")
    public boolean setspawnpoint(CommandSender cs, String[] args) {
        if (!(cs instanceof Player)) {
            cs.sendMessage("Данная команда только для игроков.");
            return true;
        }
        if (args.length < 2 || args.length > 2) {
            return false;
        }
        String arena = args[0];
        Player pl = (Player) cs;

        TntRunManager trm = TntRun.getInstance().getTntRunManager();
        TntArena ar = trm.getGameArena(arena);
        if (ar == null) {
            cs.sendMessage(TntRunManager.prefix + "Арена '§c" + arena + "§7' не найдена!");
            return true;
        }
        
        Integer id = null;
        try {
            id = Integer.parseInt(args[1]);
        }
        catch (Exception e) {}
        if (id == null) {
            cs.sendMessage(TntRunManager.prefix + "Аргумент должен быть цифрой!");
            return true;
        }
        
        TntTeam team = ar.getTeamById(id);
        if (team == null) {
            cs.sendMessage(TntRunManager.prefix + "Команда §c" + id + " §7не найдена!");
            return true;
        }
        
        team.setSpawnPoint(pl.getLocation().clone());
        
        trm.saveArenas();
        
        cs.sendMessage(TntRunManager.prefix + "Спавн поинт §c" + id + " §7успешно добавлен!");
        
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
            pl.sendMessage(TntRunManager.prefix + "§cОшибка: §7" + e.getMessage());
        }
        catch (ConsoleGameException e) {
            e.printStackTrace();
            pl.sendMessage(TntRunManager.prefix + "§cПроизошла ошибка, сообщите администрации!");
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
            pl.sendMessage(TntRunManager.prefix + "§cОшибка: §7" + e.getMessage());
        }
        catch (ConsoleGameException e) {
            e.printStackTrace();
            pl.sendMessage(TntRunManager.prefix + "§cПроизошла ошибка, сообщите администрации!");
        }
        return true;
    }
    
    @CmdInfo(name = "list", description = "Показать список всех доступных арен.", usage = "/tntrun list", permission = "")
    public boolean list(CommandSender cs, String[] args) {
        if (args.length < 0 || args.length > 0) {
            return false;
        }
        
        Collection<TntArena> arenas = TntRun.getInstance().getTntRunManager().getAllArenas();
        if (arenas.isEmpty()) {
            cs.sendMessage(TntRunManager.prefix + "Арены еще не созданы!");
            return true;
        }
        
        final String sep = TntRunManager.prefix + "§8============================";
        cs.sendMessage(sep);
        for (TntArena arena : arenas) {
            cs.sendMessage(TntRunManager.prefix + "Арена: '§c" + arena.getName() + "§7'. Статус: " + arena.getState().getName() + "§7. Игроков: " + TntRunManager.getRemainPlayersArena(arena));
        }
        cs.sendMessage(sep);
        
        return true;
    }
}
