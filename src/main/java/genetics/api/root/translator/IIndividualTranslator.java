package genetics.api.root.translator;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import genetics.api.individual.IIndividual;
import genetics.api.root.IIndividualRootBuilder;
import genetics.api.root.components.ComponentKey;
import genetics.api.root.components.IRootComponent;

/**
 * Translates a item or a block that does not contain any genetic information into a {@link ItemStack} or a
 * {@link IIndividual} if a {@link IItemTranslator} or {@link IBlockTranslator} was registered for it at the
 * {@link IIndividualRootBuilder}.
 */
public interface IIndividualTranslator<I extends IIndividual> extends IRootComponent<I> {
	/**
	 * Registers a translator that translates a {@link BlockState} into a  {@link IIndividual} or an {@link ItemStack}
	 * that contains an {@link IIndividualCapability}.
	 *
	 * @param translatorKeys The key of the translator the block of {@link BlockState} that you want to translate
	 *                       with the translator.
	 * @param translator     A translator that should be used to translate the data.
	 */
	IIndividualTranslator<I> registerTranslator(IBlockTranslator<I> translator, Block... translatorKeys);

	/**
	 * Registers a translator that translates an {@link ItemStack} that does not contain an {@link IIndividualCapability} into a
	 * {@link IIndividual} or another {@link ItemStack} that contains an {@link IIndividualCapability}.
	 *
	 * @param translatorKeys The key of the translator it is the item of the {@link ItemStack} that you want to translate
	 *                       with the translator.
	 * @param translator     A translator that should be used to translate the data.
	 */
	IIndividualTranslator<I> registerTranslator(IItemTranslator<I> translator, Item... translatorKeys);

	/**
	 * @param translatorKey The key of the translator, by default it is the item of the {@link ItemStack} that you want
	 *                      to translate with the translator.
	 */
	@Nullable
	IItemTranslator<I> getTranslator(Item translatorKey);

	/**
	 * @param translatorKey The key of the translator the block of the{@link BlockState} that you want to translate
	 *                      with the translator.
	 */
	@Nullable
	IBlockTranslator<I> getTranslator(Block translatorKey);

	/**
	 * Translates {@link BlockState}s into genetic data.
	 */
	@Nullable
	I translateMember(BlockState state);

	/**
	 * Translates {@link ItemStack}s into genetic data.
	 */
	@Nullable
	I translateMember(ItemStack stack);

	/**
	 * Translates a {@link BlockState}s into genetic data and returns a {@link ItemStack} that contains this data.
	 */
	ItemStack getGeneticEquivalent(BlockState state);

	/**
	 * Translates {@link ItemStack}s into genetic data and returns a other {@link ItemStack} that contains this data.
	 */
	ItemStack getGeneticEquivalent(ItemStack stack);

	@Override
	ComponentKey<IIndividualTranslator> getKey();
}
