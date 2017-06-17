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

package rocks.devonthe.stickychunk.listener;

import com.google.common.collect.Sets;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.chunkloader.BlockChunkLoader;
import rocks.devonthe.stickychunk.chunkload.chunkloader.ChunkLoaderFuelMode;
import rocks.devonthe.stickychunk.config.chunkloader.BlockChunkLoaderConfig;
import rocks.devonthe.stickychunk.data.DataStore;
import rocks.devonthe.stickychunk.database.SQLiteEntityManager;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ChunkLoaderListener extends ListenerBase {
	static private Set<BlockChunkLoader> blockChunkLoaders = Sets.newHashSet();
	private SQLiteEntityManager database = StickyChunk.getInstance().getEntityManager();
	private DataStore dataStore = StickyChunk.getInstance().getDataStore();

	@Listener
	public void onBlockChange(ChangeBlockEvent.Break event) {
		event.getTransactions().forEach(t ->
			blockChunkLoaders.stream()
				.filter(cl -> cl.getLocation().equals(t.getOriginal().getLocation().get()))
				.findAny()
				.ifPresent(chunkLoader -> {
					chunkLoader.dropInventory(event.getCause());
					chunkLoader.remove();
					blockChunkLoaders.remove(chunkLoader);
				}));
	}

	@Listener
	public void onInteract(InteractBlockEvent event, @Root Player player) {
		List<BlockChunkLoaderConfig> chunkloaders = instance.getConfig().getChunkLoaders().getBlockBased().stream()
			.filter(cl -> cl.getBlockId().equals(event.getTargetBlock().getState().getType()))
			.collect(Collectors.toList());

		if (chunkloaders.isEmpty()) {
			return;
		}
		if (event instanceof InteractBlockEvent.Primary) {
			onLeftClick((InteractBlockEvent.Primary) event, player);
		} else if (event instanceof InteractBlockEvent.Secondary) {
			onRightClick((InteractBlockEvent.Secondary) event, chunkloaders, player);
		}
	}

	private void onLeftClick(InteractBlockEvent.Primary event, Player player) {
		blockChunkLoaders.stream()
			.filter(cl -> cl.getLocation().equals(event.getTargetBlock().getLocation().get()))
			.findAny()
			.ifPresent(cl -> cl.sendInfoMessage(player));
	}

	private void onRightClick(InteractBlockEvent.Secondary event, List<BlockChunkLoaderConfig> chunkLoaders, Player player) {
		Optional<BlockChunkLoader> oChunkLoader = blockChunkLoaders.stream()
			.filter(cl -> !cl.getConfig().getFuelMode().equals(ChunkLoaderFuelMode.NONE))
			.filter(cl -> cl.getLocation().equals(event.getTargetBlock().getLocation().get()))
			.findAny();
		if (oChunkLoader.isPresent()) {
			player.openInventory(oChunkLoader.get().getInventory(), instance.getCause());
		} else {
			if (!player.get(Keys.IS_SNEAKING).orElse(false)
				|| !player.getItemInHand(event.getHandType()).isPresent()
				|| blockChunkLoaders.stream().filter(cl -> cl.getLocation().equals(event.getTargetBlock().getLocation().get())).count() > 1) {
				return;
			}

			ItemStack item = player.getItemInHand(event.getHandType()).get();
			chunkLoaders.stream()
				.filter(c -> c.getItemId().getType().equals(item.getItem()))
				.findAny()
				.ifPresent(cl -> {
					Location<World> location = event.getTargetBlock().getLocation().get();

					logger.info(String.format(
						"%s attempted to make a chunkloader in %s at %s",
						player.getName(),
						location.getExtent().getName(),
						location.getBlockPosition().toString()
					));

					event.setCancelled(true);

					if (!player.getGameModeData().get(Keys.GAME_MODE).get().equals(GameModes.CREATIVE)) {
						item.setQuantity(item.getQuantity() - 1);
						player.setItemInHand(event.getHandType(), (item.getQuantity() > 0) ? item : null);
					}

					//if (dataStore.isChunkLoaded(world.getChunk(blockPos.getX(), blockPos.getY(), blockPos.getZ()).get())) {
					//	player.sendMessage(Text.of(TextColors.RED, "This chunk is already loaded."));
					//	return;
					//}

					//dataStore.addUserChunkLoader(player, ChunkLoaderType.BLOCK, new BlockChunkLoader(event.getTargetBlock().getLocation().get()));

					blockChunkLoaders.add(new BlockChunkLoader(player, cl.getName(), location));

					BlockState block = BlockState.builder()
						.blockType(event.getTargetBlock().getState().getType())
						.build();

					player.getWorld().setBlock(location.getBlockPosition(), block, BlockChangeFlag.NONE, instance.getCause());

					location.getTileEntity().ifPresent(tile -> {
						tile.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "Chunkloader"));
					});

					player.sendMessage(Text.of(TextColors.GREEN, "Successfully created chunkloader."));
				});
		}
	}
}
