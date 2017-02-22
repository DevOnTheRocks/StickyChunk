package rocks.devonthe.stickychunk.chunkload;


import org.spongepowered.api.world.Chunk;
import rocks.devonthe.stickychunk.world.Coordinate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.UUID;

@Entity(name = "chunk")
public class LoadedChunk {
	@Column(name = "id")
	private UUID id;
	@Column(name = "world")
	private UUID world;
	@Transient
	private Chunk chunk;

	public LoadedChunk() {}

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

	@Column(name = "chunkPosX")
	public int getChunkX() {
		return chunk.getPosition().getX();
	}

	@Column(name = "chunkPosZ")
	public int getChunkZ() {
		return chunk.getPosition().getZ();
	}
}
