package genetics.api.root.translator;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;

import genetics.api.individual.IIndividual;

/**
 * Translates blockStates into genetic data.
 * Used by bees and butterflies to convert and pollinate foreign leaf blocks.
 */
public interface IBlockTranslator<I extends IIndividual> {
	@Nullable
	I getIndividualFromObject(BlockState blockState);

	default ItemStack getGeneticEquivalent(BlockState state) {
		return ItemStack.EMPTY;
	}
}
