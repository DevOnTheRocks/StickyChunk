package devonthe.rocks.stickychunk.database;

import devonthe.rocks.stickychunk.chunkload.LoadedRegion;

import java.util.ArrayList;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public interface IDatabase {
	public void saveData(LoadedRegion loadedRegion);
	public void saveAllData(ArrayList<LoadedRegion> data);
	public ArrayList<LoadedRegion> loadData();
}
