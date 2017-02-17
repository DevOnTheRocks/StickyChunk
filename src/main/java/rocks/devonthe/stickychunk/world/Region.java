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
package rocks.devonthe.stickychunk.world;

import org.spongepowered.api.Server;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.data.DataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Region {
	private List<Chunk> chunks = new ArrayList<>();
	private Coordinate fromChunkPosition;
	private Coordinate toChunkPosition;
	private UUID worldId;
	private int area;

	private Server server = StickyChunk.getInstance().getGame().getServer();
	private DataStore dataStore = StickyChunk.getInstance().getDataStore();

	public Region(Coordinate from, Coordinate to, UUID world) {
		this.fromChunkPosition = from;
		chunks = new ArrayList<>();
		this.toChunkPosition = to;
		this.worldId = world;
	}

	public Region(Location<World> from, Location<World> to) {
		fromChunkPosition = new Coordinate(from.getChunkPosition());
		toChunkPosition = new Coordinate(to.getChunkPosition());
		chunks = new ArrayList<>();
		worldId = from.getExtent().getUniqueId();
	}

	public Region(Location<World> location) {
		chunks = new ArrayList<>();
//		location.getExtent().getChunk(location.getChunkPosition().getX(), 0, location.getChunkPosition().getZ()).ifPresent(chunks::add);
		fromChunkPosition = toChunkPosition = new Coordinate(location.getChunkPosition());
		worldId = location.getExtent().getUniqueId();

		StickyChunk.getInstance().getLogger().info(String.format("Creating region FROM X: %s AND Z: %s", fromChunkPosition.getX(), fromChunkPosition.getZ()));
		StickyChunk.getInstance().getLogger().info(String.format("TO X: %s AND Z: %s", toChunkPosition.getX(), toChunkPosition.getZ()));
	}

	/***
	 * Get the chunks in-between the two positions used to create the region
	 * @return a list of chunks in the region
	 */
	public List<Chunk> getChunkRange() {
		List<Chunk> chunks = new ArrayList<Chunk>();
		int height, width, pointerX, pointerZ, lowestX, lowestZ, highestX, highestZ;

		Optional<World> world = server.getWorld(getWorldId());

		if (world.isPresent()) {
			lowestX = Math.min(fromChunkPosition.getX(), toChunkPosition.getX());
			lowestZ = Math.min(fromChunkPosition.getZ(), toChunkPosition.getZ());
			highestX = Math.max(fromChunkPosition.getX(), toChunkPosition.getX());
			highestZ = Math.max(fromChunkPosition.getZ(), toChunkPosition.getZ());

			width = Math.abs(lowestX - highestX);
			height = Math.abs(lowestZ - highestZ);

			area = Math.abs((width + 1) * (height + 1));

			StickyChunk.getInstance().getLogger().info(String.format("Width: %s", width));
			StickyChunk.getInstance().getLogger().info(String.format("Height: %s", height));
			StickyChunk.getInstance().getLogger().info(String.format("WidthAfter: %s", width+1));
			StickyChunk.getInstance().getLogger().info(String.format("HeightAfter: %s", height+1));
			StickyChunk.getInstance().getLogger().info(String.format("Area: %s", area));

			pointerX = lowestX;
			pointerZ = lowestZ;

			for (int i = 0; i <= width; i++) {
				for (int l = 0; l <= height; l++) {

					StickyChunk.getInstance().getLogger().info(String.format("posX: %s", pointerX));
					StickyChunk.getInstance().getLogger().info(String.format("posZ: %s", pointerZ));

					boolean ispresent = world.get().getChunk(pointerX, 0, pointerZ).isPresent();
					StickyChunk.getInstance().getLogger().info(String.format("Chunk is present: %s", ispresent));

					world.get().getChunk(pointerX, 0, pointerZ).ifPresent(chunks::add);
					pointerZ++;
				}

				pointerZ = lowestZ;
				pointerX++;
			}
		}

		StickyChunk.getInstance().getLogger().info(String.format("Chunks: %s", chunks.size()));

		return chunks;
	}

	/***
	 * Get the chunks in this region
	 * @return a list of chunks from within this regions area
	 */
	public List<Chunk> getChunks() {
		if (chunks.isEmpty())
			chunks = getChunkRange();

		return chunks;
	}

	/***
	 * Get the position of the chunk this region starts at
	 * @return a Coordinate of the region start point
	 */
	public Coordinate getFrom() {
		return fromChunkPosition;
	}

	/***
	 * Get the position of the chunk this regions ends at
	 * @return a Coordinate of the region end point
	 */
	public Coordinate getTo() {
		return toChunkPosition;
	}

	/***
	 * Get the world ID this region is associated with
	 * @return the world ID this region uses
	 */
	public UUID getWorldId() {
		return worldId;
	}
}
