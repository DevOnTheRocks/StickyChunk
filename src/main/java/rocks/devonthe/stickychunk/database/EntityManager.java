package rocks.devonthe.stickychunk.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public abstract class EntityManager {
	private SessionFactory sessionFactory;
	private Configuration configuration;

	private UserEntityManager userEntityManager;
	private ChunkLoaderEntityManager chunkLoaderEntityManager;
	private ChunkEntityManager chunkEntityManager;

	public EntityManager() {
		configuration = new Configuration()
			.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC")
			.setProperty("hibernate.connection.url", String.format("jdbc:sqlite:%s%s%s.db"))
			.setProperty("hibernate.connection.username", "")
			.setProperty("hibernate.connection.password", "");

		sessionFactory = configuration.buildSessionFactory();
		userEntityManager = new UserEntityManager();
	}

	public UserEntityManager getUserEntityManager() {
		return userEntityManager;
	}

	public ChunkLoaderEntityManager getChunkLoaderEntityManager() {
		return chunkLoaderEntityManager;
	}

	public ChunkEntityManager getChunkEntityManager() {
		return chunkEntityManager;
	}

	protected Session getSession() {
		return sessionFactory.openSession();
	}
}
