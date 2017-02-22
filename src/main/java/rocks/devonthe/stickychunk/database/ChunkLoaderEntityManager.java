package rocks.devonthe.stickychunk.database;

import org.hibernate.Session;
import rocks.devonthe.stickychunk.chunkload.chunkloader.ChunkLoader;
import rocks.devonthe.stickychunk.database.EntityManager;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class ChunkLoaderEntityManager extends EntityManager {
	public ArrayList<ChunkLoader> load() {
		Session session = getSession();
		return null;
	}

	public Optional<ChunkLoader> load(UUID id) {
		Session session = getSession();
		return Optional.empty();
	}

	public void save(ArrayList<ChunkLoader> chunkLoaders) {
		Session session = getSession();
	}

	public void save(ChunkLoader chunkLoader) {
		Session session = getSession();
	}

	public void update(ArrayList<ChunkLoader> chunkLoaders) {
		Session session = getSession();
	}

	public void update(ChunkLoader chunkLoader) {
		Session session = getSession();
	}
}
