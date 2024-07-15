/*******************************************************************************
 * Copyright 2011-2014 SirSengir
 *
 * This work (the API) is licensed under the "MIT" License, see LICENSE.txt for details.
 ******************************************************************************/
package forestry.api.apiculture;

import net.minecraft.world.level.block.state.BlockState;

public interface IJubilanceFactory {
	/**
	 * The default Jubilance Provider is satisfied when the humidity and temperature are ideal for the bee.
	 */
	IBeeJubilance getDefault();

	/**
	 * The Requires Resource Jubilance Provider is satisfied when a specific block is under the hive.
	 */
	IBeeJubilance getRequiresResource(BlockState... acceptedBlockStates);
}
