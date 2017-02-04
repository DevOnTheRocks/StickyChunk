package rocks.devonthe.stickychunk.world;

import org.spongepowered.api.Server;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.StickyChunk;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cossacksman on 19/01/2017.
 */
public class Region {
	private List<Chunk> chunks = new ArrayList<>();
	private Coordinate fromChunkPosition;
	private Coordinate toChunkPosition;
	private World world;
	private int area;

	private Server server = StickyChunk.getInstance().getGame().getServer();

	public Region(Coordinate from, Coordinate to, World world) {
		this.fromChunkPosition = from;
		chunks = new ArrayList<>();
		this.toChunkPosition = to;
		this.world = world;
	}

	public Region(Location<World> from, Location<World> to) {
		fromChunkPosition = new Coordinate(from.getBlockPosition());
		toChunkPosition = new Coordinate(to.getBlockPosition());
		chunks = new ArrayList<>();
		world = from.getExtent();
	}

	public Region(Location<World> location) {
		chunks = new ArrayList<>();
		location.getExtent().getChunk(location.getChunkPosition().getX(), 0, location.getChunkPosition().getZ()).ifPresent(chunks::add);
		fromChunkPosition = toChunkPosition = new Coordinate(location.getBlockPosition());
		world = location.getExtent();
	}

	/***
	 * Get the chunks in-between the two positions used to create the region
	 * @return a list of chunks in the region
	 */
	public List<Chunk> getChunkRange() {
		List<Chunk> chunks = new ArrayList<Chunk>();
		int height, width, pointerX, pointerZ, lowestX, lowestZ, highestX, highestZ;

		lowestX = Math.min(fromChunkPosition.getX(), toChunkPosition.getX());
		lowestZ = Math.min(fromChunkPosition.getZ(), toChunkPosition.getZ());
		highestX = Math.max(fromChunkPosition.getX(), toChunkPosition.getX());
		highestZ = Math.max(fromChunkPosition.getZ(), toChunkPosition.getZ());

		width = Math.abs(lowestX - highestX);
		height = Math.abs(lowestZ - highestZ);

		area = Math.abs((width + 1) * (height + 1));

		pointerX = lowestX;
		pointerZ = lowestZ;

		for (int i = 0; i <= width; i++) {
			for (int l = 0; l <= height; l++) {
				world.getChunk(pointerX, 0, pointerZ).ifPresent(chunks::add);
				pointerZ++;
			}

			pointerZ = lowestZ;
			pointerX++;
		}

		return chunks;
	}

	/***
	 * Get the chunks in this region
	 * @return a list of chunks from within this regions area
	 */
	public List<Chunk> getChunks() {
		if (chunks.isEmpty())
			chunks = getChunkRange();

		StickyChunk.getInstance().getLogger().info(String.format("AfterSize: %s", chunks.size()));

		return chunks;
	}

	/***
	 * Get the position of the chunk this region starts at
	 * @return a Coordinate of the region start point
	 */
	public Coordinate getFrom() {
		return fromChunkPosition;
	}

	/***
	 * Get the position of the chunk this regions ends at
	 * @return a Coordinate of the region end point
	 */
	public Coordinate getTo() {
		return toChunkPosition;
	}

	/***
	 * Get the world this region is associated with
	 * @return the world object this region uses
	 */
	public World getWorld() {
		return world;
	}
}
