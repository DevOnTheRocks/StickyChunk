package devonthe.rocks.stickychunk.chunkload;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.ForgeChunkManager;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.world.ChunkTicketManager;
import org.spongepowered.api.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.common.SpongeImpl;
import org.spongepowered.common.data.nbt.NbtDataType;
import org.spongepowered.common.data.util.NbtDataUtil;
import org.spongepowered.common.mixin.core.nbt.MixinNBTTagCompound;
import org.spongepowered.common.util.VecHelper;
import org.spongepowered.mod.service.world.SpongeChunkTicketManager;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Cossacksman on 22/01/2017.
 */
@Mixin(SpongeChunkTicketManager.class)
public abstract class MixinSpongeLoadingTicket implements ChunkTicketManager, ITicketData {

	/**
	 * Sets the callback for handling loading forced chunk tickets on world
	 * load.
	 * <p>
	 * <p><b>Required</b> for any plugin that wants to force-load chunks. Any
	 * plugin that does not have a registered callback will have all saved
	 * tickets dropped on world load.</p>
	 *
	 * @param plugin   Plugin that is registering a callback
	 * @param callback The callback function object
	 */
	@Shadow
	public void registerCallback(Object plugin, Callback callback){}

	/**
	 * Attempts to create a new loading ticket for a plugin to load chunks in a
	 * world.
	 * <p>
	 * <p>Plugins can be limited in the number of tickets they can create per
	 * world.</p>
	 *
	 * @param plugin Plugin that wants to load chunks
	 * @param world  World that chunks will be loaded in
	 * @return The new LoadingTicket, or Optional.empty() if a ticket could not
	 * be created
	 */
	@Shadow
	public Optional<LoadingTicket> createTicket(Object plugin, World world) {
		return null;
	}

	/**
	 * Attempts to create a new loading ticket for a plugin to load chunks in a
	 * world.
	 * <p>
	 * <p>This version is to create tickets that are bound to the existence of
	 * an Entity. For instance, a ticket to load the chunks a minecart is
	 * travelling through.</p>
	 * <p>
	 * <p>Plugins can be limited in the number of tickets they can create per
	 * world.</p>
	 *
	 * @param plugin Plugin that wants to load chunks
	 * @param world  World that chunks will be loaded in
	 * @return The new LoadingTicket, or Optional.empty() if a ticket could not
	 * be created
	 */
	@Shadow
	public Optional<EntityLoadingTicket> createEntityTicket(Object plugin, World world) {
		return null;
	}

	/**
	 * Attempts to create a new loading ticket for a plugin to load chunks in a
	 * world. The returned ticket will be associated with the given player.
	 * <p>
	 * <p>.</p>
	 *
	 * @param plugin Plugin that wants to load chunks
	 * @param world  World that chunks will be loaded in
	 * @param player Player that chunks are being loaded for
	 * @return The new LoadingTicket, or Optional.empty() if a ticket could not
	 * be created
	 */
	@Shadow
	public Optional<PlayerLoadingTicket> createPlayerTicket(Object plugin, World world, UUID player) {
		return null;
	}

	/**
	 * Attempts to create a new loading ticket for a plugin to load chunks in a
	 * world. The returned ticket will be associated with the given player.
	 * <p>
	 * <p>This version is to create tickets that are bound to the existence of
	 * an Entity. For instance, a ticket to load the chunks a minecart is
	 * travelling through.</p>
	 * <p>
	 * <p>Plugins can be limited in the number of tickets they can create per
	 * world.</p>
	 *
	 * @param plugin Plugin that wants to load chunks
	 * @param world  World that chunks will be loaded in
	 * @param player Player that chunks are being loaded for
	 * @return The new LoadingTicket, or Optional.empty() if a ticket could not
	 * be created
	 */
	@Shadow
	public Optional<PlayerEntityLoadingTicket> createPlayerEntityTicket(Object plugin, World world, UUID player) {
		return null;
	}

	@Shadow
	public int getMaxTickets(Object plugin) {
		return 0;
	}

	/**
	 * Gets the amount of remaining tickets a plugin can have in the world
	 * before hitting the maximum.
	 *
	 * @param plugin The plugin to get the remaining available ticket count for
	 * @param world  The world to get the remaining count in
	 * @return The remaining tickets the plugin has available in the world
	 */
	@Shadow
	public int getAvailableTickets(Object plugin, World world) {
		return 0;
	}

	/**
	 * Gets the amount of tickets remaining available for a player.
	 *
	 * @param player The player to get the number of remaining tickets for
	 * @return The remaining tickets the player has available
	 */
	@Shadow
	public int getAvailableTickets(UUID player) {
		return 0;
	}

	/**
	 * Gets the set of currently force-loaded chunks in a world.
	 *
	 * @param world The world to get force-loaded chunks from
	 * @return The set of all force-loaded chunk coordinates and the tickets
	 * that are loading those chunks
	 */
	@Shadow
	public ImmutableSetMultimap<Vector3i, LoadingTicket> getForcedChunks(World world) {
		return null;
	}

	private class SpongeLoadingTicket implements ChunkTicketManager.LoadingTicket {
		private NBTTagCompound modData;

		protected ForgeChunkManager.Ticket forgeTicket;
		private PluginContainer plugin;
		private String pluginId;
		private ImmutableSet<Vector3i> chunkList;

		private SpongeLoadingTicket(ForgeChunkManager.Ticket ticket) {
			this.forgeTicket = ticket;
			this.plugin = SpongeImpl.getGame().getPluginManager().getPlugin(ticket.getModId()).get();
			this.pluginId = this.plugin.getId();
		}

		@Override
		public boolean setNumChunks(int numChunks) {
			if (numChunks > getMaxTickets(this.plugin) || (numChunks <= 0 && getMaxTickets(this.plugin) > 0)) {
				return false;
			}

			this.forgeTicket.setChunkListDepth(numChunks);
			return true;
		}

		@Override
		public int getNumChunks() {
			return this.forgeTicket.getChunkListDepth();
		}

		@Override
		public int getMaxNumChunks() {
			return this.forgeTicket.getMaxChunkListDepth();
		}

		@Override
		public String getPlugin() {
			return this.pluginId;
		}

		@Override
		public ImmutableSet<Vector3i> getChunkList() {
			if (this.chunkList != null) {
				return this.chunkList;
			}

			Set<Vector3i> forgeChunkList = new HashSet<>();
			for (ChunkPos chunkCoord : this.forgeTicket.getChunkList()) {
				forgeChunkList.add(new Vector3i(chunkCoord.chunkXPos, 0, chunkCoord.chunkZPos));
			}

			this.chunkList = new ImmutableSet.Builder<Vector3i>().addAll(forgeChunkList).build();
			return this.chunkList;
		}

		@Override
		public void forceChunk(Vector3i chunk) {
			ForgeChunkManager.forceChunk(this.forgeTicket, VecHelper.toChunkCoordIntPair(chunk));
		}

		@Override
		public void unforceChunk(Vector3i chunk) {
			ForgeChunkManager.unforceChunk(this.forgeTicket, VecHelper.toChunkCoordIntPair(chunk));
		}

		@Override
		public void prioritizeChunk(Vector3i chunk) {
			ForgeChunkManager.reorderChunk(this.forgeTicket, VecHelper.toChunkCoordIntPair(chunk));
		}

		@Override
		public void release() {
			ForgeChunkManager.releaseTicket(this.forgeTicket);
		}

		public NBTTagCompound getModData() {
			if (modData == null)
				modData = new NBTTagCompound();

			return modData;
		}
	}
}
