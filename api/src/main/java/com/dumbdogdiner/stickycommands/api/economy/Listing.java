package com.dumbdogdiner.stickycommands.api.economy;

import lombok.Getter;
import org.bukkit.Material;

public class Listing {
	@Getter
	private Material material;

	@Getter
	private Double price;

	public Listing(Material material, Double price) {

	}
}
