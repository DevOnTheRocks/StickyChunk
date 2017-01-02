package uk.codepen.stickysponge;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.world.World;
import uk.codepen.stickysponge.config.StickySpongeConfig;
import uk.codepen.stickysponge.database.IDatabase;
import uk.codepen.stickysponge.database.SqliteDatabase;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by Cossacksman on 02/01/2017.
 */

@Plugin(
		id = "stickysponge",
		name = "StickySponge",
		version = "0.1.0",
		description = "",
		authors = {"cossacksman"}
)
public class StickySponge {
	@Inject
	private Logger logger;
	@Inject
	private Game game;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDirectory;

	private StickySpongeConfig config;
	private IDatabase database;

	private static StickySponge instance;

	public static ArrayList<Chunk> chunks = new ArrayList<>();

	public static StickySponge getInstance() {
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

		// Register commands
	}

	@Listener
	public void onServerStopped(GameStoppedServerEvent event) {
		// Update the database
	}

	public World getDefaultWorld() {
		String defaultWorldName = StickySponge.getInstance().getGame().getServer().getDefaultWorldName();
		return StickySponge.getInstance().getGame().getServer().getWorld(defaultWorldName).get();
	}
}
