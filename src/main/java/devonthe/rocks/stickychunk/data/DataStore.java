package devonthe.rocks.stickychunk.data;

import com.google.common.collect.ImmutableSet;
import devonthe.rocks.stickychunk.chunkload.LoadedRegion;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Cossacksman on 24/01/2017.
 */
public class DataStore {
	private Map<UUID, User> users = new HashMap<UUID, User>();
	private Map<UUID, ArrayList<LoadedRegion>> loadedRegions = new HashMap<UUID, ArrayList<LoadedRegion>>();

	public ImmutableSet<User> getUsers() {
		return ImmutableSet.copyOf(users.values());
	}

	public ImmutableSet<UUID> getPlayers() {
		return ImmutableSet.copyOf(loadedRegions.keySet());
	}

	public ImmutableSet<ArrayList<LoadedRegion>> getRegions() {
		return ImmutableSet.copyOf(loadedRegions.values());
	}

	public ImmutableSet<LoadedRegion> getCollatedRegions() {
		ArrayList<LoadedRegion> regions = new ArrayList<LoadedRegion>();
		loadedRegions.values().forEach(regions::addAll);
		return ImmutableSet.copyOf(regions);
	}

	public User getUser(Player player) {
		return users.get(player.getUniqueId());
	}

	public User getUser(UUID uuid) {
		return users.get(uuid);
	}

	public void updateUser(User user) {
		users.put(user.getUniqueId(), user);
	}

	public ArrayList<LoadedRegion> getPlayerRegions(Player player) {
		return loadedRegions.get(player.getUniqueId());
	}

	public ArrayList<LoadedRegion> getPlayerRegions(UUID uuid) {
		return loadedRegions.get(uuid);
	}

	public void addPlayerChunks(Player player, ArrayList<LoadedRegion> regions) {
		loadedRegions.get(player.getUniqueId()).addAll(regions);
	}

	public void addPlayerChunks(UUID uuid, ArrayList<LoadedRegion> regions) {
		loadedRegions.get(uuid).addAll(regions);
	}

	public void addPlayerChunk(Player player, LoadedRegion region) {
		loadedRegions.get(player.getUniqueId()).add(region);
	}

	public void addPlayerChunk(UUID uuid, LoadedRegion region) {
		loadedRegions.get(uuid).add(region);
	}

	public boolean playerHasRegions(Player player) {
		return loadedRegions.containsKey(player.getUniqueId());
	}

	public boolean playerHasRegions(UUID uuid) {
		return loadedRegions.containsKey(uuid);
	}
}
