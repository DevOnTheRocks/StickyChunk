package rocks.devonthe.stickychunk.chunkload;

import org.spongepowered.api.world.World;
import org.spongepowered.mod.service.world.SpongeChunkTicketManager;
import rocks.devonthe.stickychunk.StickyChunk;

import java.util.Optional;

/**
 * Created by Cossacksman on 28/01/2017.
 */
public class TicketManager extends SpongeChunkTicketManager {
	public TicketManager() {

	}

	public Optional<LoadingTicket> createTicket(World world) {
		return super.createTicket(StickyChunk.getInstance(), world);
	}
}
