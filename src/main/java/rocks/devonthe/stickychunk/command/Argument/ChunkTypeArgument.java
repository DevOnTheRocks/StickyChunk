package rocks.devonthe.stickychunk.command.Argument;

import com.google.common.collect.Lists;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.permission.Permissions;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Cossacksman on 04/02/2017.
 */
public class ChunkTypeArgument extends CommandElement {
	public ChunkTypeArgument(@Nullable Text key) {
		super(key);
	}

	/**
	 * Attempt to extract a value for this element from the given arguments.
	 * This method is expected to have no side-effects for the source, meaning
	 * that executing it will not change the state of the {@link CommandSource}
	 * in any way.
	 *
	 * @param source The source to parse for
	 * @param args   the arguments
	 * @return The extracted value
	 * @throws ArgumentParseException if unable to extract a value
	 */
	@Override
	@Nullable
	@NonnullByDefault
	protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {
		String type = args.next().toLowerCase();

		if (LoadedRegion.ChunkType.asMap().containsKey(type)) {
			if (source.hasPermission(String.format("%s.%s", Permissions.COMMAND_CREATE, type)))
				return LoadedRegion.ChunkType.asMap().get(type);
			else
				throw new ArgumentParseException(Text.of(TextColors.RED, String.format("You do not have permission to create %s chunks", type)), type, 0);
		} else {
			throw new ArgumentParseException(Text.of(TextColors.RED, "Chunk type does not exist."), type, 0);
		}
	}

	/**
	 * Fetch completions for command arguments.
	 *
	 * @param src     The source requesting tab completions
	 * @param args    The arguments currently provided
	 * @param context The context to store state in
	 * @return Any relevant completions
	 */
	@Override
	@NonnullByDefault
	public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {
		try {
			String type = args.peek().toLowerCase();
			return LoadedRegion.ChunkType.asMap().entrySet().stream()
					.filter(s -> s.getKey().startsWith(type))
					.filter(s -> Permissions.hasPermission(src, s.getKey()))
					.map(Map.Entry::getKey)
					.collect(Collectors.toList());
		} catch (ArgumentParseException e) {
			e.printStackTrace();
		}

		return Lists.newArrayList();
	}
}
