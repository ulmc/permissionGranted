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
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

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
	protected FileConfiguration config;
	protected Map<String, Double> professions = new HashMap<>();
	public static Economy econ = null;
	public static Permission perms = null;
	public static Chat chat = null;

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
		for(String key : config.getConfigurationSection("perm.group").getKeys(true)) {
			professions.put(key, config.getConfigurationSection("perm.group").getDouble(key));
		}
		getLogger().info(getAvailableProfessions());
	}

	@Override
	public void onDisable() {
		getLogger().info("PermissionGranted is Disabled");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if ("teachMe".equalsIgnoreCase(cmd.getName())) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command can only be run by a player.");
			} else {
				if(args.length != 1) {
					sender.sendMessage(config.getString("msg.wrong-arguments"));
					return false;
				}
				return workWithPlayerCommand((Player) sender, args[0]);
			}
			return false;
		} else if ("teachList".equalsIgnoreCase(cmd.getName())) {
			getAvailableProfessions((Player) sender);
			return true;
		}
		return false;
	}

	protected boolean workWithPlayerCommand(Player player, String profession) {
		if(profession == null || !professions.containsKey(profession)) {
			player.sendMessage(config.getString("msg.wrong-profession"));
			player.sendMessage(getAvailableProfessions(player));
			return false;
		}
		if(perms.playerInGroup(player, profession)) {
			player.sendMessage(config.getString("msg.already-have"));
			return false;
		}
		Double cost = professions.get(profession);
		if(econ.has(player, cost)) {
			econ.withdrawPlayer(player, cost);
			if(econ.hasBankSupport() && config.getString("econony.bank-name") != null &&
					config.getString("econony.bank-name").trim().isEmpty()) {
				econ.bankDeposit(config.getString("econony.bank-name").trim(), cost);
			}
			perms.playerAddGroup(player, profession);
			getLogger().info("PG TEACH: " + player.getName() + " for " + cost + " on " + profession);
			return true;
		}

		return false;
	}

	protected String getAvailableProfessions() {
		String groups = config.getString("msg.avail-groups");
		for(String key : professions.keySet()) {
			groups += key + " (" + professions.get(key) + "); ";
		}
		return groups;
	}

	protected String getAvailableProfessions(Player player) {
		String groups = config.getString("msg.avail-groups");
		double mltplr = getMultiplier(player);
		for(String key : professions.keySet()) {
			groups += key + " (" + Math.floor(professions.get(key) * mltplr) + "); ";
		}
		return groups;
	}

	protected double getMultiplier(Player player) {
		int count = 0;
		for(String prof : professions.keySet()) {
			if(perms.playerInGroup(player, prof)) {
				count++;
			}
		}
		switch (count) {
			case 1:
				return config.getDouble("multiplier.first");
			case 2:
				return config.getDouble("multiplier.second");
			case 3:
				return config.getDouble("multiplier.third");
			case 4:
				return config.getDouble("multiplier.fourth");
			case 5:
				return config.getDouble("multiplier.fifth");
			default:
				return 9999;
		}
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
		return chat != null;
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	protected void initConfig() {
		config = getConfig();
		config.addDefault("msg.wrong-arguments", "Неправильная комманда!");
		config.addDefault("msg.wrong-profession", "Не могу этому научить!");
		config.addDefault("msg.avail-groups", "Доступны для изучения: ");
		config.addDefault("msg.already-have", "Уже изучено! ");
		config.addDefault("multiplier.first", 1.0d);
		config.addDefault("multiplier.second", 1.5d);
		config.addDefault("multiplier.third", 2.5d);
		config.addDefault("multiplier.fourth", 4.5d);
		config.addDefault("multiplier.fifth", 6.0d);
		config.addDefault("econony.bank-name", " ");

		Map<String, Object> example = new HashMap<>();
		example.put("perm.group.lj", 1000.0D);
		example.put("perm.group.bs", 1500.0D);
		example.put("perm.group.en", 1800.0D);
		example.put("perm.group.exp-bs", 1900.0D);

		config.addDefaults(example);
		config.options().copyDefaults(true);
		saveConfig();
	}
}
