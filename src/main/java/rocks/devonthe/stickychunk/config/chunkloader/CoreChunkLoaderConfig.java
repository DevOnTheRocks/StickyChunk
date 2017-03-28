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
import java.util.Optional;

@ConfigSerializable
public abstract class CoreChunkLoaderConfig {
	@Setting(value = "Name", comment = "Name of the chunk loader used in options & commands.")
	private String name = "";
	@Setting(value = "Offline-Duration", comment = "The max amount time to keep chunks loaded when the player is offline.")
	private String duration = "1d";
	@Setting(value = "Price", comment = "The price per chunk, if any.")
	private double price = 0d;

	public CoreChunkLoaderConfig() {

	}

	public CoreChunkLoaderConfig(String name, String duration) {
		this.name = name;
		this.duration = duration;
	}

	public String getName() {
		return name;
	}

	public Optional<Duration> getDuration() {
		try {
			return Optional.of(Duration.parse(duration));
		} catch (DateTimeParseException e) {
			if (!duration.equalsIgnoreCase("forever") || !duration.equalsIgnoreCase("infinite")) {
				StickyChunk.getInstance().getLogger()
					.warn(String.format("Duration (%s) of %s is malformed. Using 1d instead", duration, name));
				return Optional.of(Duration.ofDays(1));
			}
			return Optional.empty();
		}
	}

	public double getPrice() {
		return price;
	}
}
