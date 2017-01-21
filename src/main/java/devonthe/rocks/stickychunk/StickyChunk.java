package devonthe.rocks.stickychunk;

import com.google.inject.Inject;
import devonthe.rocks.stickychunk.chunkload.ChunkLoadCallback;
import devonthe.rocks.stickychunk.chunkload.LoadedRegion;
import devonthe.rocks.stickychunk.database.SqliteDatabase;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.world.World;
import devonthe.rocks.stickychunk.config.StickySpongeConfig;
import devonthe.rocks.stickychunk.database.IDatabase;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by Cossacksman on 02/01/2017.
 */

@Plugin(
		id = "stickychunk",
		name = "StickyChunk",
		version = "0.1.0",
		description = "",
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

	private StickySpongeConfig config;
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

	public StickySpongeConfig getConfig() {
		return config;
	}

	@Listener
	public void onServerStarted(GameStartedServerEvent event) {
		instance = this;
		config = new StickySpongeConfig();
		database = new SqliteDatabase();

		// Register tickets
		getGame().getServer().getChunkTicketManager().registerCallback(this, new ChunkLoadCallback());

		// Register commands
	}

	@Listener
	public void onServerStopped(GameStoppedServerEvent event) {
		// Update the database
	}

	public World getDefaultWorld() {
		String defaultWorldName = StickyChunk.getInstance().getGame().getServer().getDefaultWorldName();
		return StickyChunk.getInstance().getGame().getServer().getWorld(defaultWorldName).get();
	}
}
