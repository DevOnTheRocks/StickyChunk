package rocks.devonthe.stickychunk.permission;

import org.spongepowered.api.command.CommandSource;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class Permissions {
	public static final String COMMAND_CREATE = "stickychunk.command.create";
	public static final String COMMAND_CREATE_PERSONAL = "stickychunk.command.create.personal";
	public static final String COMMAND_CREATE_WORLD = "stickychunk.command.create.world";

	public static final String PLAYER_SELECT_REGION = "stickychunk.command.select";

	public static final String COMMAND_DELETE = "stickychunk.command.delete";
	public static final String COMMAND_DELETE_PERSONAL = "stickychunk.command.delete.personal";
	public static final String COMMAND_DELETE_WORLD = "stickychunk.command.delete.world";

	public static final String COMMAND_RELEASE = "stickychunk.command.release";

	public static final String COMMAND_TELEPORT_TO = "stickychunk.command.teleport";

	public static boolean hasPermission(CommandSource src, String permission) {
		return src.hasPermission(permission);
	}
}
