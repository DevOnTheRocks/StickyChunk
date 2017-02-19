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

import java.util.List;

@ConfigSerializable
public class ChunkLoaderConfig {
	@Setting(value = "Command")
	private List<CommandChunkLoaderConfig> commands = Lists.newArrayList();
	@Setting(value = "Block")
	private List<BlockChunkLoaderConfig> blocks = Lists.newArrayList();

	public ChunkLoaderConfig(String name, ChunkLoaderType type, String duration, boolean afk) {
		this.name = name;
		this.type = type.toString().toLowerCase();
		this.duration = duration;
		this.afk = afk;
	}

	private void addExampleConfigs() {
		commands.add(new CommandChunkLoaderConfig("personal", "0d", true, true));
		commands.add(new CommandChunkLoaderConfig("world", "1d", true, true));
		blocks.add(
			new BlockChunkLoaderConfig("basic", "0d", true, BlockTypes.IRON_BLOCK, ItemTypes.DIAMOND, ItemTypes.NONE,
				"8h"
			));
	}
}
