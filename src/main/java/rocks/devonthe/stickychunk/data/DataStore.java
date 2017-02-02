package rocks.devonthe.stickychunk.data;

import com.google.common.collect.ImmutableSet;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import org.spongepowered.api.entity.living.player.Player;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

	public Optional<User> getUser(Player player) {
		return (users.containsKey(player.getUniqueId())) ? Optional.of(users.get(player.getUniqueId())) :  Optional.empty();
	}

	// TODO:- Turn all potential null values into optionals
	public Optional<User> getUser(UUID uuid) {
		return (users.containsKey(uuid)) ?
				Optional.of(users.get(uuid)) :
				Optional.empty();
	}

	public void updateUser(User user) {
		users.put(user.getUniqueId(), user);
	}

	public Optional<ArrayList<LoadedRegion>> getPlayerRegions(Player player) {
		return (loadedRegions.containsKey(player.getUniqueId())) ?
				Optional.of(loadedRegions.get(player.getUniqueId())) :
				Optional.empty();
	}

	public Optional<ArrayList<LoadedRegion>> getPlayerRegions(UUID uuid) {
		return (loadedRegions.containsKey(uuid)) ?
				Optional.of(loadedRegions.get(uuid)) :
				Optional.empty();
	}

	public void addPlayerChunks(Player player, ArrayList<LoadedRegion> regions) {
		if (loadedRegions.containsKey(player.getUniqueId())) {
			loadedRegions.get(player.getUniqueId()).addAll(regions);
		} else {
			ArrayList<LoadedRegion> playerRegions = new ArrayList<>();
			playerRegions.addAll(regions);
			loadedRegions.put(player.getUniqueId(), playerRegions);
		}
	}

	public void addPlayerChunks(UUID uuid, ArrayList<LoadedRegion> regions) {
		loadedRegions.get(uuid).addAll(regions);
	}

	public void addPlayerChunk(Player player, LoadedRegion region) {
		if (loadedRegions.containsKey(player.getUniqueId())) {
			loadedRegions.get(player.getUniqueId()).add(region);
		} else {
			ArrayList<LoadedRegion> playerRegions = new ArrayList<>();
			playerRegions.add(region);
			loadedRegions.put(player.getUniqueId(), playerRegions);
		}
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
