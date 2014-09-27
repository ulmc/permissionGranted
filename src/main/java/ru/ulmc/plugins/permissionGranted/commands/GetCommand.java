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
import ru.ulmc.plugins.permissionGranted.common.CommonException;
import ru.ulmc.plugins.permissionGranted.model.Profession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 45 on 26.09.2014.
 */
public class GetCommand extends UCommand {
	private static String commandName = "get";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length != 2 || args[1].isEmpty() || !(sender instanceof Player))
			return false;
		String profession = args[1];
		Player player = (Player) sender;
		if (profession == null || !R.professions.containsKey(profession)) {
			player.sendMessage(R.config.getString(R.MSG_WRONG_PROFESSION));
			return true;
		}
		if (R.perms.playerInGroup(player, profession)) {
			player.sendMessage(R.config.getString(R.MSG_ALREADY_HAVE));
			return true;
		}
		Map<String, List<String>> profs;
		try {
			profs = canGetProfession(player, profession);
		} catch (CommonException e) {
			return true;
		}
		double cost = getProfessionCostForPlayer(profession, profs);
		if (checkAndPay(player, cost)) {
			R.perms.playerAddGroup(player, profession);
			List<String> rudiments = R.professions.get(profession).getRudiments();
			if (rudiments != null && !rudiments.isEmpty()) {
				for (String rudi : rudiments) {
					if(!R.perms.playerRemoveGroup(player, rudi)) {
						log.warning("Can't remove from group: " + player.getName() + " ["+ profession + "]");
						player.sendMessage(R.config.getString(R.MSG_ERROR));
						return true;
					}
				}
			}
			player.sendMessage(R.config.getString(R.MSG_LEVEL_UPPED) + " (" + profession + ") -"  + cost);
			log.info("PG LEVEL UP: " + player.getName() + " for " + cost + " on " + profession);
			return true;
		} else {
			player.sendMessage(R.config.getString(R.MSG_FOUNDS_INSUFFICIENCY));
		}
		return false;
	}

	protected Map<String, List<String>> canGetProfession(Player player, String newProfession) throws CommonException {
		Map<String, List<String>> profs = getPlayerProfessions(player);
		Profession target = R.professions.get(newProfession);
		if ((profs.containsKey(target.getWay())) || (!profs.containsKey(target.getWay()) && !(profs.size() > R.MAX_WAYS - 1))) {
			if (target.getRequired() != null && !target.getRequired().isEmpty()) {
				List<String> notFoundRequirements = new ArrayList<>();
				for (String required : target.getRequired()) {
					if (!R.perms.playerInGroup(player, required)) {
						notFoundRequirements.add(required);
					}
				}
				if (!notFoundRequirements.isEmpty()) {
					String message = R.config.getString(R.MSG_MISSED_REQUIREMENT);
					for (String missed : notFoundRequirements) {
						message += missed + "; ";
					}
					player.sendMessage(message);
					throw new CommonException();
				}
			}
			if (target.getOpposites() != null && !target.getOpposites().isEmpty()) {
				List<String> foundOpposites = new ArrayList<>();
				for (String opposite : target.getOpposites()) {
					if (R.perms.playerInGroup(player, opposite)) {
						foundOpposites.add(opposite);
					}
				}
				if (!foundOpposites.isEmpty()) {
					String message = R.config.getString(R.MSG_OPPOSITES_FOUND);
					for (String found : foundOpposites) {
						message.concat(found + "; ");
					}
					player.sendMessage(message);
					throw new CommonException();
				}
			}
			return profs;
		}

		throw new CommonException();
	}

	public static String getCommandName() {
		return commandName;
	}
}
