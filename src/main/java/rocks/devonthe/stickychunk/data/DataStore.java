/*
 * This file is part of StickyChunk by DevOnTheRocks, licensed under GPL-3.0
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
 */
package rocks.devonthe.stickychunk.data;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.database.IDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class DataStore {
	private Map<UUID, UserData> loadedUsers = new HashMap<UUID, UserData>();
	private Map<UUID, ArrayList<LoadedRegion>> loadedRegions = new HashMap<UUID, ArrayList<LoadedRegion>>();

	private IDatabase database = StickyChunk.getInstance().getDatabase();

	public ImmutableSet<UserData> getLoadedUsers() {
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

	public Optional<UserData> getUserData(User player) {
		return getUserData(player.getUniqueId());
	}

	public Optional<UserData> getUserData(UUID uuid) {
		return (loadedUsers.containsKey(uuid)) ?
				Optional.of(loadedUsers.get(uuid)) :
				Optional.empty();
	}

	public void addUsers(ArrayList<UserData> userDatas) {
		userDatas.forEach(user -> loadedUsers.put(user.getUniqueId(), user));
	}

	public void updateUser(UserData userData) {
		loadedUsers.put(userData.getUniqueId(), userData);
	}

	public ArrayList<LoadedRegion> getPlayerRegions(User player) {
		return getPlayerRegions(player.getUniqueId());
	}

	public ArrayList<LoadedRegion> getPlayerRegions(UUID uuid) {
		return (loadedRegions.containsKey(uuid)) ?
				loadedRegions.get(uuid) :
				loadedRegions.put(uuid, Lists.newArrayList());
	}

	public ArrayList<World> getPlayerRegionWorlds(User player) {
		return getPlayerRegionWorlds(player.getUniqueId());
	}

	public ArrayList<World> getPlayerRegionWorlds(UUID uuid) {
		ArrayList<World> worlds = new ArrayList<>();
		getPlayerRegions(uuid).forEach(loadedRegion -> worlds.add(loadedRegion.getWorld()));
		return worlds;
	}

	public void addPlayerRegions(HashMap<UUID, ArrayList<LoadedRegion>> regions) {
		loadedRegions.putAll(regions);
	}

	public void addPlayerRegions(User player, ArrayList<LoadedRegion> regions) {
		addPlayerRegions(player.getUniqueId(), regions);
	}

	public void addPlayerRegions(UUID uuid, ArrayList<LoadedRegion> regions) {
		if (loadedRegions.containsKey(uuid))
			loadedRegions.get(uuid).addAll(regions);
		else
			loadedRegions.put(uuid, regions);
	}

	public void addPlayerRegion(User player, LoadedRegion region) {
		if (loadedRegions.containsKey(player.getUniqueId())) {
			loadedRegions.get(player.getUniqueId()).add(region);
		} else {
			ArrayList<LoadedRegion> playerRegions = new ArrayList<>();
			playerRegions.add(region);
			loadedRegions.put(player.getUniqueId(), playerRegions);
		}
	}

	public void deletePlayerRegion(UUID player, UUID id) {
		getPlayerRegions(player).stream()
				.filter(region -> region.getUniqueId().equals(id))
				.findFirst()
				.ifPresent(region -> {
					loadedRegions.get(player).remove(region);
					database.deleteRegionData(region);
				});
	}

	public void deletePlayerRegion(User player, UUID id) {
		deletePlayerRegion(player.getUniqueId(), id);
	}

	public void addPlayerRegion(UUID uuid, LoadedRegion region) {
		loadedRegions.get(uuid).add(region);
		database.saveRegionData(region);
	}

	public boolean playerHasRegions(User player) {
		return playerHasRegions(player.getUniqueId());
	}

	public boolean playerHasRegions(UUID uuid) {
		return loadedRegions.containsKey(uuid);
	}
}
