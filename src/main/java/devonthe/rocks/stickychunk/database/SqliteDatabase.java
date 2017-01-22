package devonthe.rocks.stickychunk.database;

import devonthe.rocks.stickychunk.StickyChunk;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class SqliteDatabase extends Database {
	// TODO:- Add config fields (location, dbName, tableName)

	private Logger logger = StickyChunk.getInstance().getLogger();

	public SqliteDatabase() {

		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			logger.error("Unable to load the JDBC driver");
		}

		try (Statement statement = getConnection().createStatement()) {
			statement.setQueryTimeout(30);
			statement.executeUpdate(Schema.createChunkSchema());
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Unable to create StickyChunk database");
		}
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(String.format("jdbc:sqlite:%s%s%s.db"));
	}
}
