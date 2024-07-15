/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.core;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;

import forestry.api.ForestryConstants;
import forestry.api.ForestryTags;

/**
 * Many things Forestry use temperature and humidity of a biome to determine whether they can or how they can work or spawn at a given location.
 * <p>
 * This enum concerns temperature.
 */
public enum TemperatureType {
	ICY(ForestryTags.Biomes.ICY_TEMPERATURE, "habitats/snow", 0xaafff0),
	COLD(ForestryTags.Biomes.COLD_TEMPERATURE, "habitats/taiga", 0x72ddf7),
	NORMAL(ForestryTags.Biomes.NORMAL_TEMPERATURE, "habitats/plains", 0xffd013),
	WARM(ForestryTags.Biomes.WARM_TEMPERATURE, "habitats/jungle", 0xfb8a24),
	HOT(ForestryTags.Biomes.HOT_TEMPERATURE, "habitats/desert", 0xd61439),
	HELLISH(ForestryTags.Biomes.HELLISH_TEMPERATURE, "habitats/nether", 0x81032d);

	public static final List<TemperatureType> VALUES = List.of(values());

	public final TagKey<Biome> tag;
	public final ResourceLocation iconTexture;
	public final int color;

	TemperatureType(TagKey<Biome> tag, String iconTexture, int color) {
		this.tag = tag;
		this.iconTexture = new ResourceLocation(ForestryConstants.MOD_ID, iconTexture);
		this.color = color;
	}

	/**
	 * @return The temperature one tolerance step above, going no higher than {@link #HELLISH}.
	 */
	public TemperatureType up() {
		return up(1);
	}

	/**
	 * @return The temperature for any number of tolerance steps above, going no higher than {@link #HELLISH}.
	 */
	public TemperatureType up(int steps) {
		return VALUES.get(Mth.clamp(ordinal() + steps, 0, 5));
	}

	/**
	 * @return The temperature one tolerance step below, going no lower than {@link #ICY}.
	 */
	public TemperatureType down() {
		return down(1);
	}

	/**
	 * @return The temperature for any number of tolerance step below, going no lower than {@link #ICY}.
	 */
	public TemperatureType down(int steps) {
		return VALUES.get(Mth.clamp(ordinal() - steps, 0, 5));
	}

	/**
	 * @return If this temperature is warmer than or equal to another temperature.
	 */
	public boolean isWarmerOrEqual(TemperatureType other) {
		return ordinal() >= other.ordinal();
	}

	/**
	 * @return If this temperature is cooler than or equal to another temperature.
	 */
	public boolean isCoolerOrEqual(TemperatureType other) {
		return ordinal() <= other.ordinal();
	}

	/**
	 * @return The temperature type corresponding to a biome's base temperature value.
	 * @see forestry.api.climate.IClimateManager#getTemperature if you know the biome.
	 */
	public static TemperatureType getFromValue(float baseTemperature) {
		if (baseTemperature > 1.00f) {
			return HOT;
		} else if (baseTemperature > 0.85f) {
			return WARM;
		} else if (baseTemperature > 0.35f) {
			return NORMAL;
		} else if (baseTemperature > 0.0f) {
			return COLD;
		} else {
			return ICY;
		}
	}
}
