/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.genetics;

import net.minecraft.world.level.Level;

import genetics.api.individual.IHasSecrets;
import genetics.api.individual.IIndividual;

import forestry.api.genetics.alleles.IAlleleForestrySpecies;

public interface IIndividualLiving extends IIndividual, IHasSecrets {
	/**
	 * @return Current health of the individual.
	 */
	int getHealth();

	/**
	 * Set the current health of the individual.
	 */
	void setHealth(int health);

	/**
	 * @return Maximum health of the individual.
	 */
	int getMaxHealth();

	/**
	 * Age the individual.
	 */
	void age(Level world, float ageModifier);

	/**
	 * @return true if the individual is among the living.
	 */
	boolean isAlive();

	default boolean hasEffect() {
		return getGenome().getPrimary(IAlleleForestrySpecies.class).hasEffect();
	}

	@Override
	default boolean isSecret() {
		return getGenome().getPrimary(IAlleleForestrySpecies.class).isSecret();
	}
}
