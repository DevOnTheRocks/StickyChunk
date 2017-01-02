package uk.codepen.stickysponge;

import com.flowpowered.math.vector.Vector2i;
import com.sun.webkit.network.URLs;
import jdk.nashorn.internal.ir.Block;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.sql.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class Chunk {
	private static final Server SERVER = StickySponge.getInstance().getGame().getServer();

	private Coordinate coordinate;
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

	public Chunk(UUID owner, UUID id, UUID world, Coordinate coordinate, Date epoch) {
		this.world = SERVER.getWorld(world).orElseGet(() -> StickySponge.getInstance().getDefaultWorld());
		this.coordinate = coordinate;
		this.owner = owner;
		this.epoch = epoch;
		this.id = id;
	}

	public Chunk(World world, Coordinate coordinate, Player owner) {
		this.epoch = (Date) Date.from(Instant.now());
		this.owner = owner.getUniqueId();
		this.id = UUID.randomUUID();
		this.world = world;
	}

	public Chunk(World world, Vector2i coordinate, Player owner) {
		this.coordinate = new Coordinate(coordinate);
		this.epoch = (Date) Date.from(Instant.now());
		this.owner = owner.getUniqueId();
		this.id = UUID.randomUUID();
		this.world = world;
	}

	public Chunk(Location<World> location, Player owner) {
		this.coordinate = new Coordinate(location.getBlockX(), location.getBlockZ());
		this.epoch = (Date) Date.from(Instant.now());
		this.world = location.getExtent();
		this.owner = owner.getUniqueId();
		this.id = UUID.randomUUID();

		// TODO:- Get the chunk from the block (bitshift)
	}

	public UUID getId() {
		return id;
	}

	public UUID getOwner() {
		return owner;
	}

	public World getWorld() {
		return world;
	}

	public Date getEpoch() {
		return epoch;
	}

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public int getX() {
		return coordinate.getX();
	}

	public int getZ() {
		return coordinate.getZ();
	}
}
