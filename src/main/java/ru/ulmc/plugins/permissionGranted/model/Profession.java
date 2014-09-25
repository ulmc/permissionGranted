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

package ru.ulmc.plugins.permissionGranted.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 45 on 25.09.2014.
 */
public class Profession implements Serializable {
	private String name;
	private String way;
	private String description;
	private Double cost;
	private List<String> rudiments;
	private List<String> opposites;

	public Profession() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public List<String> getRudiments() {
		return rudiments;
	}

	public void setRudiments(List<String> rudiments) {
		this.rudiments = rudiments;
	}

	public List<String> getOpposites() {
		return opposites;
	}

	public void setOpposites(List<String> opposites) {
		this.opposites = opposites;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public String printString() {
		return  name + " (" + description + " cost: " + cost + ")";
	}
}
