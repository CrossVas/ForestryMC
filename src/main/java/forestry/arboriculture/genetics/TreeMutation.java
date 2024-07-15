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
package forestry.arboriculture.genetics;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import forestry.api.IForestryApi;
import forestry.api.arboriculture.TreeManager;
import forestry.api.arboriculture.genetics.IAlleleTreeSpecies;
import forestry.api.arboriculture.genetics.ITreeMutation;
import forestry.api.arboriculture.genetics.ITreeMutationBuilder;
import forestry.api.arboriculture.genetics.ITreeSpeciesType;
import forestry.core.genetics.mutations.Mutation;

import forestry.api.genetics.alleles.IAllele;
import forestry.api.genetics.IGenome;

public class TreeMutation extends Mutation implements ITreeMutation, ITreeMutationBuilder {
	public TreeMutation(IAlleleTreeSpecies allele0, IAlleleTreeSpecies allele1, IAllele[] template, int chance) {
		super(allele0, allele1, template, chance);
	}

	@Override
	public ITreeMutation build() {
		return this;
	}

	@Override
	public ITreeSpeciesType getRoot() {
		return TreeManager.treeRoot;
	}

	@Override
	public float getChance(Level world, BlockPos pos, IAlleleTreeSpecies allele0, IAlleleTreeSpecies allele1, IGenome genome0, IGenome genome1) {
		float processedChance = super.getChance(world, pos, allele0, allele1, genome0, genome1, IForestryApi.INSTANCE.getClimateManager().getDefaultClimate(world, pos));
		if (processedChance <= 0) {
			return 0;
		}

		processedChance *= getRoot().getTreekeepingMode(world).getMutationModifier(genome0, genome1, 1f);

		return processedChance;
	}

}
