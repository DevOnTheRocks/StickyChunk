package rocks.devonthe.stickychunk.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.chunkload.TicketManager;
import rocks.devonthe.stickychunk.command.Argument.ChunkTypeArgument;
import rocks.devonthe.stickychunk.data.DataStore;
import rocks.devonthe.stickychunk.listener.RegionAreaListener;
import rocks.devonthe.stickychunk.permission.Permissions;
import rocks.devonthe.stickychunk.world.Coordinate;
import rocks.devonthe.stickychunk.world.Region;

/**
 * Created by Cossacksman on 30/01/2017.
 */
public class CommandCreateRange implements CommandExecutor {
	private DataStore dataStore = StickyChunk.getInstance().getDataStore();
	private TicketManager ticketManager = StickyChunk.getInstance().getTicketManager();
	private static String helpText = "/sc loadall - Chunk-load the region currently selected.";

	public static CommandSpec commandSpec = CommandSpec.builder()
			.permission(Permissions.COMMAND_CREATE_PERSONAL)
			.permission(Permissions.COMMAND_CREATE_WORLD)
			.description(Text.of(helpText))
			.arguments(GenericArguments.optional(new ChunkTypeArgument(Text.of("type"))))
			.executor(new CommandCreateRange())
			.build();

	/***
	 * Register the command with the game's command manager
	 */
	public static void register() {
		StickyChunk.getInstance().getGame().getCommandManager().register(StickyChunk.getInstance(), commandSpec, "many");
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
		LoadedRegion.ChunkType type = (LoadedRegion.ChunkType) args.getOne("type").orElse(LoadedRegion.ChunkType.WORLD);

		if (RegionAreaListener.exists(player)) {
			RegionAreaListener.PlayerData playerData = RegionAreaListener.get(player);

			Coordinate from = new Coordinate(new Location<>(player.getWorld(), playerData.getPos1()).getChunkPosition());
			Coordinate to = new Coordinate(new Location<>(player.getWorld(), playerData.getPos2()).getChunkPosition());

			StickyChunk.getInstance().getLogger().info(String.format("FromX: %s, fromZ: %s", from.getX(), from.getZ()));
			StickyChunk.getInstance().getLogger().info(String.format("ToX: %s, toZ: %s", to.getX(), to.getZ()));

			Region region = new Region(from, to, player.getWorld());
			LoadedRegion loadedRegion = new LoadedRegion(region, player, type);
			loadedRegion.assignTicket();

			if (loadedRegion.isValid()) {
				dataStore.addPlayerRegion(player, loadedRegion);
				loadedRegion.forceChunks();
				player.sendMessage(Text.of(TextColors.GREEN, "Successfully loaded chunk range."));
			} else {
				player.sendMessage(Text.of(TextColors.RED, "Failed to allocate a loading ticket or force chunks."));
			}
		} else {
			player.sendMessage(Text.of(TextColors.RED, "You must have a selected region before loading multiple chunks."));
		}

		return CommandResult.success();
	}

	/**
	 * Callback for the execution of the command on a server.
	 *
	 * @param src  The commander who is executing this command
	 * @param args The parsed command arguments for this command
	 * @return the result of executing this command
	 */
	private CommandResult execServer(CommandSource src, CommandContext args) {
		return CommandResult.success();
	}
}
