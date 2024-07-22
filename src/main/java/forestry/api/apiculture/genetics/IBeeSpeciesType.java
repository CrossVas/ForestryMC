/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture.genetics;

import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;

import com.mojang.authlib.GameProfile;

import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IGenome;

import forestry.api.apiculture.IApiaristTracker;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.genetics.ISpeciesType;

// todo reimplement beekeeping mode
public interface IBeeSpeciesType extends ISpeciesType<IBeeSpecies> {

	/* BREEDING TRACKER */

	/**
	 * @return {@link IApiaristTracker} associated with the passed world.
	 */
	@Override
	IApiaristTracker getBreedingTracker(LevelAccessor world, @Nullable GameProfile player);

	/* BEE SPECIFIC */

	/**
	 * @return true if passed item is a drone. Equal to getLifeStage(ItemStack stack) == EnumBeeType.DRONE
	 */
	boolean isDrone(ItemStack stack);

	/**
	 * @return true if passed item is mated (i.e. a queen)
	 */
	boolean isMated(ItemStack stack);

	/**
	 * Creates an IBee suitable for a queen containing the necessary second genome for the mate.
	 *
	 * @param genome Valid {@link IGenome}
	 * @param mate   Valid {@link IBee} representing the mate.
	 * @return Mated {@link IBee} from the passed genomes.
	 */
	IBee getBee(Level world, IGenome genome, IBee mate);

	/* MISC */

	/**
	 * Creates beekeepingLogic for a housing.
	 * Should be used when the housing is created, see IBeekeepingLogic
	 */
	IBeekeepingLogic createBeekeepingLogic(IBeeHousing housing);

	/**
	 * Combines multiple modifiers from an IBeeHousing into one.
	 * Stays up to date with changes to the housing's modifiers.
	 */
	IBeeModifier createBeeHousingModifier(IBeeHousing housing);

	/**
	 * Combines multiple listeners from an IBeeHousing into one.
	 * Stays up to date with changes to the housing's listeners.
	 */
	IBeeListener createBeeHousingListener(IBeeHousing housing);

}
