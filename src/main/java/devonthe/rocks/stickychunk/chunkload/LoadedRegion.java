package devonthe.rocks.stickychunk.chunkload;

import devonthe.rocks.stickychunk.StickyChunk;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.ChunkTicketManager.LoadingTicket;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import devonthe.rocks.stickychunk.world.Region;

import java.time.Instant;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class LoadedRegion {
	private static final Server SERVER = StickyChunk.getInstance().getGame().getServer();

	private LoadingTicket ticket;
	private List<Chunk> chunks;
	private Region region;
	private World world;
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

	public LoadedRegion(UUID owner, UUID id, UUID world, Region region, Date epoch) {
		this.world = SERVER.getWorld(world).orElseGet(() -> StickyChunk.getInstance().getDefaultWorld());
		this.region = region;
		this.owner = owner;
		this.epoch = epoch;
		this.id = id;
	}

	public LoadedRegion(World world, Region region, Player owner) {
		this.epoch = (Date) Date.from(Instant.now());
		this.owner = owner.getUniqueId();
		this.id = UUID.randomUUID();
		this.world = world;
	}

	public LoadedRegion(Location<World> from, Location<World> to, Player owner) {
		this.region = new Region(from, to);
		this.epoch = (Date) Date.from(Instant.now());
		this.world = from.getExtent();
		this.owner = owner.getUniqueId();
		this.id = UUID.randomUUID();
	}

	public LoadedRegion(Region region, Player owner) {

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
		return world;
	}

	public Region getRegion() {
		return region;
	}

	public List<Chunk> getChunks() {
		return chunks;
	}

	public int getChunkCount() {
		return chunks.size();
	}



	public void load(ChunkType type) {
		for (Chunk chunk : chunks)
			ticket.forceChunk(chunk.getPosition());
	}
}
