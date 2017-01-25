package devonthe.rocks.stickychunk.config.database;

import ninja.leaping.configurate.objectmapping.Setting;

/**
 * Created by Cossacksman on 25/01/2017.
 */
public class MongoConfig {
	@Setting("Database")
	public String databaseName;
	@Setting("Host")
	public String host;
	@Setting("Port")
	public int port;

	public MongoConfig() {
		databaseName = "stickychunk";
		host = "localhost";
		port = 27017;
	}
}
