package rocks.devonthe.stickychunk.listener;

import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.data.DataStore;
import rocks.devonthe.stickychunk.data.User;

import java.sql.Date;
import java.time.Instant;

/**
 * Created by Cossacksman on 25/01/2017.
 */
public class PlayerConnectionListener {
	DataStore dataStore = StickyChunk.getInstance().getDataStore();
	Logger logger = StickyChunk.getInstance().getLogger();

	@Listener
	public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
		Player player = (Player) event.player;
		Date now = (Date) java.util.Date.from(Instant.now());
		User user = dataStore.getUser(player).orElse(new User(player.getUniqueId(), 0, now, now));

		dataStore.getPlayerRegions(user.getUniqueId()).ifPresent(loadedRegions -> loadedRegions.forEach(region -> {
			if (region.getType() == LoadedRegion.ChunkType.PERSONAL)
				region.unForceChunks();
		}));

		// Update the user in case it's an existing user
		dataStore.getUser(player).ifPresent(usr -> usr.setLastSeen(now).update());
	}

	@Listener
	public void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
		Player player = (Player) event.player;
		Date now = (Date) java.util.Date.from(Instant.now());
		User user = dataStore.getUser(player).orElse(new User(player.getUniqueId(), 0, now, now));

		dataStore.getPlayerRegions(user.getUniqueId()).ifPresent(loadedRegions -> loadedRegions.forEach(region -> {
			if (region.getType() == LoadedRegion.ChunkType.PERSONAL)
				region.forceChunks();
		}));

		// Update the user in case it's an existing user
		dataStore.getUser(player).ifPresent(usr -> usr.setLastSeen(now).update());
	}
}
