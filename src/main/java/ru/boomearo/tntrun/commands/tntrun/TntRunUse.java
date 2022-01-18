package ru.boomearo.tntrun.commands.tntrun;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.regions.Region;

import ru.boomearo.gamecontrol.GameControl;
import ru.boomearo.gamecontrol.exceptions.ConsoleGameException;
import ru.boomearo.gamecontrol.exceptions.PlayerGameException;
import ru.boomearo.gamecontrol.objects.region.CuboidRegion;
import ru.boomearo.serverutils.utils.other.commands.CmdInfo;
import ru.boomearo.serverutils.utils.other.commands.Commands;
import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.managers.TntRunManager;
import ru.boomearo.tntrun.objects.TntArena;
import ru.boomearo.tntrun.objects.TntTeam;

public class TntRunUse implements Commands {

    @CmdInfo(name = "createarena", description = "Создать арену с указанным названием.", usage = "/tntrun createarena <название>", permission = "tntrun.admin")
    public boolean createarena(CommandSender cs, String[] args) {
        if (!(cs instanceof Player pl)) {
            cs.sendMessage("Данная команда только для игроков.");
            return true;
        }
        if (args.length != 1) {
            return false;
        }
        String arena = args[0];

        BukkitPlayer bPlayer = BukkitAdapter.adapt(pl);
        LocalSession ls = WorldEdit.getInstance().getSessionManager().get(bPlayer);
        if (ls == null) {
            pl.sendMessage(TntRunManager.prefix + "Выделите регион!");
            return true;
        }
        Region re = null;
        try {
            re = ls.getSelection(ls.getSelectionWorld());
        }
        catch (IncompleteRegionException ignored) {
        }
        if (re == null) {
            pl.sendMessage(TntRunManager.prefix + "Выделите регион!");
            return true;
        }

        ConcurrentMap<Integer, TntTeam> teams = new ConcurrentHashMap<>();

        int maxPlayers = 15;

        for (int i = 1; i <= maxPlayers; i++) {
            teams.put(i, new TntTeam(i, null));
        }

        try {
            TntArena newArena = new TntArena(arena, pl.getWorld(), Material.STONE, GameControl.normalizeLocation(pl.getLocation()), 2, maxPlayers, 300, new CuboidRegion(re.getMaximumPoint(), re.getMinimumPoint(), pl.getWorld()), teams);

            TntRunManager am = TntRun.getInstance().getTntRunManager();
            am.addArena(newArena);

            am.saveArenas();

            pl.sendMessage(TntRunManager.prefix + "Арена '" + TntRunManager.variableColor + arena + TntRunManager.mainColor + "' успешно создана!");
        }
        catch (Exception e) {
            pl.sendMessage(e.getMessage());
        }

        return true;
    }

    @CmdInfo(name = "setspawnpoint", description = "Установить точку спавна в указанной арене указанной команде.", usage = "/tntrun setspawnpoint <арена> <ид>", permission = "tntrun.admin")
    public boolean setspawnpoint(CommandSender cs, String[] args) {
        if (!(cs instanceof Player pl)) {
            cs.sendMessage("Данная команда только для игроков.");
            return true;
        }
        if (args.length != 2) {
            return false;
        }
        String arena = args[0];

        TntRunManager trm = TntRun.getInstance().getTntRunManager();
        TntArena ar = trm.getGameArena(arena);
        if (ar == null) {
            cs.sendMessage(TntRunManager.prefix + "Арена '" + TntRunManager.variableColor + arena + TntRunManager.mainColor + "' не найдена!");
            return true;
        }

        Integer id = null;
        try {
            id = Integer.parseInt(args[1]);
        }
        catch (Exception ignored) {
        }
        if (id == null) {
            cs.sendMessage(TntRunManager.prefix + "Аргумент должен быть цифрой!");
            return true;
        }

        TntTeam team = ar.getTeamById(id);
        if (team == null) {
            cs.sendMessage(TntRunManager.prefix + "Команда " + TntRunManager.variableColor + id + TntRunManager.mainColor + " не найдена!");
            return true;
        }

        team.setSpawnPoint(GameControl.normalizeRotation(pl.getLocation()));

        trm.saveArenas();

        cs.sendMessage(TntRunManager.prefix + "Спавн поинт " + TntRunManager.variableColor + id + TntRunManager.mainColor + " успешно добавлен!");

        return true;
    }

    @CmdInfo(name = "join", description = "Присоединиться к указанной арене.", usage = "/tntrun join <арена>", permission = "")
    public boolean join(CommandSender cs, String[] args) {
        if (!(cs instanceof Player pl)) {
            cs.sendMessage("Данная команда только для игроков.");
            return true;
        }
        if (args.length != 1) {
            return false;
        }
        String arena = args[0];

        try {
            GameControl.getInstance().getGameManager().joinGame(pl, TntRun.class, arena);
        }
        catch (PlayerGameException e) {
            pl.sendMessage(TntRunManager.prefix + "§cОшибка: " + TntRunManager.mainColor + e.getMessage());
        }
        catch (ConsoleGameException e) {
            e.printStackTrace();
            pl.sendMessage(TntRunManager.prefix + "§cПроизошла ошибка, сообщите администрации!");
        }
        return true;
    }

    @CmdInfo(name = "leave", description = "Покинуть игру.", usage = "/tntrun leave", permission = "")
    public boolean leave(CommandSender cs, String[] args) {
        if (!(cs instanceof Player pl)) {
            cs.sendMessage("Данная команда только для игроков.");
            return true;
        }
        if (args.length != 0) {
            return false;
        }

        try {
            GameControl.getInstance().getGameManager().leaveGame(pl);
        }
        catch (PlayerGameException e) {
            pl.sendMessage(TntRunManager.prefix + "§cОшибка: " + TntRunManager.mainColor + e.getMessage());
        }
        catch (ConsoleGameException e) {
            e.printStackTrace();
            pl.sendMessage(TntRunManager.prefix + "§cПроизошла ошибка, сообщите администрации!");
        }
        return true;
    }

    @CmdInfo(name = "list", description = "Показать список всех доступных арен.", usage = "/tntrun list", permission = "")
    public boolean list(CommandSender cs, String[] args) {
        if (args.length != 0) {
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
            cs.sendMessage(TntRunManager.prefix + "Арена: '" + TntRunManager.variableColor + arena.getName() + "'" + TntRunManager.mainColor + ". Статус: " + arena.getState().getName() + TntRunManager.mainColor + ". Игроков: " + TntRunManager.getRemainPlayersArena(arena, null));
        }
        cs.sendMessage(sep);

        return true;
    }
}
