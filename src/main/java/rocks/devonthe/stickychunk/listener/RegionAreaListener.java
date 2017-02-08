/*
 * This file is part of StickyChunk by DevOnTheRocks, licened under GPL-3.0
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
 *
 * Created by Cossacksman on 24/01/2017.
 */
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
		if (item.isPresent() && item.get().getItem().equals(ItemTypes.BLAZE_ROD) && event.getTargetBlock() != BlockSnapshot.NONE) {
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
		if (item.isPresent() && item.get().getItem().equals(ItemTypes.BLAZE_ROD)) {
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
