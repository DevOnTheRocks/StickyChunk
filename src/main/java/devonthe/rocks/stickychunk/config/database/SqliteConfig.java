package devonthe.rocks.stickychunk.config.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by Cossacksman on 22/01/2017.
 */
@ConfigSerializable
public class SqliteConfig {
	@Setting("Name")
	public String databaseName;
	@Setting("Location")
	public String location;

	public SqliteConfig() {
		databaseName = "stickychunk";
		location = "./";
	}
}
