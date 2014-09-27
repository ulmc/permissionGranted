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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by 45 on 27.09.2014.
 */
public abstract class UCommand {
	protected static final Logger log = Logger.getLogger("PermGrant");

	public abstract boolean onCommand(CommandSender sender, Command cmd, String label, String[] args);

	/**
	 * Проверяет есть ли у игрока необходимая сумма, списывает её и направляет в банк, если нужно.
	 * При отричательной сумме делает наоборот, начисляет игроку, списывает у банка.
	 * Проверка на наличие средств у банка не осуществляется.
	 *
	 * @param player
	 * @param cost
	 * @return
	 */
	protected boolean checkAndPay(Player player, double cost) {
		if (cost < 0) {
			R.econ.depositPlayer(player, Math.abs(cost));
			if (R.econ.hasBankSupport() && R.config.getString(R.ECONOMY_BANK_NAME) != null &&
					R.config.getString(R.ECONOMY_BANK_NAME).trim().isEmpty()) {
				R.econ.bankWithdraw(R.config.getString(R.ECONOMY_BANK_NAME).trim(), Math.abs(cost));
			}
			return true;
		} else if (R.econ.has(player, cost)) {
			R.econ.withdrawPlayer(player, cost);
			if (R.econ.hasBankSupport() && R.config.getString(R.ECONOMY_BANK_NAME) != null &&
					R.config.getString(R.ECONOMY_BANK_NAME).trim().isEmpty()) {
				R.econ.bankDeposit(R.config.getString(R.ECONOMY_BANK_NAME).trim(), cost);
			}
			return true;
		}
		return false;
	}

	protected Map<String, List<String>> getPlayerProfessions(Player player) {
		Map<String, List<String>> profs = new HashMap<>();
		//String[] playersProfs = R.perms.getPlayerGroups(player); // DOESN'T WORK!
		for (String profName : R.professions.keySet()) {
			if (R.perms.playerInGroup(player, profName)) {
				Profession prof = R.professions.get(profName);
				if (profs.containsKey(prof.getWay())) {
					profs.get(prof.getWay()).add(prof.getName());
				} else {
					List<String> profList = new ArrayList<>();
					profList.add(prof.getName());
					profs.put(prof.getWay(), profList);
				}
			}
		}
		return profs;
	}

	protected double getProfessionCostForPlayer(Player player, String profession) {
		return getProfessionCostForPlayer(profession, getPlayerProfessions(player));
	}

	protected double getProfessionCostForPlayer(String profession, Map<String, List<String>> profs) {
		Double cost;
		if (profs != null && profs.size() >= 1) {
			int profCount = 0;
			for (List<String> list : profs.values()) {
				profCount += list.size();
			}
			cost = R.professions.get(profession).getCost();
			cost += cost * R.config.getDouble(R.PARAM_PER_GROUP_MULTIPLIER) * profCount +
					cost * R.config.getDouble(R.PARAM_PER_WAY_MULTIPLIER) * (profs.size() - 1);
		} else {
			cost = R.professions.get(profession).getCost();
		}
		return cost;
	}
}
