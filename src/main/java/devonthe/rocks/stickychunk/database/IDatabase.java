package devonthe.rocks.stickychunk.database;

import devonthe.rocks.stickychunk.chunkload.LoadedRegion;

import java.util.ArrayList;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public interface IDatabase {
	void saveData(LoadedRegion loadedRegion);
	void saveData(ArrayList<LoadedRegion> data);
	ArrayList<LoadedRegion> loadData();
}
