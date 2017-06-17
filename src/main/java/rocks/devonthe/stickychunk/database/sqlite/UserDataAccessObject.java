package rocks.devonthe.stickychunk.database.sqlite;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import rocks.devonthe.stickychunk.data.UserData;
import rocks.devonthe.stickychunk.database.DatabaseAccessObject;
import rocks.devonthe.stickychunk.database.IStorable;

import java.sql.SQLException;
import java.util.UUID;

public class UserDataAccessObject extends DatabaseAccessObject implements IStorable {
	private Dao<UserData, UUID> userDataDAO;

	public UserDataAccessObject() {
		try {
			userDataDAO = DaoManager.createDao(getConnectionSource(), UserData.class);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Unable to create database access object for UserData.");
		}
	}

	public void save(UserData userData) {
		try {
			userDataDAO.createOrUpdate(userData);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Error inserting UserData into the database, check the console for more.");
		}
	}

	public void load(UUID uuid) {
		try {
			UserData userData = userDataDAO.queryForId(uuid);
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("Error retrieving UserData from the database, check the console for more.");
		}
	}
}
