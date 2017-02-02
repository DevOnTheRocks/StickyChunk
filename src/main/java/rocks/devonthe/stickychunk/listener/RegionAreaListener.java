package rocks.devonthe.stickychunk.listener;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Maps;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import rocks.devonthe.stickychunk.permission.Permissions;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Cossacksman on 24/01/2017.
 */
public class RegionAreaListener {
	private static final Map<UUID, PlayerData> PLAYER_DATA = Maps.newHashMap();

	/***
	 * A method to listen to right click block interaction events
	 * @param event the event object
	 * @param player the player who fired it
	 */
	@Listener
	public void onInteract(InteractBlockEvent.Secondary.MainHand event, @Root Player player) {
		if (!player.hasPermission(Permissions.PLAYER_SELECT_REGION))
			return;

		Optional<ItemStack> item = player.getItemInHand(HandTypes.MAIN_HAND);
		if (item.isPresent() && item.get().getItem().equals(ItemTypes.GOLDEN_AXE) && event.getTargetBlock() != BlockSnapshot.NONE) {
			get(player).setPos2(event.getTargetBlock().getPosition());
			player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "Position 2 set to " + event.getTargetBlock().getPosition()));
			event.setCancelled(true);
		}
	}

	/***
	 * A method to listen to the left click block interaction events
	 * @param event the event oject
	 * @param player the player who fired it
	 */
	@Listener
	public void onInteract(InteractBlockEvent.Primary.MainHand event, @Root Player player) {
		if (!player.hasPermission(Permissions.PLAYER_SELECT_REGION))
			return;

		Optional<ItemStack> item = player.getItemInHand(HandTypes.MAIN_HAND);
		if (item.isPresent() && item.get().getItem().equals(ItemTypes.GOLDEN_AXE)) {
			get(player).setPos1(event.getTargetBlock().getPosition());
			player.sendMessage(Text.of(TextColors.LIGHT_PURPLE, "Position 1 set to " + event.getTargetBlock().getPosition()));
			event.setCancelled(true);
		}
	}

	public static class PlayerData {

		private final UUID uid;
		private Vector3i pos1;
		private Vector3i pos2;

		public PlayerData(UUID uid) {
			this.uid = uid;
		}

		/***
		 * Get the unique identifier of the player this data associates with
		 * @return a UUID of the player
		 */
		public UUID getUid() {
			return this.uid;
		}

		/***
		 * Get the Vector3i of the position set with the left hand
		 * @return a Vector3i position
		 */
		public Vector3i getPos1() {
			return this.pos1;
		}

		/***
		 * Set the position created with the left hand
		 * @param pos the position the left hand clicked on
		 */
		public void setPos1(Vector3i pos) {
			this.pos1 = pos;
		}

		/***
		 * Get the Vector3i of the position set with the right hand
		 * @return a Vector3i position
		 */
		public Vector3i getPos2() {
			return this.pos2;
		}

		/***
		 * Set the position created with the right hand
		 * @param pos the position the right hand clicked on
		 */
		public void setPos2(Vector3i pos) {
			this.pos2 = pos;
		}
	}

	/***
	 * Check whether the player has data in the map
	 * @param player the player to check the map for
	 * @return whether the player has data in the map
	 */
	public static boolean exists(Player player) {
		return PLAYER_DATA.containsKey(player.getUniqueId());
	}

	/***
	 * Get player data from the map or create an entry if none exists
	 * @param player the player to get/set with
	 * @return a PlayerData instance associated with the player
	 */
	public static PlayerData get(Player player) {
		return PLAYER_DATA.computeIfAbsent(player.getUniqueId(), k -> new PlayerData(player.getUniqueId()));
	}
}
