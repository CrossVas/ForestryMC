package genetics.commands;

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import genetics.api.GeneticsAPI;
import genetics.api.individual.IChromosome;
import genetics.api.individual.IChromosomeType;
import genetics.api.individual.IGenome;
import genetics.api.individual.IIndividual;

public class CommandListAlleles {

	public static ArgumentBuilder<CommandSourceStack, ?> register() {
		return Commands.literal("listAlleles").requires(PermLevel.ADMIN).executes(CommandListAlleles::execute);
	}

	public static int execute(CommandContext<CommandSourceStack> context) throws CommandRuntimeException, CommandSyntaxException {
		Player player = context.getSource().getPlayerOrException();

		ItemStack stack = player.getMainHandItem();

		IIndividual individual = GeneticsAPI.apiInstance.getRootHelper().getIndividual(stack);
		if (individual == null) {
			return 0;
		}

		IGenome genome = individual.getGenome();

		for (IChromosome chromosome : genome.getChromosomes()) {
			IChromosomeType type = chromosome.getType();

			CommandHelpers.sendChatMessage(context.getSource(), Component.literal(type.getName() + ": ").append(genome.getActiveAllele(type).getDisplayName()).append(Component.literal(" ")).append(genome.getInactiveAllele(type).getDisplayName()));
		}

		GeneticsAPI.apiInstance.getAlleleRegistry().getRegisteredAlleles().forEach(a -> System.out.println(a.getRegistryName() + ": " + a.getDisplayName().getString()));

		return 1;
	}
}
