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

package ru.ulmc.plugins.permissionGranted;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.configuration.file.FileConfiguration;
import ru.ulmc.plugins.permissionGranted.commands.UCommand;
import ru.ulmc.plugins.permissionGranted.model.Profession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 45 on 27.09.2014.
 */
public class R {
	public static FileConfiguration config;
	public static Economy econ = null;
	public static Permission perms = null;
	public static Chat chat = null;
	public static Map<String, Profession> professions = new HashMap<>();
	public static Map<String, UCommand> commands = new HashMap<>();

	public static int MAX_WAYS = 1;


	public static final String MSG_WRONG_ARGUMENTS = "msg.wrong-arguments";
	public static final String MSG_WRONG_PROFESSION = "msg.wrong-profession";
	public static final String MSG_GROUPS_AVAILABLE = "msg.avail-groups";
	public static final String MSG_ALREADY_HAVE = "msg.already-have";
	public static final String MSG_MISSED_REQUIREMENT = "msg.requirements-missed";
	public static final String MSG_OPPOSITES_FOUND = "msg.found-opposites";
	public static final String MSG_FOUNDS_INSUFFICIENCY = "msg.founds-insufficiency";
	public static final String MSG_YOU_DONT_HAVE_ANY = "msg.have-no-group";
	public static final String MSG_FORGOTTEN = "msg.forgotten";
	public static final String MSG_YOU_HAVE = "msg.have-group";
	public static final String MSG_LEVEL_UPPED = "msg.level-upped";
	public static final String MSG_ERROR = "msg.error";

	public static final String PARAM_MULTIPLIER = "multipliers";
	public static final String PARAM_PER_GROUP_MULTIPLIER = PARAM_MULTIPLIER + ".per-group-multiplier";
	public static final String PARAM_PER_WAY_MULTIPLIER = PARAM_MULTIPLIER + ".per-way-multiplier";
	public static final String PARAM_FORGET_MULTIPLIER = PARAM_MULTIPLIER + ".forget-multiplier";
	public static final String PARAM_MAX_WAYS = "params.maxWays";
	public static final String PARAM_USE_GLOBAL = "params.use-global-groups";
	public static final String PARAM_FORGET_INTERNAL_ONLY = "params.forget-internal-only";
	public static final String ECONOMY_BANK_NAME = "economy.bank-name";
	public static final String PARAM_GROUPS = "groups";
	public static final String PARAM_GROUP_DESCRIPTION = "description";
	public static final String PARAM_GROUP_COST = "cost";
	public static final String PARAM_GROUP_OPPOSITES = "opposites";
	public static final String PARAM_GROUP_RUDIMENTS = "rudiments";
	public static final String PARAM_GROUP_REQUIRED = "required";
}
