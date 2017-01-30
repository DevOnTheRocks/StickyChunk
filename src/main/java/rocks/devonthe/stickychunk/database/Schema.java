package rocks.devonthe.stickychunk.database;

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

	static final String USERSCHEMA =	"user		STRING PRIMARY KEY," +
										"credits	INT," +
										"seen		DATETIME," +
										"joined		DATETIME";

	static final String USERPROPS = "user, credits, seen, joined";

	static String createChunkSchema() {
		return String.format("CREATE TABLE IF NOT EXISTS chunks (%s)", CHUNKSCHEMA);
	}

	static String getChunkProperties() {
		return CHUNKPROPS;
	}

	static String createUserSchema() {
		return String.format("CREATE TABLE IF NOT EXISTS users (%s)", USERSCHEMA);
	}

	static String getUserProperties() {
		return USERPROPS;
	}
}
