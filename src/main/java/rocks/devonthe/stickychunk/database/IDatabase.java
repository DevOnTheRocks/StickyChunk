package rocks.devonthe.stickychunk.database;

import com.google.common.collect.ImmutableSet;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;
import rocks.devonthe.stickychunk.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public interface IDatabase {
	void saveRegionData(LoadedRegion loadedRegion);

	void saveRegionData(ImmutableSet<LoadedRegion> loadedRegions);

	void saveRegionData(ArrayList<LoadedRegion> data);

	HashMap<UUID, ArrayList<LoadedRegion>> loadRegionData();

	void saveUserData(User user);

	void saveUserData(ImmutableSet<User> loadedUsers);

	void saveUserData(ArrayList<User> loadedUsers);

	ArrayList<User> loadUserData();
}
