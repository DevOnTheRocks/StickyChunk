package devonthe.rocks.stickychunk.database;

import devonthe.rocks.stickychunk.StickyChunk;
import devonthe.rocks.stickychunk.chunkload.LoadedRegion;
import devonthe.rocks.stickychunk.world.Region;
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

	public SqliteDatabase() {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			StickyChunk.getInstance().getLogger().error("Unable to load the JDBC driver");
		}

		try (Statement statement = getConnection().createStatement()) {
			statement.setQueryTimeout(30);

			String table = "CREATE TABLE IF NOT EXISTS chunks (" +
					"owner		STRING PRIMARY KEY" +
					"id			STRING" +
					"world 		STRING" +
					"fromX		INT" +
					"fromZ		INT" +
					"toX		INT" +
					"toZ		INT" +
					"z			INT" +
					"created	DATETIME" +
					")";

			statement.executeUpdate(table);
		} catch (SQLException e) {
			e.printStackTrace();
			StickyChunk.getInstance().getLogger().error("Unable to create StickyChunk database");
		}
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(String.format("jdbc:sqlite:%s%s%s.db"));
	}

	public void saveData(LoadedRegion loadedRegion) {
		String sql = "INSERT OR REPLACE INTO chunks(owner, id, world, fromX, fromZ, toX, toZ, created) VALUES(?, ?, ?, ?, ?, ?)";

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, loadedRegion.getOwner().toString());
			statement.setString(2, loadedRegion.getId().toString());
			statement.setString(3, loadedRegion.getWorld().getUniqueId().toString());
			statement.setInt(4, loadedRegion.getRegion().getFrom().getX());
			statement.setInt(5, loadedRegion.getRegion().getFrom().getZ());
			statement.setInt(6, loadedRegion.getRegion().getTo().getX());
			statement.setInt(7, loadedRegion.getRegion().getTo().getZ());
			statement.setDate(8, loadedRegion.getEpoch());
		} catch(SQLException e) {
			e.printStackTrace();
			StickyChunk.getInstance().getLogger().error(String.format("Error inserting LoadedRegion into the database: %s", e.getMessage()));
		}
	}

	public void saveAllData(ArrayList<LoadedRegion> data) {
		for (LoadedRegion loadedRegion : StickyChunk.loadedRegions)
			saveData(loadedRegion);
	}

	public ArrayList<LoadedRegion> loadData() {
		List chunks = new ArrayList<LoadedRegion>();

		try (Statement statement = getConnection().createStatement()) {
			ResultSet results = statement.executeQuery("SELECT * FROM chunks");

			while(results.next()) {
				UUID owner = UUID.fromString(results.getString("owner"));
				UUID id = UUID.fromString(results.getString("id"));
				UUID world = UUID.fromString(results.getString("world"));
				int fromX = results.getInt("fromX");
				int fromZ = results.getInt("fromZ");
				int toX = results.getInt("toX");
				int toZ = results.getInt("toZ");
				Date date = results.getDate("created");

				World worldOrDefault = StickyChunk.getInstance().getGame().getServer().getWorld(world).orElseGet(() -> StickyChunk.getInstance().getDefaultWorld());

				StickyChunk.loadedRegions.add(new LoadedRegion(owner, id, world, new Region(new Coordinate(fromX, fromZ), new Coordinate(toX, toZ)), date));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			StickyChunk.getInstance().getLogger().error(String.format("Unable to load data from the table: %s", e.getMessage()));
		}

		return null;
	}
}
