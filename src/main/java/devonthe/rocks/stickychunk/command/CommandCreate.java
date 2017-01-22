package devonthe.rocks.stickychunk.command;

import devonthe.rocks.stickychunk.Permission.Permissions;
import devonthe.rocks.stickychunk.StickyChunk;
import devonthe.rocks.stickychunk.chunkload.LoadedRegion;
import devonthe.rocks.stickychunk.world.Region;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandPermissionException;
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
			.permission(Permissions.COMMAND_CREATE_PERSONAL)
			.permission(Permissions.COMMAND_CREATE_WORLD)
			.description(Text.of(helpText))
			.arguments(GenericArguments.optional(GenericArguments.choices(Text.of("type"), LoadedRegion.ChunkType.asMap())))
			.executor(new CommandCreate())
			.build();

	public static void register() {
		StickyChunk.getInstance().getGame().getCommandManager().register(StickyChunk.getInstance(), commandSpec);
	}

	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if (!(src instanceof Player))
			return execServer(src, args);
		else {
			Player player = (Player) src;

//			if (player.hasPermission(Permissions.COMMAND_CREATE_PERSONAL) || player.hasPermission(Permissions.COMMAND_CREATE_WORLD)) {
				return execPlayer(player, args);
//			} else {
//				throw new CommandPermissionException(Text.of("You do not have permission to create chunks"));
//			}
		}
	}

	private CommandResult execPlayer(Player player, CommandContext args) {
		Region region = null; // TODO:- Remove as this command will only be executed after the region is set
		StickyChunk.loadedRegions.add(new LoadedRegion(region, player));
		return CommandResult.success();
	}

	private CommandResult execServer(CommandSource src, CommandContext args) {
		return CommandResult.success();
	}
}
