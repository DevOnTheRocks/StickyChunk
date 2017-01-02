package uk.codepen.stickysponge.database;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import uk.codepen.stickysponge.Chunk;
import uk.codepen.stickysponge.Coordinate;
import uk.codepen.stickysponge.StickySponge;

import java.sql.Connection;
import java.sql.DriverManager;
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
			StickySponge.getInstance().getLogger().error("Unable to load the JDBC driver");
		}

		try (Statement statement = getConnection().createStatement()) {
			statement.setQueryTimeout(30);

			String table = String.format("CREATE TABLE IF NOT EXISTS %s (" +
					"owner		STRING PRIMARY KEY" +
					"id			STRING" +
					"world 		STRING" +
					"x			INT" +
					"z			INT" +
					"created	DATETIME" +
					")");

			statement.executeUpdate(table);
		} catch (SQLException e) {
			e.printStackTrace();
			StickySponge.getInstance().getLogger().error("Unable to create StickySponge database");
		}
	}

	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(String.format(""));
	}

	public void saveData(Chunk chunk) {

	}

	public void saveAllData(ArrayList<Chunk> data) {

	}

	public ArrayList<Chunk> loadData() {
		List chunks = new ArrayList<Chunk>();

		try (Statement statement = getConnection().createStatement()) {
			ResultSet results = statement.executeQuery(String.format("SELECT * FROM &s" /* , config.tableName */));

			while(results.next()) {
				UUID owner = UUID.fromString(results.getString("owner"));
				UUID id = UUID.fromString(results.getString("id"));
				UUID world = UUID.fromString(results.getString("world"));
				int x = results.getInt("x");
				int z = results.getInt("z");

				World worldOrDefault = StickySponge.getInstance().getGame().getServer().getWorld(world).orElseGet(() -> StickySponge.getInstance().getDefaultWorld());
				Location<World> location = new Location<World>(worldOrDefault, new Vector3i(x, 0, z));

				StickySponge.chunks.add(new Chunk(owner, id, world, new Coordinate(x, z)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			StickySponge.getInstance().getLogger().error(String.format("Unable to load data from the table: %s", e.getMessage()));
		}

		return null;
	}
}
