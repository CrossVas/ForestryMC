package forestry.core.patchouli.processor;

import com.google.common.base.Preconditions;

import forestry.api.recipes.ICarpenterRecipe;
import forestry.core.utils.ModUtil;
import forestry.core.utils.RecipeUtils;
import forestry.factory.features.FactoryRecipeTypes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class CarpenterProcessor implements IComponentProcessor {
	@Nullable
	protected ICarpenterRecipe recipe;

	@Override
	public void setup(IVariableProvider variables) {
		ItemStack stack = variables.get("item").as(ItemStack.class, ItemStack.EMPTY);

		this.recipe = RecipeUtils.getRecipeByOutput(FactoryRecipeTypes.CARPENTER, stack);
	}

	@Override
	public IVariable process(String key) {
		Preconditions.checkNotNull(recipe);
		if (key.equals("output")) {
			return IVariable.from(this.recipe.getResultItem());
		} else if (key.equals("fluid")) {
			return IVariable.wrap(ModUtil.getRegistryName(this.recipe.getInputFluid().getFluid()).toString());
		} else if (key.equals("fluidAmount")) {
			return IVariable.wrap(this.recipe.getInputFluid().getAmount());
		} else if (key.startsWith("ingredient")) {
			int index = Integer.parseInt(key.substring("ingredient".length()));
            if (index < 1 || index > 9) {
                return IVariable.empty();
            }

			Ingredient ingredient;
			try {
				ingredient = this.recipe.getCraftingGridRecipe().getIngredients().get(index - 1);
			} catch (Exception e) {
				ingredient = Ingredient.EMPTY;
			}
			return IVariable.from(ingredient.getItems());
		} else {
			return IVariable.empty();
		}
	}
}
