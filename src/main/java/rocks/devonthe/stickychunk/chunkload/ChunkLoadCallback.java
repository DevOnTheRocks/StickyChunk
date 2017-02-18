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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.world.ChunkTicketManager.Callback;
import org.spongepowered.api.world.ChunkTicketManager.LoadingTicket;
import org.spongepowered.api.world.ChunkTicketManager.OrderedCallback;
import org.spongepowered.api.world.ChunkTicketManager.PlayerOrderedCallback;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.data.DataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ChunkLoadCallback implements Callback, OrderedCallback, PlayerOrderedCallback {
	private DataStore dataStore = StickyChunk.getInstance().getDataStore();
	private Set<LoadedRegion> allRegions = dataStore.getCollatedRegions();
	private ArrayList<LoadedRegion> unassignedRegions = Lists.newArrayList(allRegions);

	/**
	 * Callback for loading player Tickets during world load.
	 * <p>
	 * During this callback you cannot associate loadedRegions to tickets. This
	 * callback gets all player-associated tickets registered by the plugin.
	 * Tickets absent from the returned Multimap will be released.
	 *
	 * @param tickets The list of loaded tickets by player
	 * @param world   The world tickets were loaded for
	 * @return All tickets per-player that you wish to keep
	 */
	public ListMultimap<UUID, LoadingTicket> onPlayerLoaded(ImmutableListMultimap<UUID, LoadingTicket> tickets, World world) {
		return null;
	}

	/**
	 * Callback for loading Tickets during world load.
	 * <p>
	 * The list of forced chunks is not saved with Tickets, this callback
	 * is your place to reassociate chunks to Tickets, using the extra
	 * information saved with the ticket or your own external configuration.
	 * Any unneeded tickets must be manually released.
	 * <p>
	 * The list of tickets contains both standard plugin and
	 * player-associated tickets that were registered by this plugin.
	 * <p>
	 * The list of tickets has been truncated to the maximum allowed for
	 * your plugin, so may not be all saved tickets in the event that the
	 * maximum tickets for your plugin was decreased.
	 *
	 * @param tickets The list of tickets that need chunks registered
	 * @param world   The world tickets were loaded for
	 */
	@Override
	public void onLoaded(ImmutableList<LoadingTicket> tickets, World world) {
		ArrayList<LoadedRegion> assignedRegions = Lists.newArrayList();

		StickyChunk.getInstance().getLogger().info("registering tickets");

		while (unassignedRegions.size() > 0) {
			int index = 0;
			for (LoadedRegion region : unassignedRegions) {
				if (world.getUniqueId().equals(region.getWorld().getUniqueId())) {
					region.assignTicket(tickets.get(index));
					assignedRegions.add(region);
					StickyChunk.getInstance().getLogger().info(String.format("registering ticket to %s", region.getOwner().toString()));
				}
				index++;
			}

			unassignedRegions.removeAll(assignedRegions);
		}

		assignedRegions.forEach(region -> dataStore.addPlayerRegion(region.getOwner(), region));
	}

	/**
	 * Callback for loading Tickets during world load.
	 * <p>
	 * During this callback you cannot associate chunks to tickets. This
	 * callback gets all loaded non-player tickets. The returned list will
	 * be truncated to maxTickets after this callback is called, and and
	 * tickets absent from the list will be released.
	 *
	 * @param tickets    The list of loaded tickets
	 * @param world      The world tickets were loaded for
	 * @param maxTickets The maximum tickets allowed for this plugin
	 * @return A list of all tickets you wish to keep
	 */
	@Override
	public List<LoadingTicket> onLoaded(ImmutableList<LoadingTicket> tickets, World world, int maxTickets) {
		List<LoadingTicket> personalTickets = Lists.newArrayList();
		List<LoadingTicket> worldTickets = Lists.newArrayList();
		List<LoadingTicket> toKeep = Lists.newArrayList();

		StickyChunk.getInstance().getLogger().info("sorting tickets");

		if (tickets.size() > maxTickets || unassignedRegions.size() > maxTickets) {
			tickets.forEach(ticket -> ticket.getCompanionData().getString(DataQuery.of("type")).ifPresent(type -> {
				switch (type) {
					case "world":
						worldTickets.add(ticket);
						break;
					case "personal":
						personalTickets.add(ticket);
						break;
					default:
						ticket.release();
						break;
				}

				StickyChunk.getInstance().getLogger().info("sorting ticket");
			}));

			toKeep.addAll(worldTickets);
			toKeep.addAll(personalTickets);

			StickyChunk.getInstance().getLogger().info(String.format("Returning sorted tickets of size: %s", toKeep.size()));

			return toKeep;
		} else {
			StickyChunk.getInstance().getLogger().info(String.format("Returning all tickets of size: %s", tickets.size()));

			return tickets;
		}
	}
}