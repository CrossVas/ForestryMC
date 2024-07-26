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
package forestry.arboriculture.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.world.level.Level;

import forestry.api.arboriculture.ITreekeepingMode;
import forestry.core.commands.ICommandModeHelper;
import forestry.core.utils.SpeciesUtil;

public class TreeModeHelper implements ICommandModeHelper {

	@Override
	public String[] getModeNames() {
		List<ITreekeepingMode> treekeepingModes = SpeciesUtil.TREE_TYPE.get().getTreekeepingModes();
		int modeStringCount = treekeepingModes.size();
		List<String> modeStrings = new ArrayList<>(modeStringCount);
		for (ITreekeepingMode mode : treekeepingModes) {
			modeStrings.add(mode.getName());
		}

		return modeStrings.toArray(new String[modeStringCount]);
	}

	@Override
	public String getModeName(Level world) {
		return SpeciesUtil.TREE_TYPE.get().getTreekeepingMode(world).getName();
	}

	@Override
	public boolean setMode(Level world, String modeName) {
		ITreekeepingMode mode = SpeciesUtil.TREE_TYPE.get().getTreekeepingMode(modeName);
		if (mode != null) {
			SpeciesUtil.TREE_TYPE.get().setTreekeepingMode(world, mode);
			return true;
		}
		return false;
	}

	@Override
	public Iterable<String> getDescription(String modeName) {
		ITreekeepingMode mode = SpeciesUtil.TREE_TYPE.get().getTreekeepingMode(modeName);
		if (mode == null) {
			return Collections.emptyList();
		}
		return mode.getDescription();
	}

}
