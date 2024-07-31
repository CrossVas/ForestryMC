/*******************************************************************************
 * Copyright (c) 2011-2014 SirSengir.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Various Contributors including, but not limited to:
 * SirSengir (original work), CovertJaguar, Player, Binnie, MysteriousAges
 ******************************************************************************/
package forestry.apiculture.multiblock;

import javax.annotation.Nullable;

import forestry.api.climate.IClimateListener;
import forestry.api.climate.IClimatised;
import forestry.api.multiblock.IAlvearyController;
import forestry.core.inventory.IInventoryAdapter;
import forestry.core.multiblock.IMultiblockControllerInternal;
import forestry.core.network.IStreamableGui;
import forestry.core.owner.IOwnedTile;

public interface IAlvearyControllerInternal extends IAlvearyController, IMultiblockControllerInternal, IClimatised, IOwnedTile, IStreamableGui {
	IInventoryAdapter getInternalInventory();

	/**
	 * @return {@code null} if this multiblock is not assembled.
	 */
	@Nullable
	IClimateListener getClimateListener();

	int getHealthScaled(int i);
}
