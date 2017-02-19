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
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.permission.Permissions;

import java.util.List;

import static rocks.devonthe.stickychunk.StickyChunk.NAME;
import static rocks.devonthe.stickychunk.StickyChunk.VERSION;

public class CommandPersist implements CommandExecutor {
	private static String helpText = "";
	private static final Text HELP = Text.of("help");

	private static CommandSpec commandSpec = CommandSpec.builder()
		.description(Text.of(helpText))
		.child(CommandList.commandSpec, "list")
		.child(CommandLoad.commandSpec, "load")
		.child(CommandLoadRange.commandSpec, "loadarea")
		.child(CommandUnload.commandSpec, "unload")
		.arguments(GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.literal(HELP, "help"))))
		.executor(new CommandPersist())
		.build();

	/***
	 * Register the command with the game's command manager
	 */
	public static void register() {
		StickyChunk.getInstance().getGame().getCommandManager().register(StickyChunk.getInstance(), commandSpec, "sc");
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
		boolean hasPerms = false;
		List<Text> helpContents = Lists.newArrayList();

		helpContents.add(Text.of(
			TextColors.WHITE, "StickyChunk's region selection tool is set to: ",
			Text.builder("Blaze Rod").color(TextColors.GOLD)
				.onHover(
					TextActions.showItem(ItemStack.builder().itemType(ItemTypes.BLAZE_ROD).build().createSnapshot()))
			)
		);

		if (src.hasPermission(Permissions.COMMAND_LIST)) {
			helpContents.add(Text.of(
				Text.NEW_LINE,
				TextColors.GOLD, Text.builder("/sc list").onClick(TextActions.runCommand("/sc list")),
				TextColors.GRAY, (src.hasPermission(Permissions.COMMAND_LIST_OTHERS)) ? " [user]" : Text.EMPTY,
				TextColors.DARK_GRAY, " - ",
				TextColors.WHITE, CommandList.helpText
			));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_CREATE)) {
			helpContents.add(Text.of(
				Text.NEW_LINE,
				TextColors.GOLD, Text.builder("/sc load").onClick(TextActions.suggestCommand("/sc load")),
				TextColors.GRAY, " [world|personal]",
				TextColors.DARK_GRAY, " - ",
				TextColors.WHITE, CommandLoad.helpText
			));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_CREATE)) {
			helpContents.add(Text.of(
				Text.NEW_LINE,
				TextColors.GOLD, Text.builder("/sc loadarea").onClick(TextActions.suggestCommand("/sc loadarea")),
				TextColors.GRAY, " [world|personal]",
				TextColors.DARK_GRAY, " - ",
				TextColors.WHITE, CommandLoadRange.helpText
			));
			hasPerms = true;
		}

		if (src.hasPermission(Permissions.COMMAND_DELETE)) {
			helpContents.add(Text.of(
				Text.NEW_LINE,
				TextColors.GOLD, Text.builder("/sc unload").onClick(TextActions.suggestCommand("/sc unload")),
				TextColors.GRAY, " [all]",
				TextColors.DARK_GRAY, " - ",
				TextColors.WHITE, CommandUnload.helpText
			));
			hasPerms = true;
		}

		if (hasPerms) {
			PaginationList.builder()
				.title(Text.of(TextColors.GOLD, NAME, " Help"))
				.padding(Text.of(TextColors.GOLD, TextStyles.STRIKETHROUGH, "-"))
				.contents(helpContents)
				.sendTo(src);
		} else {
			src.sendMessage(Text.of(NAME + " " + VERSION));
		}

		return CommandResult.success();
	}
}
