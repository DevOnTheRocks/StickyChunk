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

package rocks.devonthe.stickychunk.chunkload;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.TextMessageException;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;

public class ChunkLoaderFuel {
	private ItemType itemType;
	private Duration duration;

	public ChunkLoaderFuel(ItemType itemType, Duration duration) {
		this.itemType = itemType;
		this.duration = duration;
	}

	public ChunkLoaderFuel(String chunkLoaderFuel) throws TextMessageException {
		parse(chunkLoaderFuel);
	}

	public static ChunkLoaderFuel parse(String chunkLoaderFuel) throws TextMessageException {
		try {
			String[] split = chunkLoaderFuel.split("|");
			return new ChunkLoaderFuel(
				Sponge.getRegistry().getType(ItemType.class, split[0]).get(),
				Duration.parse(split[1])
			);
		} catch (NoSuchElementException | DateTimeParseException e) {
			throw new TextMessageException(Text.of("Encountered invalid fuel: ", chunkLoaderFuel), e);
		}
	}

	@Override
	public String toString() {
		return String.format("%s|%s", itemType.getId(), duration.toString());
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ChunkLoaderFuel
			&& (this.itemType.equals(((ChunkLoaderFuel) obj).itemType)
			&& this.duration.equals(((ChunkLoaderFuel) obj).duration));
	}
}
