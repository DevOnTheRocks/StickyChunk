package rocks.devonthe.stickychunk.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import rocks.devonthe.stickychunk.config.database.DatabaseConfig;

/**
 * Created by Cossacksman on 02/01/2017.
 */

@ConfigSerializable
public class StickyChunkConfig {
	@Setting(value = "SqlDatabase")
	public DatabaseConfig database;
//	@Setting(value = "Misc")
//	public MiscConfig misc;

	public StickyChunkConfig() {
		database = new DatabaseConfig();
//		misc = new MiscConfig();
	}
}