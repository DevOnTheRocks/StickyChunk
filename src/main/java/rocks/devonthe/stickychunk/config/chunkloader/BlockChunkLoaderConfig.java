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
package rocks.devonthe.stickychunk.config.chunkloader;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import rocks.devonthe.stickychunk.StickyChunk;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@ConfigSerializable
public class BlockChunkLoaderConfig extends CoreChunkLoaderConfig {
	@Setting(value = "Block-ID", comment = "The block to use as a chunk loader.")
	private String blockId = "minecraft:iron_block";
	@Setting(value = "Item-ID", comment = "The item to use to activate the chunk loader.")
	private String itemId = "minecraft:diamond";
	@Setting(value = "Fuel", comment = "The item to use as fuel, if any.")
	private String fuel = "";
	@Setting(value = "Fuel-Duration", comment = "The amount of time to activate the chunk loader for each fuel consumed.")
	private String fuelDuration = "8h";

	public BlockChunkLoaderConfig(String name, String duration, boolean afk, BlockType block, ItemType item, ItemType fuel, String fuelDuration) {
		super(name, duration, afk);
		this.blockId = block.getId();
		this.itemId = item.getId();
		this.fuel = fuel.getId();
		this.fuelDuration = fuelDuration;
	}

	public BlockType getBlockId() {
		return Sponge.getRegistry().getType(BlockType.class, blockId).orElse(BlockTypes.AIR);
	}

	public ItemType getItemId() {
		return Sponge.getRegistry().getType(ItemType.class, itemId).orElse(ItemTypes.NONE);
	}

	public Optional<ItemType> getFuel() {
		return Optional.ofNullable(Sponge.getRegistry().getType(ItemType.class, fuel).orElse(null));
	}

	public Duration getFuelDuration() {
		try {
			return Duration.parse(fuelDuration);
		} catch (DateTimeParseException e) {
			StickyChunk.getInstance().getLogger().warn(String.format("Fuel-Duration (%s) of %s is malformed. Using 8h instead", fuelDuration, getName()));
			return Duration.ofHours(8);
		}
	}
}
