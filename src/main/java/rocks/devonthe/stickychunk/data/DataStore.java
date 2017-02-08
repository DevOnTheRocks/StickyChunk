/*
 * This file is part of StickyChunk by DevOnTheRocks, licened under GPL-3.0
 *
 * Copyright (C) 2017 DevOnTheRocks
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * You should have received a copy of the GNU General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * The above notice and this permission notice shall be included in all copies
 * or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Created by Cossacksman on 24/01/2017.
 */
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
