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

package rocks.devonthe.stickychunk.chunkload.chunkloader;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.config.chunkloader.BlockChunkLoaderConfig;
import rocks.devonthe.stickychunk.config.chunkloader.ChunkLoaderType;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BlockChunkLoader extends ChunkLoader {

	private String config;
	private Vector3i location;
	private Inventory inventory;
	private Task animation;
	private int range;
	private Duration fuel = Duration.ZERO;

	public BlockChunkLoader(User owner, String config, Location<World> location) {
		super(ChunkLoaderType.BLOCK, location.getExtent(), owner);
		this.config = config;
		this.location = location.getBlockPosition();
		this.inventory = Inventory.builder()
			.of(InventoryArchetypes.HOPPER)
			.property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(Text.of("Chunkloader")))
			.listener(ClickInventoryEvent.class, changeInventory())
			.build(StickyChunk.getInstance());

		this.animation = Sponge.getScheduler().createTaskBuilder()
			.intervalTicks(100)
			.execute(animateChunkLoader(location.getExtent(), location.getPosition()))
			.submit(StickyChunk.getInstance());
		this.range = 3;
	}

	public BlockChunkLoaderConfig getConfig() {
		return StickyChunk.getInstance().getConfig().getChunkLoaders().getBlockBased().stream()
			.filter(c -> c.getName().equalsIgnoreCase(config))
			.findAny()
			.get();
		//.orElseThrow(() -> new TextMessageException(Text.of(TextColors.RED, "Unable to locate chunk loader config!")));
	}

	public Location<World> getLocation() {
		return new Location<>(getWorld().get(), location);
	}

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public Duration getFuel() {
		return fuel;
	}

	public void setFuel(Duration fuel) {
		this.fuel = fuel;
	}

	public void dropInventory(Cause cause) {
		inventory.slots().forEach(slot -> slot.poll().ifPresent(item -> {
			Entity entity = getWorld().get().createEntity(EntityTypes.ITEM, location);
			entity.offer(Keys.REPRESENTED_ITEM, item.createSnapshot());
			getWorld().get().spawnEntity(entity, cause);
		}));
	}

	public void sendInfoMessage(Player player) {
		getInfoPage().sendTo(player);
	}

	private PaginationList getInfoPage() {
		final List<Text> info = Lists.newArrayList();
		info.add(getEnabledText());

		info.add(Text.of(
			TextColors.DARK_GRAY, "Owner",
			TextColors.WHITE, " : ",
			TextColors.GOLD, (getOwner().isPresent()) ? getOwner().get().getName() : "Somebody"
		));
		info.add(Text.of(
			TextColors.DARK_GRAY, "Range",
			TextColors.WHITE, " : ",
			TextColors.DARK_PURPLE, range, TextColors.GRAY, "x", TextColors.DARK_PURPLE, range
		));
		info.add(Text.of(
			TextColors.DARK_GRAY, "Fuel",
			TextColors.WHITE, " : ",
			(getConfig().getFuelMode() != ChunkLoaderFuelMode.NONE)
				? Text.of(
					TextColors.YELLOW, getConfig().getFuelMode().toText().toBuilder()
						.onHover(TextActions.showText(Text.of("Click to view detailed fuel information.")))
						.onClick(TextActions.executeCallback(src -> getFuelPage().sendTo(src))), " ",
					TextColors.DARK_PURPLE, fuel.toString().replace("P", "").replace("T", "")
				)
				: Text.of(TextColors.GRAY, getConfig().getFuelMode().toText())

		));
		return PaginationList.builder()
			.title(Text.of(TextColors.YELLOW, "Chunkloader"))
			.padding(Text.of(TextStyles.STRIKETHROUGH, "-"))
			.contents(info)
			.build();
	}

	private PaginationList getFuelPage() {
		final List<Text> textList = Lists.newArrayList();
		textList.add(Text.of(
			TextColors.DARK_GRAY, "Fuel Mode",
			TextColors.WHITE, " : ",
			TextColors.YELLOW, getConfig().getFuelMode()
		));
		textList.add(Text.of(
			TextColors.DARK_GRAY, "Remaining Fuel",
			TextColors.WHITE, " : ",
			TextColors.DARK_PURPLE, fuel.toString().replace("P", "").replace("T", "")
		));
		textList.add(Text.of(
			TextColors.DARK_GRAY, "Supported Fuel",
			TextColors.WHITE, " : "
		));
		getConfig().getFuels()
			.forEach(fuel -> textList.add(Text.of(
				TextColors.WHITE, " - ",
				TextColors.BLUE, Text.builder(fuel.getItemType()).onHover(TextActions.showItem(fuel.getItemType().getTemplate())),
				TextColors.WHITE, " : ",
				TextColors.DARK_PURPLE, fuel.getDurationText()
			)));
		textList.add(Text.of(
			Text.NEW_LINE,
			Text.builder(" << BACK").onClick(TextActions.executeCallback(src -> getInfoPage().sendTo(src)))
		));
		return PaginationList.builder()
			.title(Text.of(TextColors.YELLOW, "Fuel Info"))
			.padding(Text.of(TextStyles.STRIKETHROUGH, "-"))
			.contents(textList)
			.build();
	}

	public void remove() {
		animation.cancel();
	}

	private Consumer<ClickInventoryEvent> changeInventory() {
		return event -> {
			event.getTransactions().stream()
				.filter(t -> t.getSlot().parent().getArchetype().equals(InventoryArchetypes.HOPPER))
				.filter(t -> t.getFinal().getType() != ItemTypes.NONE || t.getFinal().getCount() > t.getOriginal().getCount())
				.filter(t -> !getConfig().getFuels().stream().map(ChunkLoaderFuel::getItemType).collect(Collectors.toList()).contains(t.getFinal().getType()))
				.forEach(t -> event.setCancelled(true));
			StickyChunk.getInstance().getLogger().info("Saved chunkloader inventory!");
		};
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
					.delayTicks(i * 2)
					.execute(circleAnimation(world, position, r))
					.submit(StickyChunk.getInstance());
			}
		};
	}

	private Consumer<Task> circleAnimation(World world, Vector3d position, int r) {
		return task -> world.spawnParticles(
			ParticleEffect.builder().type(ParticleTypes.REDSTONE_DUST).build(),
			position.add(0.5 + 1.25 * Math.cos(r), 0.5, 0.5 + 1.25 * Math.sin(r))
		);
	}
}
