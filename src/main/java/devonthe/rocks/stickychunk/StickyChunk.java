package devonthe.rocks.stickychunk;

import com.google.inject.Inject;
import devonthe.rocks.stickychunk.chunkload.ChunkLoadCallback;
import devonthe.rocks.stickychunk.chunkload.LoadedRegion;
import devonthe.rocks.stickychunk.command.CommandCreateOne;
import devonthe.rocks.stickychunk.config.ConfigManager;
import devonthe.rocks.stickychunk.data.DataStore;
import devonthe.rocks.stickychunk.database.SqliteDatabase;
import devonthe.rocks.stickychunk.world.RegionAreaListener;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.world.World;
import devonthe.rocks.stickychunk.config.StickyChunkConfig;
import devonthe.rocks.stickychunk.database.IDatabase;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by Cossacksman on 02/01/2017.
 */

@Plugin(
		id = "stickychunk",
		name = "StickyChunk",
		version = "0.3.2",
		description = "A chunk persistence plugin for keeping your entities and blocks loaded.",
		authors = {"cossacksman"}
)
public class StickyChunk {
	@Inject
	private Logger logger;
	@Inject
	private Game game;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDirectory;
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configManager;
	private ConfigManager pluginConfigManager;
	private StickyChunkConfig config;

	private DataStore dataStore;
	private IDatabase database;

	private static StickyChunk instance;

	public static ArrayList<LoadedRegion> loadedRegions = new ArrayList<>();

	public static StickyChunk getInstance() {
		return instance;
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

	@Listener
	public void onPostInitialization(GamePostInitializationEvent event) {
		instance = this;
	}

	@Listener
	public void onAboutToStart(GameAboutToStartServerEvent event) {
		// Initialize configs
		config = new StickyChunkConfig();
		pluginConfigManager = new ConfigManager(configManager);
		pluginConfigManager.save();

		// Register events
		Sponge.getGame().getEventManager().registerListeners(this, new RegionAreaListener());

		// Register commands
		registerCommands();
	}

	@Listener
	public void onServerStarted(GameStartedServerEvent event) {
		dataStore = new DataStore();
		database = new SqliteDatabase();

		// Register tickets
		game.getServer().getChunkTicketManager().registerCallback(this, new ChunkLoadCallback());
	}

	@Listener
	public void onServerStopped(GameStoppedServerEvent event) {
		database.saveRegionData(dataStore.getCollatedRegions());
	}

	public DataStore getDataStore() {
		return dataStore;
	}

	public World getDefaultWorld() {
		String defaultWorldName = StickyChunk.getInstance().getGame().getServer().getDefaultWorldName();
		return StickyChunk.getInstance().getGame().getServer().getWorld(defaultWorldName).get();
	}

	private void registerCommands() {
		CommandCreateOne.register();
	}
}
