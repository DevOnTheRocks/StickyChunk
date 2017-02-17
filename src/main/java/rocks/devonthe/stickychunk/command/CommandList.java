/*
 * This file is part of StickyChunk by DevOnTheRocks, licensed under GPL-3.0
 *
 * Copyright (C) 2017 DevOnTheRocks
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * The above notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package rocks.devonthe.stickychunk.command;

import com.google.common.collect.Lists;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.data.DataStore;
import rocks.devonthe.stickychunk.permission.Permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandList implements CommandExecutor {
	private static Game game = StickyChunk.getInstance().getGame();
	private static Server server = game.getServer();
	private DataStore dataStore = StickyChunk.getInstance().getDataStore();
	public static String helpText = "/sc list - Lists all of your chunks across worlds.";
	private static final Text USER = Text.of("user");

	public static CommandSpec commandSpec = CommandSpec.builder()
		.permission(Permissions.COMMAND_LIST)
		.description(Text.of(helpText))
		.arguments(GenericArguments.optional(
			GenericArguments.requiringPermission(GenericArguments.user(USER), Permissions.COMMAND_LIST_OTHERS)))
		.executor(new CommandList())
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

		User user = args.<User>getOne(USER).orElse((Player) src);
		List<Text> listText = Lists.newArrayList();
		ArrayList<LoadedRegion> loadedRegions = dataStore.getPlayerRegions(user);

		loadedRegions.forEach(region -> StickyChunk.getInstance().getLogger().info(region.getOwner().toString()));

		Text header = Text.of(
			"Listing ",
			TextColors.GOLD, loadedRegions.size(),
			TextColors.RESET, " region(s) across ",
			TextColors.GOLD, dataStore.getPlayerRegionWorlds(user).size(),
			TextColors.RESET, " worlds"
		);

		if (loadedRegions.isEmpty())
			header = Text.of(TextColors.RED, "There are no loaded regions to display");

		loadedRegions.forEach(region -> listText.add(Text.of(
			TextColors.GOLD, region.getChunks().size(),
			TextColors.GREEN, Text.of(" [", (region.getType() == LoadedRegion.ChunkType.WORLD) ? 'W' : 'P', "]")
				.toBuilder().onHover(TextActions.showText(Text.of(TextColors.GREEN,
					(region.getType() == LoadedRegion.ChunkType.WORLD) ? "World" : "Personal"
				))),
			TextColors.WHITE, (region.getChunks().size() > 1) ? " chunks in world " : " chunk in world ",
			TextColors.GOLD, region.getWorld().getName(),
			TextColors.WHITE, " from (", TextColors.LIGHT_PURPLE, region.getRegion().getFrom().getX(), TextColors.WHITE,
			", ", TextColors.LIGHT_PURPLE, region.getRegion().getFrom().getZ(), TextColors.WHITE, ")",
			TextColors.WHITE, " to (", TextColors.LIGHT_PURPLE, region.getRegion().getTo().getX(), TextColors.WHITE,
			", ", TextColors.LIGHT_PURPLE, region.getRegion().getTo().getZ(), TextColors.WHITE, ")"
		)));

		PaginationList.builder()
			.title(Text.of(TextColors.GOLD, "Loaded Regions"))
			.padding(Text.of(TextColors.GOLD, TextStyles.STRIKETHROUGH, "-"))
			.header(header)
			.contents(listText)
			.sendTo(src);

		return CommandResult.success();
	}

	private CommandResult execServer(CommandSource src, CommandContext args) {
		if (dataStore.getCollatedRegions().isEmpty())
			src.sendMessage(Text.of(TextColors.RED, "There are no loaded regions to display"));

		dataStore.getCollatedRegions().forEach(region -> {
			Optional<Player> oPlayer = server.getPlayer(region.getOwner());
			String owner = (oPlayer.isPresent()) ? oPlayer.get().getName() : region.getOwner().toString();

			src.sendMessage(
				Text.of(
					TextColors.GREEN, owner, " ",
					TextColors.GOLD, region.getChunks().size(),
					TextColors.GREEN, " [", (region.getType() == LoadedRegion.ChunkType.WORLD) ? 'W' : 'P', "]",
					TextColors.WHITE, " chunks in world ",
					TextColors.GOLD, region.getWorld().getName(),
					TextColors.WHITE, " from (", TextColors.LIGHT_PURPLE, region.getRegion().getFrom().getX(), TextColors.WHITE,
					", ", TextColors.LIGHT_PURPLE, region.getRegion().getFrom().getZ(), TextColors.WHITE, ")",
					TextColors.WHITE, " to (", TextColors.LIGHT_PURPLE, region.getRegion().getTo().getX(), TextColors.WHITE,
					", ", TextColors.LIGHT_PURPLE, region.getRegion().getTo().getZ(), TextColors.WHITE, ")"
				)
			);
		});

		return CommandResult.success();
	}
}
