package devonthe.rocks.stickychunk.world;

import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;

/**
 * Created by Cossacksman on 19/01/2017.
 */
public class Region {

	private List<Chunk> chunks;
	private Coordinate fromChunkPosition;
	private Coordinate toChunkPosition;

	public Region(Coordinate fromChunkPosition, Coordinate toChunkPosition) {
		this.fromChunkPosition = fromChunkPosition;
		this.toChunkPosition = toChunkPosition;
	}
	public Region(Location<World> from, Location<World> to) {
		from.getExtent().getChunk(from.getChunkPosition()).ifPresent(chunks::add);

		// Get blocks inbetween

		to.getExtent().getChunk(to.getChunkPosition()).ifPresent(chunks::add);
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
}
