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

package rocks.devonthe.stickychunk;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.world.World;
import rocks.devonthe.stickychunk.chunkload.ChunkLoadCallback;
import rocks.devonthe.stickychunk.chunkload.ChunkLoadType;
import rocks.devonthe.stickychunk.chunkload.TicketManager;
import rocks.devonthe.stickychunk.command.CommandLoad;
import rocks.devonthe.stickychunk.command.CommandPersist;
import rocks.devonthe.stickychunk.command.CommandUnload;
import rocks.devonthe.stickychunk.config.ConfigManager;
import rocks.devonthe.stickychunk.config.StickyChunkConfig;
import rocks.devonthe.stickychunk.data.DataStore;
import rocks.devonthe.stickychunk.database.IDatabase;
import rocks.devonthe.stickychunk.database.SqliteDatabase;
import rocks.devonthe.stickychunk.economy.EconomyManager;
import rocks.devonthe.stickychunk.listener.PlayerConnectionListener;
import rocks.devonthe.stickychunk.listener.RegionAreaListener;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Optional;

import static rocks.devonthe.stickychunk.StickyChunk.*;

@Plugin(
	id = PLUGIN_ID,
	name = NAME,
	version = VERSION,
	description = DESCRIPTION,
	authors = {AUTHORS}
)
public class StickyChunk {
	public static final String PLUGIN_ID = "stickychunk";
	public static final String NAME = "@NAME@";
	public static final String DESCRIPTION = "@DESCRIPTION@";
	public static final String VERSION = "@VERSION@";
	public static final String AUTHORS = "cossacksman, Mohron";

	@Inject
	private Logger logger;
	@Inject
	private Game game;
	@Inject
	private PluginContainer pluginContainer;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDirectory;
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;
	private ConfigManager pluginConfigManager;
	private StickyChunkConfig config;

	private TicketManager ticketManager;
	private EconomyManager economyManager;
	private UserStorageService userStorageService;

	private DataStore dataStore;
	private IDatabase database;

	EnumMap<ChunkLoadType, String> chunkLoaders = Maps.newEnumMap(ChunkLoadType.class);

	private static StickyChunk instance;
	private static boolean enabled;

	@Listener
	public void onPostInitialization(GamePostInitializationEvent event) {
		instance = this;
		enabled = validateSpongeVersion();
	}

	@Listener
	public void onAboutToStart(GameAboutToStartServerEvent event) {
		if (!enabled)
			return;

		// Initialize configs
		config = new StickyChunkConfig();
		pluginConfigManager = new ConfigManager(configManager);
		pluginConfigManager.save();

		database = new SqliteDatabase();
		dataStore = new DataStore();
		ticketManager = new TicketManager();

		dataStore.addPlayerRegions(database.loadRegionData());
		dataStore.addUsers(database.loadUserData());

		logger.info("registering callback");

		// Register callbacks
		Sponge.getServer().getChunkTicketManager().registerCallback(this, new ChunkLoadCallback());

		// Register events
		Sponge.getGame().getEventManager().registerListeners(this, new PlayerConnectionListener());
		Sponge.getGame().getEventManager().registerListeners(this, new RegionAreaListener());
	}

	@Listener
	public void onServerStarting(GameStartingServerEvent event) {

	}

	@Listener
	public void onServerStarted(GameStartedServerEvent event) {
		if (!enabled)
			return;

		// Register commands
		registerCommands();

		// Validate LoadedRegions
		ticketManager.validateLoadedRegions();
	}

	@Listener
	public void onServerStopped(GameStoppedServerEvent event) {
		if (!enabled)
			return;

		database.saveRegionData(dataStore.getCollatedRegions());
		database.saveUserData(dataStore.getLoadedUsers());
	}

	@Listener
	public void onServiceChanged(ChangeServiceProviderEvent event) {
		if (!enabled)
			return;

		if (event.getService().equals(EconomyService.class))
			economyManager = new EconomyManager((EconomyService) event.getNewProviderRegistration().getProvider());

		if (event.getService().equals(UserStorageService.class))
			userStorageService = (UserStorageService) event.getNewProviderRegistration().getProvider();
	}

	public DataStore getDataStore() {
		return dataStore;
	}

	public World getDefaultWorld() {
		String defaultWorldName = StickyChunk.getInstance().getGame().getServer().getDefaultWorldName();
		return StickyChunk.getInstance().getGame().getServer().getWorld(defaultWorldName).get();
	}

	private void registerCommands() {
		CommandPersist.register();
		CommandLoad.register();
		CommandUnload.register();
	}

	private boolean validateSpongeVersion() {
		PluginContainer container = Sponge.getPlatform().getContainer(Platform.Component.IMPLEMENTATION);
		if (container.getName().equals("SpongeForge")) {
			try {
				String version = container.getVersion().orElseThrow(Exception::new);
				version = version.substring(Math.max(version.length() - 4, 0));
				int spongeVersion = Integer.parseInt(version);
				if (spongeVersion < 2132) {
					this.logger.error(String.format(
						"Failed to initialize StickyChunk due to outdated SpongeForge (%s). StickyChunk requires SF 2132+",
						spongeVersion
					));
					return false;
				}
			} catch (Exception ignored) {
			}
		}
		return true;
	}

	public static StickyChunk getInstance() {
		return instance;
	}

	public Cause getCause() {
		return Cause.source(pluginContainer).build();
	}

	public Logger getLogger() {
		return logger;
	}

	public Game getGame() {
		return game;
	}

	public Path getConfigDirectory() {
		return configDirectory;
	}

	public StickyChunkConfig getConfig() {
		return config;
	}

	public IDatabase getDatabase() {
		return database;
	}

	public TicketManager getTicketManager() {
		return ticketManager;
	}

	public Optional<EconomyManager> getEconomyManager() {
		return Optional.ofNullable(economyManager);
	}

	public UserStorageService getUserStorageService() {
		return userStorageService;
	}
}
