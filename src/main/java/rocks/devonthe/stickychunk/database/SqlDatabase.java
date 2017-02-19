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

package rocks.devonthe.stickychunk.database;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.data.UserData;
import rocks.devonthe.stickychunk.world.Coordinate;
import rocks.devonthe.stickychunk.world.Region;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class SqlDatabase implements IDatabase {
	private Server server = StickyChunk.getInstance().getGame().getServer();
	private Logger logger = StickyChunk.getInstance().getLogger();

	public abstract Connection getConnection() throws SQLException;

	public HashMap<UUID, ArrayList<LoadedRegion>> loadRegionData() {
		HashMap<UUID, ArrayList<LoadedRegion>> regions = new HashMap<>();

		try (Statement statement = getConnection().createStatement()) {
			ResultSet results = statement.executeQuery("SELECT * FROM chunks");

			while (results.next()) {
				UUID id = UUID.fromString(results.getString("id"));
				UUID owner = UUID.fromString(results.getString("owner"));
				UUID world = UUID.fromString(results.getString("world"));
				LoadedRegion.ChunkType type = LoadedRegion.ChunkType.valueOf(results.getString("type").toUpperCase());
				int fromX = results.getInt("fromX");
				int fromZ = results.getInt("fromZ");
				int toX = results.getInt("toX");
				int toZ = results.getInt("toZ");
				Date date = results.getDate("created");

				Region region = new Region(new Coordinate(fromX, fromZ), new Coordinate(toX, toZ), world);

				if (regions.containsKey(id))
					regions.get(id).add(new LoadedRegion(owner, id, region, date, type));
				else
					regions.put(id, Lists.newArrayList(new LoadedRegion(owner, id, region, date, type)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(String.format("Unable to load loadedRegion from the table: %s", e.getMessage()));
		}

		return regions;
	}

	public ArrayList<UserData> loadUserData() {
		ArrayList<UserData> userDatas = new ArrayList<UserData>();

		try (Statement statement = getConnection().createStatement()) {
			ResultSet results = statement.executeQuery("SELECT * FROM users");

			while (results.next()) {
				UUID player = UUID.fromString(results.getString("user"));
				Date seen = results.getDate("seen");
				Date joined = results.getDate("joined");

				UserData userData = new UserData(player, joined, seen);
				userDatas.add(userData);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(String.format("Unable to load user from the table: %s", e.getMessage()));
		}

		return userDatas;
	}

	public void saveRegionData(LoadedRegion loadedRegion) {
		String sql = String.format(
			"INSERT OR REPLACE INTO chunks(%s) VALUES(?,?,?,?,?,?,?,?,?)", Schema.getChunkProperties());

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, loadedRegion.getUniqueId().toString());
			statement.setString(2, loadedRegion.getOwner().toString());
			statement.setString(3, loadedRegion.getRegion().getWorldId().toString());
			statement.setString(4, loadedRegion.getType().toString());
			statement.setInt(5, loadedRegion.getRegion().getFrom().getX());
			statement.setInt(6, loadedRegion.getRegion().getFrom().getZ());
			statement.setInt(7, loadedRegion.getRegion().getTo().getX());
			statement.setInt(8, loadedRegion.getRegion().getTo().getZ());
			statement.setDate(9, new Date(loadedRegion.getEpoch().getTime()));
			statement.execute();
		} catch (SQLException e) {
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

	public void saveUserData(UserData userData) {
		String sql = String.format("INSERT OR REPLACE INTO users(%s) VALUES(?,?,?)", Schema.getUserProperties());

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, userData.getUser().toString());
			statement.setDate(2, userData.getLastSeen());
			statement.setDate(3, userData.getUserJoined());
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(String.format("Error inserting user into the database: %s", e.getMessage()));
		}
	}

	public void saveUserData(ImmutableSet<UserData> loadedRegions) {
		loadedRegions.forEach(this::saveUserData);
	}

	public void saveUserData(ArrayList<UserData> data) {
		data.forEach(this::saveUserData);
	}

	public void deleteRegionData(LoadedRegion region) {
		String sql = "DELETE FROM chunks WHERE id = ?";

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, region.getUniqueId().toString());
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(String.format("Unable to delete record from the chunks table: %s", e.getMessage()));
		}
	}

	public void deleteRegionData(ArrayList<LoadedRegion> regions) {
		regions.forEach(this::deleteRegionData);
	}

	public void deleteUserData(UserData userData) {
		String sql = "DELETE FROM users WHERE id = ?";

		try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, userData.getUser().toString());
			statement.execute();
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error(String.format("Unable to delete record from the users table: %s", e.getMessage()));
		}
	}

	public void deleteUserData(ArrayList<UserData> userDatas) {
		userDatas.forEach(this::deleteUserData);
	}
}
