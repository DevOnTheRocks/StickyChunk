package rocks.devonthe.stickychunk.database;

import com.google.common.collect.ImmutableSet;
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
import rocks.devonthe.stickychunk.world.Coordinate;
import rocks.devonthe.stickychunk.world.Region;
import org.bson.Document;
import org.slf4j.Logger;
import org.spongepowered.api.Server;

import java.sql.Date;
import java.util.ArrayList;
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

	public ArrayList<LoadedRegion> loadRegionData() {
		ArrayList<LoadedRegion> chunks = new ArrayList<LoadedRegion>();

		MongoCollection<Document> collection = getDatabase().getCollection("chunks");
		collection.find().forEach((Block<Document>) document -> {
			UUID id = UUID.fromString(document.getString("_id"));
			UUID owner = UUID.fromString(document.getString("owner"));
			UUID world = UUID.fromString(document.getString("world"));
			int fromX  = document.getInteger("fromX");
			int fromZ = document.getInteger("fromZ");
			int toX = document.getInteger("toX");
			int toZ = document.getInteger("toZ");
			Date date = (Date) document.getDate("created");

			if (server.getWorld(world).isPresent()) {
				server.getWorld(world).ifPresent(loadedWorld -> {
					chunks.add(new LoadedRegion(owner, id, new Region(new Coordinate(fromX, fromZ), new Coordinate(toX, toZ), loadedWorld), date));
				});
			}
		});

		return chunks;
	}

	public void saveRegionData(LoadedRegion loadedRegion) {
		MongoCollection<Document> collection = getDatabase().getCollection("chunks");

		Document regionDocument = new Document("_id", loadedRegion.getId().toString())
				.append("owner", loadedRegion.getOwner().toString())
				.append("world", loadedRegion.getWorld().getUniqueId().toString())
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

	public void saveRegionData(ImmutableSet<LoadedRegion> loadedRegions) {
		loadedRegions.forEach(this::saveRegionData);
	}

	public void saveRegionData(ArrayList<LoadedRegion> loadedRegions) {
		loadedRegions.forEach(this::saveRegionData);
	}
}
