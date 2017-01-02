package uk.codepen.stickysponge;

import com.flowpowered.math.vector.Vector2i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class Chunk {
	private Coordinate coordinate;

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

	public Chunk(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public Chunk(Vector2i coordinate) {
		this.coordinate = new Coordinate(coordinate);
	}

	public static Chunk fromBlock(Location<World> location) {
		return new Chunk(new Coordinate(location.getChunkPosition()));
	}

	public int getX() {
		return coordinate.getX();
	}

	public int getZ() {
		return coordinate.getZ();
	}
}
