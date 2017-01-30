package rocks.devonthe.stickychunk.listener;

import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.data.User;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;

import java.time.Instant;
import java.sql.Date;
import java.util.Optional;

/**
 * Created by Cossacksman on 25/01/2017.
 */
public class PlayerConnectionListener {
	Logger logger = StickyChunk.getInstance().getLogger();

	@Listener
	public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = (Player) event.player;
		Date now = (Date) java.util.Date.from(Instant.now());

		Optional<User> user = StickyChunk.getInstance().getDataStore().getUser(player);

		// Create a new user record if it doesn't already exist
		if (!user.isPresent())
			new User(player.getUniqueId(), 0, now, now).update();

		// Update the user if it does already exist (ensured by the previous check)
		StickyChunk.getInstance().getDataStore().getUser(player).ifPresent(usr -> usr.setLastSeen(now).update());
	}

	@Listener
	public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		Player player = (Player) event.player;
		Date now = (Date) java.util.Date.from(Instant.now());

		Optional<User> user = StickyChunk.getInstance().getDataStore().getUser(player);

		// Create a new user record if it doesn't already exist
		if (!user.isPresent()) {
			logger.error(String.format("Player %s was able to join the server without User record, creating one now...", player.getName()));
			new User(player.getUniqueId(), 0, now, now).update();
			logger.info(String.format("User record created for %s", player.getName()));
		}

		// Update the user if it does already exist (ensured by the previous check)
		StickyChunk.getInstance().getDataStore().getUser(player).ifPresent(usr -> usr.setLastSeen(now).update());
	}
}
