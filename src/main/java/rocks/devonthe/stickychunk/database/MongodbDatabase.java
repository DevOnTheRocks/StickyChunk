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
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.spongepowered.api.Server;
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.config.database.MongoConfig;
import rocks.devonthe.stickychunk.data.UserData;
import rocks.devonthe.stickychunk.world.Coordinate;
import rocks.devonthe.stickychunk.world.Region;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MongodbDatabase implements IDatabase {
	private MongoConfig config;
	private String databaseName;
	private String host;
	private int port;

	MongoDatabase database;

	private Server server = StickyChunk.getInstance().getGame().getServer();
	private Logger logger = StickyChunk.getInstance().getLogger();

	public MongodbDatabase() {
		config = StickyChunk.getInstance().getConfig().database.mongo;
		databaseName = config.databaseName;
		host = config.host;
		port = config.port;

		MongoClientURI connectionString = new MongoClientURI(String.format("mongodb://%s:%s", host, port));
		MongoClient client = new MongoClient(connectionString);
		database = client.getDatabase(databaseName);
	}

	private MongoDatabase getDatabase() {
		return database;
	}

	public HashMap<UUID, ArrayList<LoadedRegion>> loadRegionData() {
		HashMap<UUID, ArrayList<LoadedRegion>> regions = new HashMap<>();

		MongoCollection<Document> collection = getDatabase().getCollection("chunks");
		collection.find().forEach((Block<Document>) document -> {
			UUID id = UUID.fromString(document.getString("_id"));
			UUID owner = UUID.fromString(document.getString("owner"));
			UUID world = UUID.fromString(document.getString("world"));
			LoadedRegion.ChunkType type = LoadedRegion.ChunkType.valueOf(document.getString("type"));
			int fromX = document.getInteger("fromX");
			int fromZ = document.getInteger("fromZ");
			int toX = document.getInteger("toX");
			int toZ = document.getInteger("toZ");
			Date date = (Date) document.getDate("created");

			Region region = new Region(new Coordinate(fromX, fromZ), new Coordinate(toX, toZ), world);

			if (regions.containsKey(id))
				regions.get(id).add(new LoadedRegion(owner, id, region, date, type));
			else
				regions.put(id, Lists.newArrayList(new LoadedRegion(owner, id, region, date, type)));

		});

		return regions;
	}

	public ArrayList<UserData> loadUserData() {
		ArrayList<UserData> userDatas = new ArrayList<UserData>();

		MongoCollection<Document> collection = getDatabase().getCollection("user");
		collection.find().forEach((Block<Document>) document -> {
			UUID player = UUID.fromString(document.getString("user"));
			Date seen = (Date) document.getDate("seen");
			Date joined = (Date) document.getDate("joined");

			UserData userData = new UserData(player, joined, seen);
			userDatas.add(userData);
		});

		return userDatas;
	}

	public void saveRegionData(LoadedRegion loadedRegion) {
		MongoCollection<Document> collection = getDatabase().getCollection("chunks");

		Document regionDocument = new Document("_id", loadedRegion.getUniqueId().toString())
			.append("owner", loadedRegion.getOwner().toString())
			.append("world", loadedRegion.getWorld().getUniqueId().toString())
			.append("type", loadedRegion.getType().toString())
			.append("fromX", loadedRegion.getRegion().getFrom().getX())
			.append("fromZ", loadedRegion.getRegion().getFrom().getZ())
			.append("toX", loadedRegion.getRegion().getTo().getX())
			.append("toZ", loadedRegion.getRegion().getTo().getZ())
			.append("created", loadedRegion.getEpoch());

		collection.replaceOne(
			Filters.eq("_id", loadedRegion.getUniqueId().toString()),
			regionDocument,
			(new UpdateOptions()).upsert(true)
		);
	}

	public void saveUserData(UserData userData) {
		MongoCollection<Document> collection = getDatabase().getCollection("users");

		Document userDocument = new Document("_id", userData.getUser())
			.append("seen", userData.getLastSeen())
			.append("joined", userData.getUserJoined());

		collection.replaceOne(
			Filters.eq("_id", userData.getUser().toString()),
			userDocument,
			(new UpdateOptions()).upsert(true)
		);
	}

	public void saveRegionData(ImmutableSet<LoadedRegion> loadedRegions) {
		loadedRegions.forEach(this::saveRegionData);
	}

	public void saveRegionData(ArrayList<LoadedRegion> loadedRegions) {
		loadedRegions.forEach(this::saveRegionData);
	}

	public void deleteRegionData(LoadedRegion region) {
		MongoCollection<Document> collection = getDatabase().getCollection("chunks");

		Bson condition = new Document("_id", region.getUniqueId().toString());
		collection.deleteOne(condition);
	}

	public void deleteRegionData(ArrayList<LoadedRegion> regions) {
		regions.forEach(this::deleteRegionData);
	}

	public void saveUserData(ImmutableSet<UserData> loadedUserDatas) {
		loadedUserDatas.forEach(this::saveUserData);
	}

	public void saveUserData(ArrayList<UserData> loadedUserDatas) {
		loadedUserDatas.forEach(this::saveUserData);
	}

	public void deleteUserData(UserData userData) {
		MongoCollection<Document> collection = getDatabase().getCollection("users");

		Bson condition = new Document("_id", userData.getUser().toString());
		collection.deleteOne(condition);
	}

	public void deleteUserData(ArrayList<UserData> userDatas) {
		userDatas.forEach(this::deleteUserData);
	}
}
