package devonthe.rocks.stickychunk.world;

import devonthe.rocks.stickychunk.StickyChunk;
import org.spongepowered.api.Server;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;

/**
 * Created by Cossacksman on 19/01/2017.
 */
public class Region {

	private World world;
	private List<Chunk> chunks;
	private Coordinate fromChunkPosition;
	private Coordinate toChunkPosition;

	private Server server = StickyChunk.getInstance().getGame().getServer();

	public Region(Coordinate from, Coordinate to, World world) {
		this.fromChunkPosition = from;
		this.toChunkPosition = to;
		this.world = world;
	}

	public Region(Location<World> from, Location<World> to) {
		fromChunkPosition = new Coordinate(from.getBlockPosition());
		toChunkPosition = new Coordinate(to.getBlockPosition());
		world = from.getExtent();
	}

	public Region(Location<World> location) {
		chunks.add(server.getWorld(location.getExtent().getUniqueId()).get().getChunk(location.getChunkPosition()).get());
		fromChunkPosition = toChunkPosition = new Coordinate(location.getBlockPosition());
		world = location.getExtent();
	}

	public List<Chunk> getChunks() {
		return chunks;
	}

	public Coordinate getFrom() {
		return fromChunkPosition;
	}

	public Coordinate getTo() {
		return toChunkPosition;
	}

	public World getWorld() {
		return world;
	}
}
