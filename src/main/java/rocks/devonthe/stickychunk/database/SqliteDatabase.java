package rocks.devonthe.stickychunk.database;

import rocks.devonthe.stickychunk.StickyChunk;
import rocks.devonthe.stickychunk.config.database.SqliteConfig;
import org.slf4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class SqliteDatabase extends SqlDatabase {
	private SqliteConfig config;
	private String databaseLocation;
	private String databaseName;
	private Connection connection;

	private Logger logger;

	public SqliteDatabase() {
		config = StickyChunk.getInstance().getConfig().database.sqlite;
		databaseLocation = config.location;
		databaseName = config.databaseName;

		logger = StickyChunk.getInstance().getLogger();

		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s%s%s.db", databaseLocation, File.separator, databaseName));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.error("Unable to load the JDBC driver");
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Unable to connect to the database.");
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
		return connection;
	}
}
