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
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.ItemTypes;
import rocks.devonthe.stickychunk.chunkload.chunkloader.ChunkLoaderFuel;
import rocks.devonthe.stickychunk.chunkload.chunkloader.ChunkLoaderFuelMode;

import java.time.Duration;
import java.util.List;

@ConfigSerializable
public class ChunkLoaderConfig {

	@Setting(value = "Block")
	private List<BlockChunkLoaderConfig> blocks = Lists.newArrayList();
	@Setting(value = "Command")
	private List<CommandChunkLoaderConfig> commands = Lists.newArrayList();

	public ChunkLoaderConfig() {
		if (commands.isEmpty() && blocks.isEmpty())
			addExampleConfigs();
	}

	private void addExampleConfigs() {
		commands.add(new CommandChunkLoaderConfig("personal", "0d", true));
		commands.add(new CommandChunkLoaderConfig("world", Duration.ofDays(1).toString(), true));
		blocks.add(new BlockChunkLoaderConfig("basic", "0d", BlockTypes.QUARTZ_BLOCK, ItemTypes.DIAMOND, ChunkLoaderFuelMode.ONLINE,
			Lists.newArrayList(ChunkLoaderFuel.of(ItemTypes.BLAZE_ROD, Duration.ofHours(1)))));
	}

	public List<BlockChunkLoaderConfig> getBlockBased() {
		return blocks;
	}

	public List<CommandChunkLoaderConfig> getCommandBased() {
		return commands;
	}
}
