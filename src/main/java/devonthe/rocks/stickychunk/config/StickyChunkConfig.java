package devonthe.rocks.stickychunk.config;

import devonthe.rocks.stickychunk.config.database.DatabaseConfig;
import devonthe.rocks.stickychunk.config.token.TokenConfig;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

/**
 * Created by Cossacksman on 02/01/2017.
 */

@ConfigSerializable
public class StickyChunkConfig {
	@Setting(value = "Database")
	public DatabaseConfig database;
	@Setting(value = "Token")
	public TokenConfig token;
	@Setting(value = "Misc")
	public MiscConfig misc;

	public StickyChunkConfig() {
		database = new DatabaseConfig();
		token = new TokenConfig();
		misc = new MiscConfig();
	}
}