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

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import com.mojang.brigadier.builder.ArgumentBuilder;

import forestry.api.arboriculture.genetics.ITreeSpeciesType;
import forestry.core.commands.CommandSaveStats;
import forestry.core.commands.GiveSpeciesCommand;
import forestry.core.commands.IStatsSaveHelper;
import forestry.core.commands.ModifyGenomeCommand;
import forestry.core.utils.SpeciesUtil;

public class CommandTree {
	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		IStatsSaveHelper saveHelper = new TreeStatsSaveHelper();
		ITreeSpeciesType type = SpeciesUtil.TREE_TYPE.get();

		return Commands.literal("tree")
				.then(CommandTreeSpawn.register("spawnTree", new TreeSpawner()))
				.then(CommandTreeSpawn.register("spawnForest", new ForestSpawner()))
				.then(CommandSaveStats.register(saveHelper))
				.then(GiveSpeciesCommand.register(type))
				.then(ModifyGenomeCommand.register(type));
	}
}