package forestry.core.items;

import java.util.function.Consumer;

import forestry.api.core.ItemGroups;
import forestry.core.blocks.IBlockTypeTesr;
import forestry.core.render.RenderForestryItemProperties;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

public class ItemBlockBase<B extends Block> extends ItemBlockForestry<B> {
	public final IBlockTypeTesr blockTypeTesr;
	
	public ItemBlockBase(B block, Properties builder, final IBlockTypeTesr blockTypeTesr) {
		super(block, builder);
		this.blockTypeTesr = blockTypeTesr;
	}

	public ItemBlockBase(B block, final IBlockTypeTesr blockTypeTesr) {
		super(block, new Properties().tab(ItemGroups.tabForestry));
		this.blockTypeTesr = blockTypeTesr;
	}

	// OnlyIn client?
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new RenderForestryItemProperties(this));
	}
}
