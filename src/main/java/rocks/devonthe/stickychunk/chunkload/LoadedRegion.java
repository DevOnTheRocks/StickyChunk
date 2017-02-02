package rocks.devonthe.stickychunk.chunkload;

import rocks.devonthe.stickychunk.StickyChunk;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.ChunkTicketManager.LoadingTicket;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.world.Region;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class LoadedRegion {
	private static final Server SERVER = StickyChunk.getInstance().getGame().getServer();
	private static final TicketManager ticketManager = StickyChunk.getInstance().getTicketManager();
	private static final Logger logger = StickyChunk.getInstance().getLogger();

	private LoadingTicket ticket;
	private boolean isValid;
	private Region region;
	private Date epoch;
	private UUID owner;
	private UUID id;

	public enum ChunkType {
		PERSONAL,
		WORLD;

		public static HashMap<String, Integer> asMap() {
			HashMap<String, Integer> map = new HashMap<>();

			for (ChunkType type : ChunkType.values())
				map.put(type.name(), type.ordinal());

			return map;
		}
	}

	public LoadedRegion(UUID owner, UUID id, Region region, Date epoch) {
		this.ticket = createTicket(region.getWorld());
		this.region = region;
		this.owner = owner;
		this.epoch = epoch;
		this.id = id;
	}

	public LoadedRegion(Region region, Player owner) {
		this.ticket = createTicket(region.getWorld());
		this.epoch = Date.from(Instant.now());
		this.owner = owner.getUniqueId();
		this.id = UUID.randomUUID();
		this.region = region;
	}

	public LoadedRegion(Location<World> from, Location<World> to, Player owner) {
		this.region = new Region(from, to);
		this.ticket = createTicket(region.getWorld());
		this.epoch = Date.from(Instant.now());
		this.owner = owner.getUniqueId();
		this.id = UUID.randomUUID();
	}

	public LoadedRegion(Location<World> location, Player owner) {
		this.region = new Region(location);
		this.ticket = createTicket(region.getWorld());
		this.epoch = Date.from(Instant.now());
		this.owner = owner.getUniqueId();
		this.id = UUID.randomUUID();
	}

	public static List<Chunk> getChunks(Region region) {
		return region.getChunks();
	}

	public UUID getId() {
		return id;
	}

	public UUID getOwner() {
		return owner;
	}

	public Date getEpoch() {
		return epoch;
	}

	public World getWorld() {
		return region.getWorld();
	}

	public Region getRegion() {
		return region;
	}

	public List<Chunk> getChunks() {
		return region.getChunks();
	}

	public int getChunkCount() {
		return region.getChunks().size();
	}

	public boolean isValid() {
		return isValid;
	}

	public void forceChunks() {
		region.getChunks().forEach(chunk -> ticket.forceChunk(chunk.getPosition()));
	}

	public LoadingTicket createTicket(World world) {
		Optional<LoadingTicket> opTicket = ticketManager.createTicket(world);
		LoadingTicket newTicket = null;

		if (!opTicket.isPresent()) {
			logger.error("Requested ticket was not provided; maximum tickets may have been reached.");
			this.isValid = false;
		} else {
			newTicket = opTicket.get();
			this.isValid = true;
		}

		return newTicket;
	}
}
