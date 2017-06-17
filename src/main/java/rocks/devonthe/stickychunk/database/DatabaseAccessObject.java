package rocks.devonthe.stickychunk.database;

import com.j256.ormlite.support.ConnectionSource;
import org.slf4j.Logger;
import rocks.devonthe.stickychunk.StickyChunk;

public abstract class DatabaseAccessObject {
	protected ConnectionSource connectionSource;
	protected Logger logger;

	public DatabaseAccessObject() {
		logger = StickyChunk.getInstance().getLogger();
	}

	protected ConnectionSource getConnectionSource() {
		return connectionSource;
	}
}
