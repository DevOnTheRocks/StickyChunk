package rocks.devonthe.stickychunk.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Properties;

public abstract class EntityManager {
	private SessionFactory sessionFactory;
	private Configuration configuration;

	public EntityManager() {
		configuration = new Configuration()
			.setProperty("hibernate.connection.driver_class", "org.sqlite.JDBC")
			.setProperty("hibernate.connection.url", String.format("jdbc:sqlite:%s%s%s.db"))
			.setProperty("hibernate.connection.username", "")
			.setProperty("hibernate.connection.password", "");

		sessionFactory = configuration.buildSessionFactory();
	}
}
