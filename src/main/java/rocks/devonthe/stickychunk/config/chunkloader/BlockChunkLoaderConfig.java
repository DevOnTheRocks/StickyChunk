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

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.util.TextMessageException;
import rocks.devonthe.stickychunk.chunkload.ChunkLoaderFuel;

import java.util.List;

@ConfigSerializable
public class BlockChunkLoaderConfig extends CoreChunkLoaderConfig {
	@Setting(value = "Block-Id", comment = "The block used to represent the chunk loader. ex \"minecraft:quartz_block\"")
	private String blockId = BlockTypes.QUARTZ_BLOCK.getId();
	@Setting(value = "Item-Id", comment = "The item to used to activate the chunk loader. ex \"minecraft:quartz_block\"")
	private String itemId = ItemTypes.DIAMOND.getId();
	@Setting(value = "Fuel", comment = "A list of items and the duration that the item activates the chunk loader ex. \"minecraft:coal|1h\".")
	private List<String> fuels = Lists.newArrayList();

	public BlockChunkLoaderConfig() {
	}

	public BlockChunkLoaderConfig(String name, String duration, boolean afk, BlockType block, ItemType item) {
		super(name, duration, afk);
		this.blockId = block.getId();
		this.itemId = item.getId();
	}

	public BlockType getBlockId() {
		return Sponge.getRegistry().getType(BlockType.class, blockId).orElse(BlockTypes.AIR);
	}

	public ItemType getItemId() {
		return Sponge.getRegistry().getType(ItemType.class, itemId).orElse(ItemTypes.NONE);
	}

	public List<ChunkLoaderFuel> getFuels() {
		List<ChunkLoaderFuel> validFuels = Lists.newArrayList();
		fuels.forEach(fuel -> {
			try {
				validFuels.add(ChunkLoaderFuel.parse(fuel));
			} catch (TextMessageException e) {
				e.printStackTrace();
			}
		});
		return validFuels;
	}
}
