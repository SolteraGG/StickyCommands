package com.dumbdogdiner.stickycommands.api.economy;

public class Listing {
	@Getter
	private Material material;

	@Getter
	private Double price;

	public Listing(Material material, Double price);
}
