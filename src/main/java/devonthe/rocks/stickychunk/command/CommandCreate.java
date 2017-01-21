package devonthe.rocks.stickychunk.command;

import devonthe.rocks.stickychunk.Permission.Permissions;
import devonthe.rocks.stickychunk.StickyChunk;
import devonthe.rocks.stickychunk.chunkload.LoadedRegion;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class CommandCreate implements CommandExecutor {

	private static String helpText = "/ss create [type] - Created a chunk-load of the specified type.";

	private static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_CREATE)
			.description(Text.of(helpText))
			.arguments(GenericArguments.optional(GenericArguments.choices(Text.of("type"), LoadedRegion.ChunkType.asMap())))
			.executor(new CommandCreate())
			.build();

	public static void register() {
		StickyChunk.getInstance().getGame().getCommandManager().register(StickyChunk.getInstance(), commandSpec);
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			throw new CommandException(Text.of("You must be a player to execute this command."));

		Player player = (Player) src;

		// Do checks for the command

		StickyChunk.loadedRegions.add(new LoadedRegion(player.getLocation(), player));

		return CommandResult.success();
	}
}
