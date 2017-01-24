package devonthe.rocks.stickychunk.database;

import com.google.common.collect.ImmutableSet;
import devonthe.rocks.stickychunk.chunkload.LoadedRegion;

import java.util.ArrayList;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public interface IDatabase {
	void saveData(LoadedRegion loadedRegion);
	void saveData(ImmutableSet<LoadedRegion> loadedRegions);
	void saveData(ArrayList<LoadedRegion> data);
	ArrayList<LoadedRegion> loadData();
}
