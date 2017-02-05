package rocks.devonthe.stickychunk.chunkload;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import org.spongepowered.api.world.ChunkTicketManager.Callback;
import org.spongepowered.api.world.ChunkTicketManager.LoadingTicket;
import org.spongepowered.api.world.ChunkTicketManager.OrderedCallback;
import org.spongepowered.api.world.ChunkTicketManager.PlayerOrderedCallback;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.StickyChunk;

import java.util.List;
import java.util.UUID;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class ChunkLoadCallback implements Callback, OrderedCallback, PlayerOrderedCallback {
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
		int index[] = new int[1];

		StickyChunk.getInstance().getLogger().info("Restoring regions...");

		StickyChunk.getInstance().getDataStore().getCollatedRegions().forEach(region -> {
			if (index[0] <= tickets.size()) {
				region.assignTicket(tickets.get(index[0]));
				index[0]++;
			}
		});

		int regionCount = StickyChunk.getInstance().getDataStore().getCollatedRegions().size();
		StickyChunk.getInstance().getLogger().info(String.format("Restored %s regions", regionCount));
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
		// Redundant until Sponge fixes Ticket NBT data
		return tickets;
	}
}