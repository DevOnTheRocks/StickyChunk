package rocks.devonthe.stickychunk.data;

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.entity.living.player.Player;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.database.IDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Cossacksman on 24/01/2017.
 */
public class DataStore {
	private Map<UUID, User> loadedUsers = new HashMap<UUID, User>();
	private Map<UUID, ArrayList<LoadedRegion>> loadedRegions = new HashMap<UUID, ArrayList<LoadedRegion>>();

	private IDatabase database = StickyChunk.getInstance().getDatabase();

	public ImmutableSet<User> getLoadedUsers() {
		return ImmutableSet.copyOf(loadedUsers.values());
	}

	public ImmutableSet<UUID> getPlayers() {
		return ImmutableSet.copyOf(loadedRegions.keySet());
	}

	public ImmutableSet<ArrayList<LoadedRegion>> getRegions() {
		return ImmutableSet.copyOf(loadedRegions.values());
	}

	public ImmutableSet<LoadedRegion> getCollatedRegions() {
		ArrayList<LoadedRegion> regions = new ArrayList<>();
		loadedRegions.values().forEach(regions::addAll);

		StickyChunk.getInstance().getLogger().info(String.format("There are %s chunks.", loadedRegions.size()));
		StickyChunk.getInstance().getLogger().info(String.format("%s are copied.", regions.size()));

		return ImmutableSet.copyOf(regions);
	}

	public Optional<User> getUser(Player player) {
		return (loadedUsers.containsKey(player.getUniqueId())) ? Optional.of(loadedUsers.get(player.getUniqueId())) : Optional.empty();
	}

	public Optional<User> getUser(UUID uuid) {
		return (loadedUsers.containsKey(uuid)) ?
				Optional.of(loadedUsers.get(uuid)) :
				Optional.empty();
	}

	public void addUsers(ArrayList<User> users) {
		users.forEach(user -> loadedUsers.put(user.getUniqueId(), user));
	}

	public void updateUser(User user) {
		loadedUsers.put(user.getUniqueId(), user);
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

	public void addPlayerRegions(HashMap<UUID, ArrayList<LoadedRegion>> regions) {
		loadedRegions.putAll(regions);
	}

	public void addPlayerRegions(Player player, ArrayList<LoadedRegion> regions) {
		if (loadedRegions.containsKey(player.getUniqueId())) {
			loadedRegions.get(player.getUniqueId()).addAll(regions);
		} else {
			ArrayList<LoadedRegion> playerRegions = new ArrayList<>();
			playerRegions.addAll(regions);
			loadedRegions.put(player.getUniqueId(), playerRegions);
		}
	}

	public void addPlayerRegions(UUID uuid, ArrayList<LoadedRegion> regions) {
		loadedRegions.get(uuid).addAll(regions);
		database.saveRegionData(regions);
	}

	public void addPlayerRegion(Player player, LoadedRegion region) {
		if (loadedRegions.containsKey(player.getUniqueId())) {
			loadedRegions.get(player.getUniqueId()).add(region);
		} else {
			ArrayList<LoadedRegion> playerRegions = new ArrayList<>();
			playerRegions.add(region);
			loadedRegions.put(player.getUniqueId(), playerRegions);
		}
	}

	public void deletePlayerRegion(UUID player, UUID id) {
		getPlayerRegions(player).ifPresent(regions -> regions.stream()
				.filter(region -> region.getUniqueId().equals(id))
				.findFirst()
				.ifPresent(region -> {
					loadedRegions.get(player).remove(region);
					database.deleteRegionData(region);
				}));
	}

	public void deletePlayerRegion(Player player, UUID id) {
		getPlayerRegions(player).ifPresent(regions -> regions.stream()
				.filter(region -> region.getUniqueId().equals(id))
				.findFirst()
				.ifPresent(region -> {
					loadedRegions.get(player.getUniqueId()).remove(region);
					database.deleteRegionData(region);
				}));
	}

	public void addPlayerRegion(UUID uuid, LoadedRegion region) {
		loadedRegions.get(uuid).add(region);
		database.saveRegionData(region);
	}

	public boolean playerHasRegions(Player player) {
		return loadedRegions.containsKey(player.getUniqueId());
	}

	public boolean playerHasRegions(UUID uuid) {
		return loadedRegions.containsKey(uuid);
	}
}
