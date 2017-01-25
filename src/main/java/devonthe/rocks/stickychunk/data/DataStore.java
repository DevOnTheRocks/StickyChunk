package devonthe.rocks.stickychunk.data;

import com.google.common.collect.ImmutableSet;
import devonthe.rocks.stickychunk.chunkload.LoadedRegion;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Cossacksman on 24/01/2017.
 */
public class DataStore {
	private Map<UUID, User> users = new HashMap<UUID, User>();
	private Map<UUID, ArrayList<LoadedRegion>> loadedChunks = new HashMap<UUID, ArrayList<LoadedRegion>>();

	public ImmutableSet<User> getUsers() {
		return ImmutableSet.copyOf(users.values());
	}

	public ImmutableSet<UUID> getPlayers() {
		return ImmutableSet.copyOf(loadedChunks.keySet());
	}

	public ImmutableSet<ArrayList<LoadedRegion>> getRegions() {
		return ImmutableSet.copyOf(loadedChunks.values());
	}

	public ImmutableSet<LoadedRegion> getCollatedRegions() {
		ArrayList<LoadedRegion> regions = new ArrayList<LoadedRegion>();
		loadedChunks.values().forEach(regions::addAll);
		return ImmutableSet.copyOf(regions);
	}

	public User getUser(Player player) {
		return users.get(player.getUniqueId());
	}

	public User getUser(UUID uuid) {
		return users.get(uuid);
	}

	public ArrayList<LoadedRegion> getPlayerRegions(Player player) {
		return loadedChunks.get(player.getUniqueId());
	}

	public ArrayList<LoadedRegion> getPlayerRegions(UUID uuid) {
		return loadedChunks.get(uuid);
	}

	public void addPlayerChunks(Player player, ArrayList<LoadedRegion> regions) {
		loadedChunks.get(player.getUniqueId()).addAll(regions);
	}

	public void addPlayerChunks(UUID uuid, ArrayList<LoadedRegion> regions) {
		loadedChunks.get(uuid).addAll(regions);
	}

	public void addPlayerChunk(Player player, LoadedRegion region) {
		loadedChunks.get(player.getUniqueId()).add(region);
	}

	public void addPlayerChunk(UUID uuid, LoadedRegion region) {
		loadedChunks.get(uuid).add(region);
	}

	public boolean playerHasRegions(Player player) {
		return loadedChunks.containsKey(player.getUniqueId());
	}

	public boolean playerHasRegions(UUID uuid) {
		return loadedChunks.containsKey(uuid);
	}
}
