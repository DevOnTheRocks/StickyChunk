package rocks.devonthe.stickychunk.permission;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.PermissionService;

public class Options {
	private static final PermissionService PERMISSION_SERVICE = Sponge.getServiceManager().provide(PermissionService.class).get();

	private static final String MAX_PERSONAL_CHUNKS = "stickychunk.max-personal";
	private static final String MAX_WORLD_CHUNKS = "stickychunk.max-world";
	private static final String MAX_WORLD_DURATION = "stickychunk.world-duration";

	public static int getMaxPersonalChunks(User user) {
		try {
			return Integer.parseInt(PERMISSION_SERVICE.getUserSubjects().get(user.getIdentifier()).getOption(user.getActiveContexts(), MAX_PERSONAL_CHUNKS).orElse("-1"));
		} catch (NumberFormatException ignored) {
			return -1;
		}
	}

	public static int getMaxWorldChunks(User user) {
		try {
			return Integer.parseInt(PERMISSION_SERVICE.getUserSubjects().get(user.getIdentifier()).getOption(user.getActiveContexts(), MAX_WORLD_CHUNKS).orElse("-1"));
		} catch (NumberFormatException ignored) {
			return -1;
		}
	}

	public static int getWorldChunksDuration(User user) {
		try {
			return Integer.parseInt(PERMISSION_SERVICE.getUserSubjects().get(user.getIdentifier()).getOption(user.getActiveContexts(), MAX_WORLD_DURATION).orElse("-1"));
		} catch (NumberFormatException ignored) {
			return -1;
		}
	}
}
