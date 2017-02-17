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
import com.google.common.collect.Maps;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.database.IDatabase;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataStore {
	private Map<UUID, UserData> loadedUsers = Maps.newHashMap();
	private Map<UUID, ArrayList<LoadedRegion>> loadedRegions = Maps.newHashMap();

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
		ArrayList<LoadedRegion> regions = Lists.newArrayList();
		loadedRegions.values().forEach(regions::addAll);

		return ImmutableSet.copyOf(regions);
	}

	public UserData getOrCreateUserData(User user) {
		return getOrCreateUserData(user.getUniqueId());
	}

	public UserData getOrCreateUserData(UUID uuid) {
		Date now = new Date(java.util.Date.from(Instant.now()).getTime());

		if (loadedUsers.containsKey(uuid)) {
			return loadedUsers.get(uuid);
		} else {
			loadedUsers.put(uuid, new UserData(uuid, now, now));
			return loadedUsers.get(uuid);
		}
	}

	public void addUsers(ArrayList<UserData> userDatas) {
		userDatas.forEach(user -> loadedUsers.put(user.getUniqueId(), user));
	}

	public void updateUser(UserData userData) {
		loadedUsers.put(userData.getUniqueId(), userData);
	}

	public ArrayList<LoadedRegion> getPlayerRegions(User user) {
		return getPlayerRegions(user.getUniqueId());
	}

	public ArrayList<LoadedRegion> getPlayerRegions(UUID uuid) {
		if (loadedRegions.containsKey(uuid)) {
			return loadedRegions.get(uuid);
		} else {
			loadedRegions.put(uuid, Lists.newArrayList());
			return loadedRegions.get(uuid);
		}
	}

	public ArrayList<World> getPlayerRegionWorlds(User user) {
		return getPlayerRegionWorlds(user.getUniqueId());
	}

	public ArrayList<World> getPlayerRegionWorlds(UUID uuid) {
		ArrayList<World> worlds = Lists.newArrayList();
		getPlayerRegions(uuid).forEach(loadedRegion -> {
			if (!worlds.contains(loadedRegion.getWorld()))
				worlds.add(loadedRegion.getWorld());
		});
		return worlds;
	}

	public void addPlayerRegions(HashMap<UUID, ArrayList<LoadedRegion>> regions) {
		loadedRegions.putAll(regions);
	}

	public void addPlayerRegions(User user, ArrayList<LoadedRegion> regions) {
		addPlayerRegions(user.getUniqueId(), regions);
	}

	public void addPlayerRegions(UUID uuid, ArrayList<LoadedRegion> regions) {
		if (loadedRegions.containsKey(uuid))
			loadedRegions.get(uuid).addAll(regions);
		else
			loadedRegions.put(uuid, regions);
	}

	public void addPlayerRegion(User user, LoadedRegion region) {
		addPlayerRegion(user.getUniqueId(), region);
	}

	public void addPlayerRegion(UUID uuid, LoadedRegion region) {
		StickyChunk.getInstance().getLogger().info(String.format("UUID is %s and region owner is %s", uuid, region.getOwner()));

		if (loadedRegions.containsKey(uuid)) {
			StickyChunk.getInstance().getLogger().info("key exists");
			loadedRegions.get(uuid).add(region);
		} else {
			StickyChunk.getInstance().getLogger().info("key doesn't exist");
			ArrayList<LoadedRegion> playerRegions = Lists.newArrayList();
			playerRegions.add(region);
			loadedRegions.put(uuid, playerRegions);
		}
	}

	public void deletePlayerRegion(User user, UUID id) {
		deletePlayerRegion(user.getUniqueId(), id);
	}

	public void deletePlayerRegion(UUID user, UUID id) {
		StickyChunk.getInstance().getLogger().info("delete called");

		if (user == null)
			StickyChunk.getInstance().getLogger().info("user is null");
		if (id == null)
			StickyChunk.getInstance().getLogger().info("user is null");
		if (getPlayerRegions(user) == null)
			StickyChunk.getInstance().getLogger().info("regions is null");

		getPlayerRegions(user).stream()
			.filter(region -> region.getUniqueId().equals(id))
			.findAny()
			.ifPresent(region -> {
				loadedRegions.get(user).remove(region);
				database.deleteRegionData(region);
			});
	}

	public boolean playerHasRegions(User user) {
		return playerHasRegions(user.getUniqueId());
	}

	public boolean playerHasRegions(UUID uuid) {
		return loadedRegions.containsKey(uuid);
	}
}
