package rocks.devonthe.stickychunk.command;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import rocks.devonthe.stickychunk.StickyChunk;

/**
 * Created by Cossacksman on 24/01/2017.
 */
public class CommandPersist implements CommandExecutor {
	private static String helpText = "";

	private static CommandSpec commandSpec = CommandSpec.builder()
			.description(Text.of(helpText))
			.child(CommandLoad.commandSpec, "load")
			.child(CommandLoadRange.commandSpec, "loadarea")
			.child(CommandUnload.commandSpec, "unload")
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
		return null;
	}
}
