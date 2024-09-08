package forestry.apiculture.items;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import forestry.core.items.ItemForestry;

public class ItemBeesWax extends ItemForestry {
	// copied from HoneycombItem
	public InteractionResult useOn(UseOnContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockState state = level.getBlockState(pos);
		return HoneycombItem.getWaxed(state).map(waxedState -> {
			Player player = ctx.getPlayer();
			ItemStack stack = ctx.getItemInHand();
			if (player instanceof ServerPlayer serverPlayer) {
				CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, stack);
			}

			stack.shrink(1);
			level.setBlock(pos, waxedState, 11);
			level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, waxedState));
			level.levelEvent(player, LevelEvent.PARTICLES_AND_SOUND_WAX_ON, pos, 0);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}).orElse(InteractionResult.PASS);
	}
}
