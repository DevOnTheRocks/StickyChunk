package uk.codepen.stickysponge.database;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import uk.codepen.stickysponge.Chunk;
import uk.codepen.stickysponge.Coordinate;
import uk.codepen.stickysponge.StickySponge;

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
		return DriverManager.getConnection(String.format("jdbc:sqlite:%s%s%s.db"));
	}

	public void saveData(Chunk chunk) {
		String sql = String.format("INSERT OR REPLACE INTO %s(owner, id, world, x, z, created) VALUES(?, ?, ?, ?, ?, ?)");

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, chunk.getOwner().toString());
			statement.setString(2, chunk.getId().toString());
			statement.setString(3, chunk.getWorld().getUniqueId().toString());
			statement.setInt(4, chunk.getX());
			statement.setInt(5, chunk.getZ());
			statement.setDate(6, chunk.getEpoch());
		} catch(SQLException e) {
			e.printStackTrace();
			StickySponge.getInstance().getLogger().error(String.format("Error inserting Chunk into the database: %s", e.getMessage()));
		}
	}

	public void saveAllData(ArrayList<Chunk> data) {
		for (Chunk chunk : StickySponge.chunks) {
			String sql = String.format("INSERT OR REPLACE INTO %s(owner, id, world, x, z, created) VALUES(?, ?, ?, ?, ?, ?)" /* config.tableName*/);

			try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
				statement.setString(1, chunk.getOwner().toString());
				statement.setString(2, chunk.getId().toString());
				statement.setString(3, chunk.getWorld().getUniqueId().toString());
				statement.setInt(4, chunk.getX());
				statement.setInt(5, chunk.getZ());
				statement.setDate(6, chunk.getEpoch());

				statement.execute();
			} catch (SQLException e) {
				StickySponge.getInstance().getLogger().error(String.format("Error updating the database: %s", e.getMessage()));
			}
		}
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
				Date date = results.getDate("created");

				World worldOrDefault = StickySponge.getInstance().getGame().getServer().getWorld(world).orElseGet(() -> StickySponge.getInstance().getDefaultWorld());
				Location<World> location = new Location<>(worldOrDefault, new Vector3i(x, 0, z));

				StickySponge.chunks.add(new Chunk(owner, id, world, new Coordinate(x, z), date));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			StickySponge.getInstance().getLogger().error(String.format("Unable to load data from the table: %s", e.getMessage()));
		}

		return null;
	}
}
