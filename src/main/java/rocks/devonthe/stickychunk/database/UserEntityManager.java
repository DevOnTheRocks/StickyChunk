package rocks.devonthe.stickychunk.database;

import com.google.common.collect.ImmutableSet;
import org.hibernate.Session;
import rocks.devonthe.stickychunk.data.UserData;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class UserEntityManager extends EntityManager {
	public ArrayList<UserData> load() {
		Session session = getSession();
		return null;
	}

	public Optional<UserData> load(UUID uuid) {
		Session session = getSession();
		return Optional.empty();
	}

	public void save(ImmutableSet<UserData> userData) {
		Session session = getSession();
	}

	public void save(UserData userData) {
		Session session = getSession();
	}

	public void update(ArrayList<UserData> userDatas) {
		Session session = getSession();
	}

	public void update(UserData userData) {
		Session session = getSession();
	}
}
