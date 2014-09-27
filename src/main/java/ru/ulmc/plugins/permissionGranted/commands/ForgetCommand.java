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

/**
 * Created by 45 on 26.09.2014.
 */
public class ForgetCommand extends UCommand {
	private static String commandName = "forget";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length < 2 || args[1].isEmpty() || !(sender instanceof Player))
			return false;
		String profession = args[1];
		Player player = (Player) sender;
		if (profession == null || profession.isEmpty()) {
			player.sendMessage(R.config.getString(R.MSG_WRONG_PROFESSION));
			return false;
		}
		if(R.config.getBoolean(R.PARAM_FORGET_INTERNAL_ONLY)) {
			if(R.professions.get(profession) != null) {
				double cost = R.professions.get(profession).getCost() * R.config.getDouble(R.PARAM_FORGET_INTERNAL_ONLY);
				if(checkAndPay(player, cost)) {
					if(!R.perms.playerRemoveGroup(player, profession)) {
						log.warning("Can't remove from group: " + player.getName() + " ["+ profession + "]");
						player.sendMessage(R.config.getString(R.MSG_ERROR));
						return true;
					}
					log.info("PG FORGET: " + player.getName() + " for " + cost + " forgot how to [" + profession + "]");
					player.sendMessage(R.config.getString(R.MSG_FORGOTTEN) + " [" + profession +"]");
					return true;
				}
			}
		} else {
			player.sendMessage(R.config.getString(R.MSG_FOUNDS_INSUFFICIENCY));
			return true;
		}
		sender.sendMessage(R.config.getString(R.MSG_WRONG_ARGUMENTS));
		return false;
	}

	public static String getCommandName() {
		return commandName;
	}
}
