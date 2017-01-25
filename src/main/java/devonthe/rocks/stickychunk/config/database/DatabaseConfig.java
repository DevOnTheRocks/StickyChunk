package devonthe.rocks.stickychunk.config.database;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by Cossacksman on 22/01/2017.
 */
@ConfigSerializable
public class DatabaseConfig {
	@Setting(value = "Type", comment = "The type of data storage to use. Supports [SQLite, MySQL]")
	public String type;
	@Setting(value = "SQLite")
	public SqliteConfig sqlite;
	@Setting(value = "MySQL")
	public MysqlConfig mysql;
	@Setting(value = "Mongo")
	public MongoConfig mongo;

	public DatabaseConfig() {
		type = "SQLite";
		sqlite = new SqliteConfig();
		mysql = new MysqlConfig();
		mongo = new MongoConfig();
	}
}
