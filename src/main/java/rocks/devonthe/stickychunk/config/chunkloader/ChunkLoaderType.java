package rocks.devonthe.stickychunk.config.chunkloader;

public enum ChunkLoaderType {
	COMMAND;

	public static ChunkLoaderType parse(String type) {
		if (COMMAND.toString().equalsIgnoreCase(type)) return COMMAND;

		throw new IllegalArgumentException(String.format("Invalid ChunkLoaderType (%s) found!", type));
	}
}
