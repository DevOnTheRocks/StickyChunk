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

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.BlockChangeFlag;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.chunkloader.BlockChunkLoader;
import rocks.devonthe.stickychunk.config.chunkloader.ChunkLoaderType;
import rocks.devonthe.stickychunk.data.DataStore;
import rocks.devonthe.stickychunk.database.IDatabase;

import java.util.function.Consumer;

public class ChunkLoaderListener {
	private IDatabase database = StickyChunk.getInstance().getDatabase();
	private DataStore dataStore = StickyChunk.getInstance().getDataStore();

	@Listener
	public void onBlockChange(ChangeBlockEvent.Break event, @Root Player player) {
		if (event.getTransactions().stream()
			.filter(t -> t.getOriginal().getState().getType().equals(BlockTypes.IRON_BLOCK))
			.count() == 0)
			return;

		StickyChunk.getInstance().getLogger().info(String
			.format("%s attempted to break a chunkloader in %s at %s", player.getName(),
				player.getLocation().getExtent().getName(), player.getLocation().getPosition().toString()
			));

		event.setCancelled(true);
	}

	@Listener
	public void onInteract(InteractBlockEvent.Primary event, @Root Player player) {
		if (!event.getTargetBlock().getState().getType().equals(BlockTypes.IRON_BLOCK)
			|| !player.getItemInHand(event.getHandType()).isPresent()
			|| !player.getItemInHand(event.getHandType()).get().getItem().equals(ItemTypes.ENDER_EYE))
			return;

		StickyChunk.getInstance().getLogger().info(String
			.format("%s attempted to make a chunkloader in %s at %s", player.getName(),
				player.getLocation().getExtent().getName(), player.getLocation().getPosition().toString()
			));

		event.setCancelled(true);

		World world = player.getWorld();
		Vector3i blockPos = event.getTargetBlock().getPosition();

		ItemStack item = player.getItemInHand(event.getHandType()).get();
		item.setQuantity(item.getQuantity() - 1);

		if (dataStore.isChunkLoaded(world.getChunk(blockPos.getX(),blockPos.getY(), blockPos.getZ()).get())) {
			player.sendMessage(Text.of(TextColors.RED, "This chunk is already loaded."));
			return;
		}

		dataStore.addUserChunkLoader(player, ChunkLoaderType.BLOCK, new BlockChunkLoader(event.getTargetBlock().getLocation().get()));

		player.setItemInHand(event.getHandType(), (item.getQuantity() > 0) ? item : null);

		Sponge.getScheduler().createTaskBuilder()
			.intervalTicks(100)
			.execute(animateChunkLoader(world, event.getTargetBlock().getLocation().get().getPosition()))
			.submit(StickyChunk.getInstance());

		BlockState block = BlockState.builder()
			.blockType(event.getTargetBlock().getState().getType())
			.add(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "Chunk Loader"))
			.build();

		player.getWorld().setBlock(event.getTargetBlock().getPosition(), block, BlockChangeFlag.NONE,
			StickyChunk.getInstance().getCause()
		);

		player.sendMessage(Text.of(TextColors.GREEN, "Successfully created chunkloader."));
	}

	private Consumer<Task> animateChunkLoader(World world, Vector3d position) {
		return task -> {
			// Top Effect
			world.spawnParticles(
				ParticleEffect.builder().type(ParticleTypes.END_ROD).option(ParticleOptions.QUANTITY, 5).build(),
				position.add(0.5, 1.5, 0.5)
			);
			// Circle Effect
			for (int i = 0, r = 0; r < 360; i++, r += 15) {
				Sponge.getScheduler().createTaskBuilder()
					.delayTicks(i)
					.execute(circleAnimation(world, position, r))
					.submit(StickyChunk.getInstance());
			}
		};
	}

	private Consumer<Task> circleAnimation(World world, Vector3d position, int r) {
		return task -> {
			// Upper
			world.spawnParticles(
				ParticleEffect.builder().type(ParticleTypes.HAPPY_VILLAGER)/*.option(ParticleOptions.COLOR, Color.WHITE)*/
					.build(),
				position.add(0.5 + 0.5 * Math.cos(r), 1.5, 0.5 + 0.5 * Math.sin(r))
			);
			// Lower
			world.spawnParticles(
				ParticleEffect.builder().type(ParticleTypes.REDSTONE_DUST).build(),
				position.add(0.5 + 1.25 * Math.cos(r), 0.5, 0.5 + 1.25 * Math.sin(r))
			);
		};
	}
}
