package devonthe.rocks.stickychunk.command;

import devonthe.rocks.stickychunk.Permission.Permissions;
import devonthe.rocks.stickychunk.StickyChunk;
import devonthe.rocks.stickychunk.chunkload.LoadedRegion;
import devonthe.rocks.stickychunk.world.Region;
import devonthe.rocks.stickychunk.world.RegionAreaListener;
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
public class CommandCreateOne implements CommandExecutor {

	private static String helpText = "/sc create [type] - Created a chunk-load of the specified type.";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_CREATE_PERSONAL)
			.permission(Permissions.COMMAND_CREATE_WORLD)
			.description(Text.of(helpText))
			.arguments(GenericArguments.optional(GenericArguments.choices(Text.of("type"), LoadedRegion.ChunkType.asMap())))
			.executor(new CommandCreateOne())
			.build();

	public static void register() {
		StickyChunk.getInstance().getGame().getCommandManager().register(StickyChunk.getInstance(), commandSpec, "testicles", "testes");
	}

	/**
	 * Callback for the execution of a command.
	 *
	 * @param src  The commander who is executing this command
	 * @param args The parsed command arguments for this command
	 * @return the result of executing this command
	 * @throws CommandException If a user-facing error occurs while executing this command
	 */
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			return execServer(src, args);
		else {
				return execPlayer((Player) src, args);
		}
	}

	private CommandResult execPlayer(Player player, CommandContext args) {


//		StickyChunk.loadedRegions.add(new LoadedRegion(region, player));
		return CommandResult.success();
	}

	private CommandResult execServer(CommandSource src, CommandContext args) {
		return CommandResult.success();
	}
}
