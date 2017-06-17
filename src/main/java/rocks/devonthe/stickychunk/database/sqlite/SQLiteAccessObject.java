package rocks.devonthe.stickychunk.database.sqlite;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import rocks.devonthe.stickychunk.database.DatabaseAccessObject;

import java.sql.SQLException;

public abstract class SQLiteAccessObject extends DatabaseAccessObject {

	private UserDataAccessObject userDataAccessObject;

	public SQLiteAccessObject(String connectionString) {
		try {
			connectionSource = new JdbcConnectionSource(connectionString);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Error connecting to the SQLite Database, check the console for more.");
		}
	}
}
