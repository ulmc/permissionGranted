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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.ulmc.plugins.permissionGranted.commands.ForgetCommand;
import ru.ulmc.plugins.permissionGranted.commands.GetCommand;
import ru.ulmc.plugins.permissionGranted.commands.InfoCommand;
import ru.ulmc.plugins.permissionGranted.commands.ListCommand;
import ru.ulmc.plugins.permissionGranted.model.Profession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bormoshka
 * 24.09.2014
 *
 * Приобретение пермишена игроком.
 *
 */
public class PermissionGranted extends JavaPlugin {

	@Override
	public void onEnable() {
		if (!setupEconomy() ) {
			getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		setupPermissions();
		setupChat();
		initConfig();
		initCommands();
	}

	@Override
	public void onDisable() {
		getLogger().info("PermissionGranted is Disabled");
	}

	public void initCommands() {
		R.commands.put(GetCommand.getCommandName(), new GetCommand());
		R.commands.put(ListCommand.getCommandName(), new ListCommand());
		R.commands.put(InfoCommand.getCommandName(), new InfoCommand());
		R.commands.put(ForgetCommand.getCommandName(), new ForgetCommand());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ("levelUp".equalsIgnoreCase(cmd.getName())) {
			if(args.length >= 1 && R.commands.containsKey(args[0].toLowerCase())) {
				getLogger().info(R.commands.get(args[0].toLowerCase()).toString());
				return R.commands.get(args[0].toLowerCase()).onCommand(sender, cmd, label, args);
			}
			return false;
		}

		return false;
	}

	protected void initConfig() {
		R.config = getConfig();
		R.config.addDefault(R.MSG_WRONG_ARGUMENTS, "Неправильная команда!");
		R.config.addDefault(R.MSG_WRONG_PROFESSION, "Не могу этому научить!");
		R.config.addDefault(R.MSG_GROUPS_AVAILABLE, "Доступны для изучения: ");
		R.config.addDefault(R.MSG_ALREADY_HAVE, "Уже изучено! ");
		R.config.addDefault(R.MSG_MISSED_REQUIREMENT, "Невозможно получить профессию — не хватает след. знаний: ");
		R.config.addDefault(R.MSG_OPPOSITES_FOUND, "Невозможно получить профессию — найдены конфликты: ");
		R.config.addDefault(R.MSG_FOUNDS_INSUFFICIENCY, "Недостаточно средств!");
		R.config.addDefault(R.MSG_YOU_DONT_HAVE_ANY, "Нет ни одной группы!");
		R.config.addDefault(R.MSG_YOU_HAVE, "Есть следующие группы: ");
		R.config.addDefault(R.MSG_LEVEL_UPPED, "Получены новые знания! ");
		R.config.addDefault(R.MSG_FORGOTTEN, "Знания забыты! ");
		R.config.addDefault(R.MSG_ERROR, "Произошла ошибка! ");
		R.config.addDefault(R.PARAM_PER_GROUP_MULTIPLIER, 1);
		R.config.addDefault(R.PARAM_PER_WAY_MULTIPLIER, 1);
		R.config.addDefault(R.PARAM_MAX_WAYS, 2);
		R.config.addDefault(R.PARAM_FORGET_INTERNAL_ONLY, true);
		//R.config.addDefault("economy.bank-name", " ");

		if(R.config.getConfigurationSection(R.PARAM_GROUPS) == null) {
			R.config.addDefault(R.PARAM_GROUPS + ".sampleWay.sampleGroup." + R.PARAM_GROUP_DESCRIPTION, "desc here");
			R.config.addDefault(R.PARAM_GROUPS + ".sampleWay.sampleGroup." + R.PARAM_GROUP_COST, 0.1d);
			R.config.addDefault(R.PARAM_GROUPS + ".sampleWay.sampleGroup." + R.PARAM_GROUP_REQUIRED, new ArrayList<String>());
			R.config.addDefault(R.PARAM_GROUPS + ".sampleWay.sampleGroup." + R.PARAM_GROUP_OPPOSITES, new ArrayList<String>());
			R.config.addDefault(R.PARAM_GROUPS + ".sampleWay.sampleGroup." + R.PARAM_GROUP_RUDIMENTS, new ArrayList<String>());
			getLogger().warning("No groups detected! Nothing to do =(");
		} else {
			for(String key : R.config.getConfigurationSection(R.PARAM_GROUPS).getKeys(false)) { // ways
				ConfigurationSection cs = R.config.getConfigurationSection(R.PARAM_GROUPS).getConfigurationSection(key);
				for(String profName : cs.getKeys(false)) { //profs
					Profession prof = new Profession();
					prof.setWay(key);
					prof.setName(profName);
					ConfigurationSection profSection = cs.getConfigurationSection(profName);
					for(String field : profSection.getKeys(false)) {
						if(R.PARAM_GROUP_DESCRIPTION.equals(field)) {
							prof.setDescription(profSection.getString(field));
						} else if(R.PARAM_GROUP_COST.equals(field)) {
							prof.setCost(profSection.getDouble(field));
						} else if(R.PARAM_GROUP_OPPOSITES.equals(field)) {
							prof.setOpposites(profSection.getStringList(field));
						} else if(R.PARAM_GROUP_RUDIMENTS.equals(field)) {
							prof.setRudiments(profSection.getStringList(field));
						} else if(R.PARAM_GROUP_REQUIRED.equals(field)) {
							prof.setRequired(profSection.getStringList(field));
						}
					}
					R.professions.put(profName, prof);
				}
			}
		}

		R.MAX_WAYS = R.config.getInt(R.PARAM_MAX_WAYS);

		R.config.options().copyDefaults(true);
		saveConfig();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		R.econ = rsp.getProvider();
		return R.econ != null;
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		R.chat = rsp.getProvider();
		return R.chat != null;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		R.perms = rsp.getProvider();
		return R.perms != null;
	}
}
