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
package forestry.core.genetics.mutations;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import forestry.api.climate.IClimateProvider;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IMutation;
import forestry.api.genetics.IMutationCondition;

// todo separate classes for single biome and tag
public class MutationConditionBiome implements IMutationCondition {
	private final TagKey<Biome> validBiomes;

	public MutationConditionBiome(TagKey<Biome> validBiomes) {
		this.validBiomes = validBiomes;
	}

	@Override
	public float modifyChance(Level level, BlockPos pos, IMutation<?> mutation, IGenome genome0, IGenome genome1, IClimateProvider climate, float currentChance) {
		return level.getBiome(pos).is(this.validBiomes) ? currentChance : 0f;
	}

	@Override
	public Component getDescription() {
		String biomeType = this.validBiomes.location().toString();
		return Component.translatable("for.mutation.condition.biome.single", biomeType);
	}
}
