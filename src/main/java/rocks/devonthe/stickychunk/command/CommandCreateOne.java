package rocks.devonthe.stickychunk.command;

import rocks.devonthe.stickychunk.chunkload.TicketManager;
import rocks.devonthe.stickychunk.data.DataStore;
import rocks.devonthe.stickychunk.permission.Permissions;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import rocks.devonthe.stickychunk.world.Region;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class CommandCreateOne implements CommandExecutor {
	private DataStore dataStore = StickyChunk.getInstance().getDataStore();
	private TicketManager ticketManager = StickyChunk.getInstance().getTicketManager();
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
		Region region = new Region(player.getLocation());

		ticketManager.createTicket(player.getWorld()).ifPresent(ticket -> {
			ticket.forceChunk(player.getLocation().getChunkPosition());
			LoadedRegion loadedRegion = new LoadedRegion(region, player);
			dataStore.addPlayerChunk(player, loadedRegion);
		});

		return CommandResult.success();
	}

	private CommandResult execServer(CommandSource src, CommandContext args) {
		return CommandResult.success();
	}
}
