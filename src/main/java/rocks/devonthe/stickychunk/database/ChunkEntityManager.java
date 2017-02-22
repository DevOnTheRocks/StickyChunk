package rocks.devonthe.stickychunk.database;

import org.hibernate.Session;
import rocks.devonthe.stickychunk.chunkload.chunkloader.LoadedChunk;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class ChunkEntityManager extends EntityManager {
	public ArrayList<LoadedChunk> load() {
		Session session = getSession();
		return null;
	}

	public Optional<LoadedChunk> load(UUID id) {
		Session session = getSession();
		return Optional.empty();
	}

	public void save(ArrayList<LoadedChunk> chunks) {
		Session session = getSession();
	}

	public void save(LoadedChunk chunk) {
		Session session = getSession();
	}

	public void update(ArrayList<LoadedChunk> chunks) {
		Session session = getSession();
	}

	public void update(LoadedChunk chunk) {
		Session session = getSession();
	}
}
