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

import java.util.List;
import java.util.Map;

/**
 * Created by 45 on 26.09.2014.
 */
public class ListCommand extends UCommand{
	private static String commandName = "list";

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if (sender instanceof Player) {
				Map<String, List<String>> profs = getPlayerProfessions((Player) sender);
				String message = profs.isEmpty() ? R.config.getString(R.MSG_YOU_DONT_HAVE_ANY) : " ";
				for(String key : profs.keySet()) {
					message += key + " [";
					int i = 1;
					for(String profName : profs.get(key)) {
						message += profName + (i++ == profs.get(key).size()? "]":", ");
					}
				}
				log.info(message);
				sender.sendMessage(message);
			} else {
				return false;
			}
			return true;
	}

	public static String getCommandName() {
		return commandName;
	}
}
