package rocks.devonthe.stickychunk.profile;

import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Scheduler;
import org.spongepowered.api.scheduler.Task;
import rocks.devonthe.stickychunk.StickyChunk;

/**
 * Created by Cossacksman on 05/01/2017.
 */
public class TickCompensator {
	private static Scheduler scheduler = Sponge.getScheduler();
	private static Task.Builder tickObserver = scheduler.createTaskBuilder();

	private static final Server SERVER = StickyChunk.getInstance().getGame().getServer();
	private static final Game GAME = StickyChunk.getInstance().getGame();

	public static void observeTicks() {
		tickObserver.async().execute(task -> {
			double ticks = SERVER.getTicksPerSecond();
		});
	}
}
