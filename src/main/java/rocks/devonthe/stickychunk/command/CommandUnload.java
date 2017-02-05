package rocks.devonthe.stickychunk.command;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.TicketManager;
import rocks.devonthe.stickychunk.data.DataStore;
import rocks.devonthe.stickychunk.permission.Permissions;

/**
 * Created by Cossacksman on 04/02/2017.
 */
public class CommandUnload implements CommandExecutor {
	private Logger logger = StickyChunk.getInstance().getLogger();
	private static Game game = StickyChunk.getInstance().getGame();
	private DataStore dataStore = StickyChunk.getInstance().getDataStore();
	private TicketManager ticketManager = StickyChunk.getInstance().getTicketManager();
	private static String helpText = "/sc unload - unload the region at your current position.";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_DELETE)
			.description(Text.of(helpText))
			.executor(new CommandUnload())
			.build();

	/***
	 * Register the command with the game's command manager
	 */
	public static void register() {
		game.getCommandManager().register(StickyChunk.getInstance(), commandSpec);
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

		Player player = (Player) src;

		if (player.hasPermission(Permissions.COMMAND_DELETE)) {
			logger.info("attempting to delete loadedregion");

			dataStore.getPlayerRegions(player).ifPresent(
				playerRegions -> playerRegions.forEach(
					region -> region.getChunks().forEach(
						chunk -> {
							logger.info("Checking players location");
							if (player.getLocation().getChunkPosition().equals(chunk.getPosition())) {
								logger.info("found chunk to delete");
								region.unForceChunks();
								region.invalidateTicket();
								dataStore.deletePlayerRegion(player, region.getUniqueIdentifier());
								player.sendMessage(Text.of(TextColors.GREEN, "Successfully removed loaded region"));
							}
						}
					)
				)
			);
		}

		return CommandResult.success();
	}

	/***
	 *
	 * @param src
	 * @param args
	 * @return
	 */
	private CommandResult execServer(CommandSource src, CommandContext args) {
		return CommandResult.success();
	}
}
