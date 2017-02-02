package rocks.devonthe.stickychunk.database;

import com.google.common.collect.ImmutableSet;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.data.User;
import rocks.devonthe.stickychunk.world.Coordinate;
import rocks.devonthe.stickychunk.world.Region;
import org.slf4j.Logger;
import org.spongepowered.api.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Date;
import java.util.UUID;

/**
 * Created by Cossacksman on 22/01/2017.
 */
public abstract class SqlDatabase implements IDatabase {
	private Server server = StickyChunk.getInstance().getGame().getServer();
	private Logger logger = StickyChunk.getInstance().getLogger();

	public abstract Connection getConnection() throws SQLException;

	public ArrayList<LoadedRegion> loadRegionData() {
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
							chunks.add(new LoadedRegion(owner, id, new Region(new Coordinate(fromX, fromZ), new Coordinate(toX, toZ), loadedWorld), date))
					);
				} else {
					logger.error("The world that a chunk was associated to no longer exists.");
					logger.debug(String.format("The world's unique ID is %s", world.toString()));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(String.format("Unable to load loadedRegion from the table: %s", e.getMessage()));
		}

		return chunks;
	}

	public ArrayList<User> loadUserData() {
		ArrayList<User> users = new ArrayList<User>();

		try (Statement statement = getConnection().createStatement()) {
			ResultSet results = statement.executeQuery("SELECT * FROM users");

			while(results.next()) {
				UUID player = UUID.fromString(results.getString("user"));
				int credits = results.getInt("credits");
				Date seen = results.getDate("seen");
				Date joined = results.getDate("joined");

				User user = new User(player, credits, joined, seen);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(String.format("Unable to load users from the table: %s", e.getMessage()));
		}

		return users;
	}

	public void saveRegionData(LoadedRegion loadedRegion) {
		String sql = String.format("INSERT OR REPLACE INTO chunks(%s) VALUES(?,?,?,?,?,?)", Schema.getChunkProperties());

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, loadedRegion.getId().toString());
			statement.setString(2, loadedRegion.getOwner().toString());
			statement.setString(3, loadedRegion.getWorld().getUniqueId().toString());
			statement.setInt(4, loadedRegion.getRegion().getFrom().getX());
			statement.setInt(5, loadedRegion.getRegion().getFrom().getZ());
			statement.setInt(6, loadedRegion.getRegion().getTo().getX());
			statement.setInt(7, loadedRegion.getRegion().getTo().getZ());
			statement.setDate(8, (Date) loadedRegion.getEpoch());
		} catch(SQLException e) {
			e.printStackTrace();
			logger.error(String.format("Error inserting LoadedRegion into the database: %s", e.getMessage()));
		}
	}

	public void saveRegionData(ImmutableSet<LoadedRegion> loadedRegions) {
		loadedRegions.forEach(this::saveRegionData);
	}

	public void saveRegionData(ArrayList<LoadedRegion> data) {
		data.forEach(this::saveRegionData);
	}

	public void saveUserData(User user) {
		String sql = String.format("INSERT OR REPLACE INTO users(%s) VALUES(?,?,?,?)", Schema.getUserProperties());

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, user.getUniqueId().toString());
			statement.setInt(2, user.getCredits());
			statement.setDate(3, user.getLastSeen());
			statement.setDate(4, user.getUserJoined());
			statement.execute();
		} catch(SQLException e) {
			e.printStackTrace();
			logger.error(String.format("Error inserting user into the database: %s", e.getMessage()));
		}
	}

	public void saveUserData(ImmutableSet<User> loadedRegions) {
		loadedRegions.forEach(this::saveUserData);
	}

	public void saveUserData(ArrayList<User> data) {
		data.forEach(this::saveUserData);
	}
}
