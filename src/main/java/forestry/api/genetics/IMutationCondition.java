/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

import forestry.api.climate.IClimateProvider;

public interface IMutationCondition {
	/**
	 * Returns a float from 0 to 1 representing the chance for mutation to occur.
	 * Most will return 1 if the condition is met and 0 otherwise,
	 * but the float offers flexibility for more advanced conditions.
	 */
	float getChance(Level level, BlockPos pos, ISpecies<?> firstParent, ISpecies<?> secondParent, IGenome firstGenome, IGenome secondGenome, IClimateProvider climate);

	/**
	 * A localized description of the mutation condition. (i.e. "A temperature of HOT is required.")
	 */
	Component getDescription();
}
