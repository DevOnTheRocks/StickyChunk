package devonthe.rocks.stickychunk.chunkload;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ListMultimap;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.ChunkTicketManager.Callback;
import org.spongepowered.api.world.ChunkTicketManager.OrderedCallback;
import org.spongepowered.api.world.ChunkTicketManager.PlayerOrderedCallback;
import org.spongepowered.api.world.ChunkTicketManager.LoadingTicket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class ChunkLoadCallback implements Callback, OrderedCallback, PlayerOrderedCallback {

	/**
	 * Callback for loading player Tickets during world load.
	 * 
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
	 * 
	 * The list of forced chunks is not saved with Tickets, this callback
	 * is your place to reassociate chunks to Tickets, using the extra
	 * information saved with the ticket or your own external configuration.
	 * Any unneeded tickets must be manually released.
	 * 
	 * The list of tickets contains both standard plugin and
	 * player-associated tickets that were registered by this plugin.
	 * 
	 * The list of tickets has been truncated to the maximum allowed for
	 * your plugin, so may not be all saved tickets in the event that the
	 * maximum tickets for your plugin was decreased.
	 *
	 * @param tickets The list of tickets that need chunks registered
	 * @param world   The world tickets were loaded for
	 */
	@Override
	public void onLoaded(ImmutableList<LoadingTicket> tickets, World world) {
		// Associate chunks to tickets using the DB data
	}

	/**
	 * Callback for loading Tickets during world load.
	 * 
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
//		List<LoadingTicket> worldTickets = new ArrayList<>();
//		List<LoadingTicket> personalTickets = new ArrayList<>();
		List<LoadingTicket> orderedTickets = new ArrayList<>();

		if (tickets.size() > maxTickets) {
			for (LoadingTicket ticket : tickets) {
				// TODO - Get & sort by type
				orderedTickets.add(ticket);
			}
		} else {
			orderedTickets = tickets;
		}

		return ImmutableList.copyOf(orderedTickets);
	}
}