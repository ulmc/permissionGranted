/*
 * *
 *  * Copyright (C) 2014 Kolmogorov Alexey
 *  *
 *  * This file part of ulmc.ru ModPack
 *  *
 *  * ulmc.ru ModPack is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * ulmc.ru ModPack is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see [http://www.gnu.org/licenses/].
 *  *
 *
 */

package ru.ulmc.plugins.permissionGranted.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ulmc.plugins.permissionGranted.R;
import ru.ulmc.plugins.permissionGranted.model.Profession;

import java.util.List;
import java.util.Map;

/**
 * Created by 45 on 26.09.2014.
 */
public class InfoCommand extends UCommand{
	private static String commandName = "info";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1) {
			if (sender instanceof Player) {
				Map<String, List<String>> profs = getPlayerProfessions((Player) sender);
				double cost;
				for(Profession prof : R.professions.values()) {
					cost = getProfessionCostForPlayer(prof.getName(), profs);
					sender.sendMessage(prof.getWay().concat(" - ").concat(prof.getName()).concat(": ")
							.concat(prof.getDescription()).concat(" " + cost + " (" + prof.getCost() + "); "));
				}
			} else {
				for(Profession prof : R.professions.values()) {
					sender.sendMessage(prof.getWay().concat(" - ").concat(prof.getName()).concat(": ")
							.concat(prof.getDescription()).concat(" (" + prof.getCost() + ");"));
				}
			}
			return true;
		}
		if(args.length >= 2 || args[1].isEmpty())
			return false;
		String profession = args[1];
		if(R.professions.containsKey(profession)) {
			if (sender instanceof Player) {
				Profession prof = R.professions.get(profession);
				double cost = getProfessionCostForPlayer((Player) sender, profession);
				sender.sendMessage(prof.getWay().concat(" - ").concat(prof.getName()).concat(": ")
						.concat(prof.getDescription()).concat(" " + cost + " (" + prof.getCost() + "); "));
			} else {
				Profession prof = R.professions.get(profession);
				sender.sendMessage(prof.getWay().concat(" - ").concat(prof.getName()).concat(": ")
						.concat(prof.getDescription()).concat(" (" + prof.getCost() + ");"));
			}

			return true;
		}
		sender.sendMessage(R.config.getString(R.MSG_WRONG_ARGUMENTS));
		return false;
	}

	public static String getCommandName() {
		return commandName;
	}
}
