package rocks.devonthe.stickychunk.chunkload;

import org.spongepowered.api.world.World;
import org.spongepowered.mod.service.world.SpongeChunkTicketManager;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.data.DataStore;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Cossacksman on 28/01/2017.
 */
public class TicketManager extends SpongeChunkTicketManager {
	DataStore dataStore = StickyChunk.getInstance().getDataStore();

	public TicketManager() {

	}

	public Optional<LoadingTicket> createTicket(World world) {
		return super.createTicket(StickyChunk.getInstance(), world);
	}

	public void validateLoadedRegions() {
		List<LoadedRegion> invalidRegions = dataStore
				.getCollatedRegions()
				.stream()
				.filter(region -> !region.isValid())
				.collect(Collectors.toList());

		invalidRegions.forEach(LoadedRegion::assignTicket);
	}
}
