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
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.chunkload.chunkloader.ChunkLoader;
import rocks.devonthe.stickychunk.chunkload.LoadedChunk;
import rocks.devonthe.stickychunk.config.chunkloader.ChunkLoaderType;

import java.sql.Date;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

public class DataStore {
	private Map<UUID, UserData> userChunkData = Maps.newHashMap();

	public ImmutableSet<UUID> getAllUsers() {
		return ImmutableSet.copyOf(userChunkData.keySet());
	}

	public ImmutableSet<UserData> getAllUserData() {
		return ImmutableSet.copyOf(userChunkData.values());
	}

	public ImmutableSet<ChunkLoader> getCollatedChunkLoaders() {
		ArrayList<ChunkLoader> chunkLoaders = Lists.newArrayList();
		userChunkData.values().forEach(userData ->
			chunkLoaders.addAll(userData.getChunkLoaders())
		);

		return ImmutableSet.copyOf(chunkLoaders);
	}

	public ImmutableSet<LoadedChunk> getCollatedChunks() {
		ArrayList<LoadedChunk> chunks = Lists.newArrayList();
		userChunkData.values().forEach(userData ->
			chunks.addAll(userData.getCollatedChunks())
		);

		return ImmutableSet.copyOf(chunks);
	}

	public ImmutableSet<World> getUserWorlds(UUID uuid) {
		UserData userData = (userChunkData.containsKey(uuid)) ?
							userChunkData.get(uuid) :
							getOrCreateUserData(uuid);

		return ImmutableSet.copyOf(userData.getLoadedWorlds());
	}

	public ImmutableSet<World> getUserWorlds(User user) {
		return getUserWorlds(user.getUniqueId());
	}

	public ImmutableSet<LoadedChunk> getUserChunks(UUID uuid) {
		UserData userData = (userChunkData.containsKey(uuid)) ?
							userChunkData.get(uuid) :
							getOrCreateUserData(uuid);

		return ImmutableSet.copyOf(userData.getCollatedChunks());
	}

	public ImmutableSet<LoadedChunk> getUserChunks(User user) {
		return getUserChunks(user.getUniqueId());
	}

	public ImmutableSet<LoadedChunk> getUserChunks(UUID uuid, World world) {
		UserData userData = (userChunkData.containsKey(uuid)) ?
							userChunkData.get(uuid) :
							getOrCreateUserData(uuid);

		return ImmutableSet.copyOf(userData.getChunks(world));
	}

	public ImmutableSet<LoadedChunk> getUserChunks(User user, World world) {
		return getUserChunks(user.getUniqueId());
	}

	public ImmutableSet<LoadedChunk> getUserChunks(UUID uuid, World world, ChunkLoaderType type) {
		UserData userData = (userChunkData.containsKey(uuid)) ?
							userChunkData.get(uuid) :
							getOrCreateUserData(uuid);

		return ImmutableSet.copyOf(userData.getChunks(type, world));
	}

	public ImmutableSet<LoadedChunk> getUserChunks(User user, World world, ChunkLoaderType type) {
		return getUserChunks(user.getUniqueId(), world, type);
	}

	public ImmutableSet<ChunkLoader> getUserChunkLoaders(UUID uuid) {
		UserData userData = (userChunkData.containsKey(uuid)) ?
							userChunkData.get(uuid) :
							getOrCreateUserData(uuid);

		return userData.getChunkLoaders();
	}

	public ImmutableSet<ChunkLoader> getUserChunkLoaders(User user) {
		return getUserChunkLoaders(user.getUniqueId());
	}

	public void addUserChunkLoader(UUID uuid, ChunkLoaderType type, ChunkLoader chunkLoader) {
		UserData userData = (userChunkData.containsKey(uuid)) ?
							userChunkData.get(uuid) :
							getOrCreateUserData(uuid);

		userData.addChunkLoader(chunkLoader, type);
		userChunkData.put(uuid, userData);
	}

	public void addUserChunkLoader(User user, ChunkLoaderType type, ChunkLoader chunkLoader) {
		addUserChunkLoader(user.getUniqueId(), type, chunkLoader);
	}

	public void addUserChunkLoaders(UUID uuid, ChunkLoaderType type, ArrayList<ChunkLoader> chunkLoaders) {
		UserData userData = (userChunkData.containsKey(uuid)) ?
							userChunkData.get(uuid) :
							getOrCreateUserData(uuid);

		userData.addChunkLoaders(chunkLoaders, type);
		userChunkData.put(uuid, userData);
	}

	public void addUserChunkLoaders(User user, ChunkLoaderType type, ArrayList<ChunkLoader> chunkLoaders) {
		addUserChunkLoaders(user.getUniqueId(), type, chunkLoaders);
	}

	public void deleteUserChunkLoader(UUID uuid, ChunkLoader chunkLoader) {
		userChunkData.get(uuid).removeChunkLoader(chunkLoader);
		// TODO:- Update DataBase
	}

	public void deleteUserChunkLoader(User user, ChunkLoader chunkLoader) {
		deleteUserChunkLoader(user.getUniqueId(), chunkLoader);
	}

	/**
	 * A method to remove ChunkLoader instances from a UserData without
	 * effecting the database. Allowing the ChunkLoader to be re-loaded.
	 *
	 * @param uuid The UUID of the UserData the ChunkLoader belongs to
	 * @param chunkLoader The ChunkLoader to remove from the UserData
	 */
	public void removeChunkLoader(UUID uuid, ChunkLoader chunkLoader) {
		userChunkData.get(uuid).removeChunkLoader(chunkLoader);
	}

	public boolean isChunkLoaded(Chunk chunk) {
		return getCollatedChunks().stream()
			.anyMatch(loadedChunk ->
				loadedChunk.getChunk().getUniqueId().equals(chunk.getUniqueId()) &&
					loadedChunk.getChunk().getWorld().getUniqueId().equals(chunk.getWorld().getUniqueId())
			);
	}

	public UserData getOrCreateUserData(UUID uuid) {
		Date now = new Date(java.util.Date.from(Instant.now()).getTime());

		if (userChunkData.containsKey(uuid)) {
			return userChunkData.get(uuid);
		} else {
			UserData userData = new UserData(uuid, now, now);
			userChunkData.put(uuid, userData);
			return userData;
		}
	}

	public UserData getOrCreateUserData(User user) {
		return getOrCreateUserData(user.getUniqueId());
	}

	public void updateUserData(UserData userData) {
		userChunkData.put(userData.getUser(), userData);
	}
}
