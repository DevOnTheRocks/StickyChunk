package rocks.devonthe.stickychunk.world;

import rocks.devonthe.stickychunk.StickyChunk;
import org.spongepowered.api.Server;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
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
		this.chunks = getChunkRange();
	}

	public Region(Location<World> from, Location<World> to) {
		fromChunkPosition = new Coordinate(from.getBlockPosition());
		toChunkPosition = new Coordinate(to.getBlockPosition());
		world = from.getExtent();
		chunks = getChunkRange();
	}

	public Region(Location<World> location) {
		location.getExtent().getChunk(location.getBlockX(), 0, location.getBlockZ()).ifPresent(chunks::add);
		fromChunkPosition = toChunkPosition = new Coordinate(location.getBlockPosition());
		world = location.getExtent();
	}

	public List<Chunk> getChunkRange() {
		return getChunkRange(getFrom(), getTo(), world);
	}

	public static List<Chunk> getChunkRange(Coordinate from, Coordinate to, World world) {
		List<Chunk> chunks = new ArrayList<Chunk>();
		int height, width, area, pointerX, pointerZ, lowestX, lowestZ, highestX, highestZ;

		lowestX = Math.min(from.getX(), to.getX());
		lowestZ = Math.min(from.getZ(), to.getZ());
		highestX = Math.max(from.getX(), to.getX());
		highestZ = Math.max(from.getZ(), to.getZ());

		width = Math.abs(lowestX - highestX);
		height = Math.abs(lowestZ - highestZ);

		area = Math.abs((width+1) * (height+1));

		pointerX = lowestX;
		pointerZ = lowestZ;

		for (int i = 0; i <= width; i++) {
			System.out.println(String.format("X: %s", pointerX));
			for (int l = 0; l <= height; l++) {
				System.out.println(String.format("Z: %s", pointerZ));

				world.getChunk(pointerX, 0, pointerZ).ifPresent(chunks::add);
				pointerZ++;
			}

			pointerZ = lowestZ;
			pointerX++;
		}

		return chunks;
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
