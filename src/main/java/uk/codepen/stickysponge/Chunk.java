package uk.codepen.stickysponge;

import com.flowpowered.math.vector.Vector2i;
import jdk.nashorn.internal.ir.Block;
import org.spongepowered.api.Server;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class Chunk {
	private static final Server SERVER = StickySponge.getInstance().getGame().getServer();

	private Coordinate coordinate;
	private World world;
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

	public Chunk(UUID owner, UUID id, UUID world, Coordinate coordinate) {
		this.owner = owner;
		this.id = id;
		this.world = SERVER.getWorld(world).orElseGet(() -> StickySponge.getInstance().getDefaultWorld());
		this.coordinate = coordinate;
	}

	public Chunk(World world, Coordinate coordinate) {
		this.coordinate = coordinate;
		this.world = world;
	}

	public Chunk(World world, Vector2i coordinate) {
		this.coordinate = new Coordinate(coordinate);
		this.world = world;
	}

	public Chunk(Location<World> location) {
		this.world = location.getExtent();
		this.coordinate = new Coordinate(location.getBlockX(), location.getBlockZ());

		// TODO:- Get the chunk from the block (bitshift)
	}

	public int getX() {
		return coordinate.getX();
	}

	public int getZ() {
		return coordinate.getZ();
	}
}
