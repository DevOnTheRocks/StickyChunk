package rocks.devonthe.stickychunk.config.chunkloader;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class CommandChunkLoaderConfig extends ChunkLoaderConfig {
	@Setting(value = "Allow-Regions", comment = "Allow loading of multi-chunk regions.")
	private boolean allowRegion = true;

	public CommandChunkLoaderConfig(String name, String duration, boolean afk, boolean allowRegion) {
		super(name, ChunkLoaderType.COMMAND, duration, afk);
		this.allowRegion = allowRegion;
	}

	public boolean isAllowRegion() {
		return allowRegion;
	}
}
