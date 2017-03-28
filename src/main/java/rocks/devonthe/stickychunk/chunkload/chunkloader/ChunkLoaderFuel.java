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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.TextMessageException;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChunkLoaderFuel {

	private ItemType itemType;
	private Duration duration;

	public ChunkLoaderFuel(ItemType itemType, Duration duration) {
		this.itemType = itemType;
		this.duration = duration;
	}

	public ChunkLoaderFuel(String chunkLoaderFuel) throws TextMessageException {
		ChunkLoaderFuel clf = parse(chunkLoaderFuel);
		this.itemType = clf.getItemType();
		this.duration = clf.getDuration();
	}

	public static ChunkLoaderFuel of(ItemType itemType, Duration duration) {
		return new ChunkLoaderFuel(itemType, duration);
	}

	public ItemType getItemType() {
		return itemType;
	}

	public Duration getDuration() {
		return duration;
	}

	private String getDurationString() {
		return duration.toString().replace("P", "").replace("T", "");
	}

	public Text getDurationText() {
		long days = duration.toDays();
		long hours = duration.minusDays(days).toHours();
		long minutes = duration.minusDays(days).minusHours(hours).toMinutes();
		return Text.of(
			(days > 0) ? Text.of(TextColors.DARK_PURPLE, days, TextColors.GRAY, " day", (days != 1) ? "s" : Text.EMPTY, " ") : Text.EMPTY,
			(hours > 0) ? Text.of(TextColors.DARK_PURPLE, hours, TextColors.GRAY, " hour", (hours != 1) ? "s" : Text.EMPTY, " ") : Text.EMPTY,
			(minutes > 0) ? Text.of(TextColors.DARK_PURPLE, minutes, TextColors.GRAY, " minute", (minutes != 1) ? "s" : Text.EMPTY, " ") : Text.EMPTY
		);
	}

	private static ChunkLoaderFuel parse(String chunkLoaderFuel) throws TextMessageException {
		final Pattern PATTERN = Pattern.compile("([^|\\s]+?)[|](\\S+)", Pattern.CASE_INSENSITIVE);
		Matcher m = PATTERN.matcher(chunkLoaderFuel);
		if (m.matches()) {
			try {
				return ChunkLoaderFuel.of(
					Sponge.getRegistry().getType(ItemType.class, m.group(1)).get(),
					getDuration(m.group(2))
				);
			} catch (NoSuchElementException e) {
				throw new TextMessageException(Text.of("Encountered invalid fuel type: ", m.group(1)), e);
			} catch (DateTimeParseException e) {
				throw new TextMessageException(Text.of("Encountered invalid fuel duration: ", m.group(2)), e);
			}
		} else {
			throw new TextMessageException(Text.of("Encountered invalid fuel: ", chunkLoaderFuel));
		}
	}

	private static Duration getDuration(String string) {
		final Pattern PATTERN =
			Pattern.compile("(?=.*[DHM])(?:([0-9]+)D)?\\s*(?:([0-9]+)H)?\\s*(?:([0-9]+)M)?", Pattern.CASE_INSENSITIVE);
		Matcher m = PATTERN.matcher(string);
		if (m.matches()) {
			return Duration.parse(String.format("P%sDT%sH%sM0S",
				(m.group(1) != null) ? Long.parseLong(m.group(1)) : 0,
				(m.group(2) != null) ? Long.parseLong(m.group(2)) : 0,
				(m.group(3) != null) ? Long.parseLong(m.group(3)) : 0
			));
		} else {
			throw new DateTimeParseException("Encountered invalid fuel duration: ", string, 0);
		}
	}

	@Override
	public String toString() {
		return String.format(
			"%s|%s",
			itemType.getId(),
			getDurationString()
		);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ChunkLoaderFuel
			&& (this.itemType.equals(((ChunkLoaderFuel) obj).itemType)
			&& this.duration.equals(((ChunkLoaderFuel) obj).duration));
	}
}
