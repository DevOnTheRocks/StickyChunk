package rocks.devonthe.stickychunk.database;

import com.google.common.collect.ImmutableSet;
import rocks.devonthe.stickychunk.chunkload.LoadedRegion;

import java.util.ArrayList;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public interface IDatabase {
	void saveRegionData(LoadedRegion loadedRegion);
	void saveRegionData(ImmutableSet<LoadedRegion> loadedRegions);
	void saveRegionData(ArrayList<LoadedRegion> data);
	ArrayList<LoadedRegion> loadRegionData();
}
