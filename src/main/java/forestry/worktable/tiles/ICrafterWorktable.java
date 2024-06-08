package forestry.worktable.tiles;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;

public interface ICrafterWorktable {
	ItemStack getResult(CraftingContainer inventory, Level world);

	boolean mayPickup(int slotIndex);

	boolean onCraftingStart(Player player);

	void onCraftingComplete(Player player);

	@Nullable
	CraftingRecipe getRecipeUsed();
}
