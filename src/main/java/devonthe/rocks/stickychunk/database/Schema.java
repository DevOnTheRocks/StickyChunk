package devonthe.rocks.stickychunk.database;

/**
 * Created by Cossacksman on 21/01/2017.
 */
public interface Schema {
	static final String CHUNKSCHEMA = 	"id			STRING PRIMARY KEY," +
										"owner		STRING," +
										"world 		STRING," +
										"fromX		INT," +
										"fromZ		INT," +
										"toX		INT," +
										"toZ		INT," +
										"created	DATETIME";

	static final String CHUNKPROPS = "id, owner, world, fromX, fromZ, toX, toZ, created";

	static String createChunkSchema() {
		return String.format("CREATE TABLE IF NOT EXISTS chunks (%s)", CHUNKSCHEMA);
	}

	static String getChunkProperties() {
		return CHUNKPROPS;
	}
}
