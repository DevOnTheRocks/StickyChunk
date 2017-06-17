package rocks.devonthe.stickychunk.chunkload;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.spongepowered.api.world.Chunk;
import rocks.devonthe.stickychunk.world.Coordinate;

import java.util.UUID;

@DatabaseTable(tableName = "chunk")
public class LoadedChunk {
	@DatabaseField
	private UUID id;
	@DatabaseField
	private UUID world;
	private Chunk chunk;
	@DatabaseField(useGetSet = true)
	private int chunkPosX;
	@DatabaseField(useGetSet = true)
	private int chunkPosZ;

	protected LoadedChunk() {}

	public LoadedChunk(UUID id, UUID world, Chunk chunk) {
		this.id = id;
		this.world = world;
		this.chunk = chunk;
	}

	public UUID getId() {
		return id;
	}

	public UUID getWorld() {
		return world;
	}

	public Chunk getChunk() {
		return chunk;
	}

	public Coordinate getChunkLocation() {
		return new Coordinate(chunk.getPosition());
	}

	public int getChunkPosX() {
		return chunk.getPosition().getX();
	}

	public int getChunkPosZ() {
		return chunk.getPosition().getZ();
	}
}
