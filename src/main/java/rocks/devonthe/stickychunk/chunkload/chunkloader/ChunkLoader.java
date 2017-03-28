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

package rocks.devonthe.stickychunk.chunkload.chunkloader;

import com.google.common.collect.ImmutableSet;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.ChunkTicketManager.LoadingTicket;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedChunk;
import rocks.devonthe.stickychunk.config.chunkloader.ChunkLoaderType;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.Entity;

@Entity(name = "chunkloader")
public abstract class ChunkLoader {

	private HashMap<World, LoadingTicket> tickets;
	private ChunkLoaderType chunkLoaderType;
	private ChunkLoaderFuelMode fuelType;
	private ArrayList<LoadedChunk> loadedChunks;
	private UUID worldId;
	private UUID owner;

	// Configurable properties
	private Duration offlineDuration;
	private boolean enabled;

	protected ChunkLoader(ChunkLoaderType type, World world, User owner) {
		this.chunkLoaderType = type;
		this.worldId = world.getUniqueId();
		this.owner = owner.getUniqueId();
		this.enabled = true;
	}

	public UUID getWorldId() {
		return worldId;
	}

	public Optional<World> getWorld() {
		return Sponge.getServer().getWorld(worldId);
	}

	public Optional<User> getOwner() {
		return StickyChunk.getInstance().getGame().getServiceManager().provideUnchecked(UserStorageService.class).get(owner);
	}

	public UUID getOwnerId() {
		return owner;
	}

	public void setOwner(UUID owner) {
		this.owner = owner;
	}

	public ImmutableSet<LoadedChunk> getLoadedChunks() {
		return ImmutableSet.copyOf(loadedChunks);
	}

	public ChunkLoaderType getChunkLoaderType() {
		return chunkLoaderType;
	}

	public void addChunk(LoadedChunk chunk) {
		this.loadedChunks.add(chunk);
	}

	public void addChunks(ArrayList<LoadedChunk> chunks) {
		this.loadedChunks.addAll(chunks);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Optional<Duration> getOfflineDuration() {
		return (offlineDuration == null || offlineDuration.isNegative()) ?
			Optional.empty() :
			Optional.of(offlineDuration);
	}

	public void forceChunks() {
		loadedChunks.forEach(chunk ->
			tickets.get(chunk.getChunk().getWorld()).forceChunk(chunk.getChunk().getPosition())
		);
	}

	public void unForceChunks() {
		loadedChunks.forEach(chunk ->
			tickets.get(chunk.getChunk().getWorld()).unforceChunk(chunk.getChunk().getPosition())
		);
	}

	public void releaseAllTickets() {
		unForceChunks();
		tickets.values().forEach(LoadingTicket::release);
		tickets.clear();
	}

	protected Text getEnabledText() {
		return Text.of(
			TextColors.DARK_GRAY, "Enabled",
			TextColors.WHITE, " : ",
			isEnabled()
				? Text.builder("ON")
				.color(TextColors.GREEN)
				.onClick(TextActions.executeCallback(src -> setEnabled(false)))
				.onHover(TextActions.showText(Text.of("Click to toggle.")))
				: Text.builder("OFF")
				.color(TextColors.RED)
				.onClick(TextActions.executeCallback(src -> setEnabled(true)))
				.onHover(TextActions.showText(Text.of("Click to toggle.")))
		);
	}
}