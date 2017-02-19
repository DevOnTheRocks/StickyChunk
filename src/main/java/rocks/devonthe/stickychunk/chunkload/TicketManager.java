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

import org.spongepowered.api.world.World;
import org.spongepowered.mod.service.world.SpongeChunkTicketManager;
import rocks.devonthe.stickychunk.StickyChunk;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TicketManager extends SpongeChunkTicketManager {
	public TicketManager() {

	}

	public Optional<LoadingTicket> createTicket(World world) {
		return super.createTicket(StickyChunk.getInstance(), world);
	}

	public void validateLoadedRegions() {
		List<LoadedRegion> invalidRegions = StickyChunk.getInstance().getDataStore()
			.getCollatedRegions()
			.stream()
			.filter(region -> !region.isValid())
			.collect(Collectors.toList());

		invalidRegions.forEach(LoadedRegion::assignTicket);
	}
}
