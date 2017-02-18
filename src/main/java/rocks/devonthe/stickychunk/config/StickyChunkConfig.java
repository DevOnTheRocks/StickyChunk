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
package rocks.devonthe.stickychunk.config;

import com.google.common.collect.Lists;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import rocks.devonthe.stickychunk.config.chunkloader.ChunkLoaderConfig;
import rocks.devonthe.stickychunk.config.chunkloader.CommandChunkLoaderConfig;
import rocks.devonthe.stickychunk.config.database.DatabaseConfig;

import java.util.List;

@ConfigSerializable
public class StickyChunkConfig {
	@Setting(value = "ChunkLoaders")
	private List<ChunkLoaderConfig> chunkLoaderConfigs = Lists.newArrayList();
	@Setting(value = "SqlDatabase")
	public DatabaseConfig database;
//	@Setting(value = "Misc")
//	public MiscConfig misc;

	public StickyChunkConfig() {
//		if (chunkLoaderConfigs.isEmpty())
//			chunkLoaderConfigs = addDefaultConfigs();

		database = new DatabaseConfig();
//		misc = new MiscConfig();
	}

	private List<ChunkLoaderConfig> addDefaultConfigs() {
		List<ChunkLoaderConfig> defaults = Lists.newArrayList();
		defaults.add(new CommandChunkLoaderConfig("personal", "0d", true, true));
		defaults.add(new CommandChunkLoaderConfig("world", "1d", true, true));
		return defaults;
	}
}