package devonthe.rocks.stickychunk.database;

import devonthe.rocks.stickychunk.StickyChunk;
import devonthe.rocks.stickychunk.chunkload.LoadedRegion;
import devonthe.rocks.stickychunk.world.Region;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.world.World;
import devonthe.rocks.stickychunk.world.Coordinate;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class SqliteDatabase implements IDatabase {
	// TODO:- Add config fields (location, dbName, tableName)

	Server server = StickyChunk.getInstance().getGame().getServer();
	Logger logger = StickyChunk.getInstance().getLogger();

	public SqliteDatabase() {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			logger.error("Unable to load the JDBC driver");
		}

		try (Statement statement = getConnection().createStatement()) {
			statement.setQueryTimeout(30);
			statement.executeUpdate(Schema.createChunkSchema());
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Unable to create StickyChunk database");
		}
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(String.format("jdbc:sqlite:%s%s%s.db"));
	}

	public void saveData(LoadedRegion loadedRegion) {
		String sql = String.format("INSERT OR REPLACE INTO chunks(%s) VALUES(?, ?, ?, ?, ?, ?)", Schema.getChunkProperties());

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, loadedRegion.getId().toString());
			statement.setString(2, loadedRegion.getOwner().toString());
			statement.setString(3, loadedRegion.getWorld().getUniqueId().toString());
			statement.setInt(4, loadedRegion.getRegion().getFrom().getX());
			statement.setInt(5, loadedRegion.getRegion().getFrom().getZ());
			statement.setInt(6, loadedRegion.getRegion().getTo().getX());
			statement.setInt(7, loadedRegion.getRegion().getTo().getZ());
			statement.setDate(8, loadedRegion.getEpoch());
		} catch(SQLException e) {
			e.printStackTrace();
			logger.error(String.format("Error inserting LoadedRegion into the database: %s", e.getMessage()));
		}
	}

	public void saveAllData(ArrayList<LoadedRegion> data) {
		for (LoadedRegion loadedRegion : StickyChunk.loadedRegions)
			saveData(loadedRegion);
	}

	public ArrayList<LoadedRegion> loadData() {
		ArrayList<LoadedRegion> chunks = new ArrayList<LoadedRegion>();

		try (Statement statement = getConnection().createStatement()) {
			ResultSet results = statement.executeQuery("SELECT * FROM chunks");

			while(results.next()) {
				UUID id = UUID.fromString(results.getString("id"));
				UUID owner = UUID.fromString(results.getString("owner"));
				UUID world = UUID.fromString(results.getString("world"));
				int fromX = results.getInt("fromX");
				int fromZ = results.getInt("fromZ");
				int toX = results.getInt("toX");
				int toZ = results.getInt("toZ");
				Date date = results.getDate("created");

				if (server.getWorld(world).isPresent()) {
					server.getWorld(world).ifPresent(loadedWorld ->
						chunks.add(new LoadedRegion(owner, id, loadedWorld, new Region(new Coordinate(fromX, fromZ), new Coordinate(toX, toZ)), date))
					);
				} else {
					logger.error("The world that a chunk was associated to no longer exists.");
					logger.debug(String.format("The world's unique ID is %s", world.toString()));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(String.format("Unable to load data from the table: %s", e.getMessage()));
		}

		return chunks;
	}
}
