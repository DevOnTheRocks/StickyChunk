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
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.chunkloader.ChunkLoader;
import rocks.devonthe.stickychunk.config.chunkloader.ChunkLoaderType;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Optional;
import java.util.UUID;

public class UserData {
	private UUID user;
	private Date seen;
	private Date joined;
	private UniqueAccount account;
	private EnumMap<ChunkLoaderType, ArrayList<ChunkLoader>> chunkLoaders;

	public UserData(UUID id, Date joined, Date seen) {
		this.user = id;
		this.seen = seen;
		this.joined = joined;
		this.chunkLoaders = Maps.newEnumMap(ChunkLoaderType.class);

		StickyChunk.getInstance().getEconomyManager().ifPresent(economyManager -> {
			Optional<UniqueAccount> oAccount = economyManager.getOrCreateAccount(id);
			oAccount.ifPresent(uniqueAccount -> this.account = uniqueAccount);
		});
	}

	public UUID getUser() {
		return user;
	}

	public Date getLastSeen() {
		return seen;
	}

	public UserData setLastSeen(Date seen) {
		this.seen = seen;
		return this;
	}

	public Date getUserJoined() {
		return joined;
	}

	public BigDecimal getBalance(Currency currency) {
		return account.getBalance(currency);
	}

	public void update() {
		StickyChunk.getInstance().getDataStore().updateUserData(this);
	}

	public ArrayList<Chunk> getChunks(World world) {
		ArrayList<Chunk> chunks = Lists.newArrayList();
		chunkLoaders.values().forEach(activeChunkLoaders ->
			activeChunkLoaders.forEach(chunkLoader ->
				chunks.addAll(chunkLoader.getAllChunks())
			)
		);

		return chunks;
	}

	public ArrayList<Chunk> getChunks(ChunkLoaderType type, World world) {
		ArrayList<Chunk> chunks = Lists.newArrayList();
		chunkLoaders.get(type).forEach(chunkLoader ->
			chunks.addAll(chunkLoader.getAllChunks())
		);

		return chunks;
	}

	public ArrayList<Chunk> getCollatedChunks() {
		ArrayList<Chunk> chunks = Lists.newArrayList();
		chunkLoaders.values().forEach(activeChunkLoaders ->
			activeChunkLoaders.forEach(chunkLoader ->
				chunks.addAll(chunkLoader.getAllChunks())
			)
		);

		return chunks;
	}

	public ArrayList<World> getLoadedWorlds() {
		ArrayList<World> worlds = Lists.newArrayList();
		chunkLoaders.values().forEach(activeChunkLoaders ->
			activeChunkLoaders.forEach(chunkLoader ->
				worlds.addAll(chunkLoader.getAllWorlds())
			)
		);

		return worlds;
	}

	public ImmutableSet<ChunkLoader> getChunkLoaders() {
		ArrayList<ChunkLoader> usedChunkLoaders = Lists.newArrayList();
		chunkLoaders.values().forEach(usedChunkLoaders::addAll);
		return ImmutableSet.copyOf(usedChunkLoaders);
	}

	public ImmutableSet<ChunkLoader> getChunkLoader(ChunkLoaderType type) {
		return ImmutableSet.copyOf(chunkLoaders.get(type));
	}

	public void addChunkLoader(ChunkLoader chunkLoader, ChunkLoaderType type) {
		chunkLoaders.get(type).add(chunkLoader);
	}

	public void addChunkLoaders(ArrayList<ChunkLoader> newChunkLoaders, ChunkLoaderType type) {
		chunkLoaders.get(type).addAll(newChunkLoaders);
	}
}
