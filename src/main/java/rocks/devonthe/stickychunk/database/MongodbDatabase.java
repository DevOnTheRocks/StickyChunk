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
import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.config.database.MongoConfig;
import rocks.devonthe.stickychunk.data.User;
import rocks.devonthe.stickychunk.world.Coordinate;
import rocks.devonthe.stickychunk.world.Region;
import org.bson.Document;
import org.slf4j.Logger;
import org.spongepowered.api.Server;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Cossacksman on 25/01/2017.
 */
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
			int fromX  = document.getInteger("fromX");
			int fromZ = document.getInteger("fromZ");
			int toX = document.getInteger("toX");
			int toZ = document.getInteger("toZ");
			Date date = (Date) document.getDate("created");

			if (server.getWorld(world).isPresent()) {
				server.getWorld(world).ifPresent(loadedWorld -> {
					Region region = new Region(new Coordinate(fromX, fromZ), new Coordinate(toX, toZ), loadedWorld);

					if (regions.containsKey(id))
						regions.get(id).add(new LoadedRegion(owner, id, region, date, type));
					else
						regions.put(id, Lists.newArrayList(new LoadedRegion(owner, id, region, date, type)));
				});
			} else {
				logger.error("The world that a chunk was associated to no longer exists.");
				logger.debug(String.format("The world's unique ID is %s", world.toString()));
			}
		});

		return regions;
	}

	public ArrayList<User> loadUserData() {
		ArrayList<User> users = new ArrayList<User>();

		MongoCollection<Document> collection = getDatabase().getCollection("users");
		collection.find().forEach((Block<Document>) document -> {
			UUID player = UUID.fromString(document.getString("user"));
			double balance = document.getDouble("personalCredits");
			Date seen = (Date) document.getDate("seen");
			Date joined = (Date) document.getDate("joined");

			User user = new User(player, balance, joined, seen);
			users.add(user);
		});

		return users;
	}

	public void saveRegionData(LoadedRegion loadedRegion) {
		MongoCollection<Document> collection = getDatabase().getCollection("chunks");

		Document regionDocument = new Document("_id", loadedRegion.getId().toString())
				.append("owner", loadedRegion.getOwner().toString())
				.append("world", loadedRegion.getWorld().getUniqueId().toString())
				.append("type", loadedRegion.getType().toString())
				.append("fromX", loadedRegion.getRegion().getFrom().getX())
				.append("fromZ", loadedRegion.getRegion().getFrom().getZ())
				.append("toX", loadedRegion.getRegion().getTo().getX())
				.append("toZ", loadedRegion.getRegion().getTo().getZ())
				.append("created", loadedRegion.getEpoch());

		collection.replaceOne(
			Filters.eq("_id", loadedRegion.getId().toString()),
			regionDocument,
			(new UpdateOptions()).upsert(true)
		);
	}

	public void saveUserData(User user) {
		MongoCollection<Document> collection = getDatabase().getCollection("users");

		Document userDocument = new Document("_id", user.getUniqueId())
				.append("balance", user.getBalance())
				.append("seen", user.getLastSeen())
				.append("joined", user.getUserJoined());

		collection.replaceOne(
				Filters.eq("_id", user.getUniqueId().toString()),
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

	public void saveUserData(ImmutableSet<User> loadedUsers) {
		loadedUsers.forEach(this::saveUserData);
	}

	public void saveUserData(ArrayList<User> loadedUsers) {
		loadedUsers.forEach(this::saveUserData);
	}
}
