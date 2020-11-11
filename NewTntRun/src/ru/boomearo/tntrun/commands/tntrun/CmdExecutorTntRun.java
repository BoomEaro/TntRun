package ru.boomearo.tntrun.commands.tntrun;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import ru.boomearo.tntrun.TntRun;
import ru.boomearo.tntrun.commands.AbstractExecutor;
import ru.boomearo.tntrun.commands.CmdList;

public class CmdExecutorTntRun extends AbstractExecutor {

	public CmdExecutorTntRun() {
		super(new TntRunUse());
	}

	@Override
	public boolean zeroArgument(CommandSender sender, CmdList cmds) {
		cmds.sendUsageCmds(sender);
		return true;
	}

	private static final List<String> empty = new ArrayList<>();

	@Override
	public List<String> onTabComplete(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
	    //TODO
		return empty;
	}

	@Override
	public String getPrefix() {
		return TntRun.prefix;
	}

	@Override
	public String getSuffix() {
		return " §8-§c ";
	}
}
