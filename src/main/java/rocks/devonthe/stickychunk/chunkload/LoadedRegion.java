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

	/***
	 * Get the chunks of any specified Region instance
	 * @param region a region to get the chunks from
	 * @return a list of chunks in the region
	 */
	public static List<Chunk> getChunks(Region region) {
		return region.getChunks();
	}

	/***
	 * Get the unique identifier of this LoadedRegion
	 * @return a UUID of this LoadedRegion
	 */
	public UUID getId() {
		return id;
	}

	/***
	 * Get the unique identifier of the player who owns this LoadedRegion
	 * @return a UUID of the player's account
	 */
	public UUID getOwner() {
		return owner;
	}

	/***
	 * Get the time this LoadedRegion was created
	 * @return a Util Date object of this regions epoch
	 */
	public Date getEpoch() {
		return epoch;
	}

	/***
	 * Get the world this LoadedRegion is associated with
	 * @return the world object this LoadedRegion uses
	 */
	public World getWorld() {
		return region.getWorld();
	}

	/***
	 * Get the region object of this LoadedRegion
	 * @return a Region object
	 */
	public Region getRegion() {
		return region;
	}

	/***
	 * Get the chunks this region covers
	 * @return a list of chunks in this region
	 */
	public List<Chunk> getChunks() {
		return region.getChunks();
	}

	/***
	 * Get the amount of chunks this region covers
	 * @return the chunks in this region
	 */
	public int getChunkCount() {
		return region.getChunks().size();
	}

	/***
	 * Check whether the ticket in this LoadedRegion is valid
	 * @return whether the LoadingTicket is valid or not
	 */
	public boolean isValid() {
		return isValid;
	}

	/***
	 * Use this instances ticket to force chunks to persist in the world
	 */
	public void forceChunks() {
		int i[] = new int[1];
		i[0] = 0;

		logger.info("BeforeSize: " + region.getChunks().size());
		region.getChunks().forEach(chunk -> {
			logger.info(""+i);
			ticket.forceChunk(chunk.getPosition());
			logger.info(String.format("Forcing chunks at %s,%s", chunk.getPosition().getX(), chunk.getPosition().getZ()));
			i[0]++;
		});
	}

	/***
	 * Create a ticket, requested from the Ticket Manager which returns an Optional
	 * @param world the world which the ticket should associate with
	 * @return A LoadingTicket for force loading chunks
	 */
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
