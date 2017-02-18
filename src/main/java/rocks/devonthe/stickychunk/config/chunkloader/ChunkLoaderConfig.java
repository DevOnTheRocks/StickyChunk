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
import rocks.devonthe.stickychunk.StickyChunk;

import java.time.Duration;
import java.time.format.DateTimeParseException;

@ConfigSerializable
public abstract class ChunkLoaderConfig {
	@Setting(value = "Name", comment = "Name of the chunk loader used in options & commands.")
	private String name = "";
	@Setting(value = "Type", comment = "The type of chunk loader. [command]")
	private String type = ChunkLoaderType.COMMAND.toString().toLowerCase();
	@Setting(value = "Offline-Duration", comment = "The amount time to keep the chunk(s) loaded when the owner is offline.")
	private String duration = "1d";
	@Setting(value = "Load-While-AFK", comment = "Whether the chunk stays loaded while the player is AFK.")
	private boolean afk = true;

	public ChunkLoaderConfig (String name, ChunkLoaderType type, String duration, boolean afk) {
		this.name = name;
		this.type = type.toString().toLowerCase();
		this.duration = duration;
		this.afk = afk;
	}

	public String getName() {
		return name;
	}

	public ChunkLoaderType getType() {
		return ChunkLoaderType.parse(type);
	}

	public Duration getDuration() {
		try {
			return Duration.parse(duration);
		} catch (DateTimeParseException e) {
			if (!duration.equalsIgnoreCase("forever") || !duration.equalsIgnoreCase("infinite")) {
				StickyChunk.getInstance().getLogger()
					.warn(String.format("Duration (%s) of %s is malformed. Using 1d instead", duration, name));
				return Duration.ofDays(1);
			}
			return null;
		}
	}

	public boolean isLoadedWhileAFK() {
		return afk;
	}
}
