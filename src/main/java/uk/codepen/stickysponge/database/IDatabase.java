package uk.codepen.stickysponge.database;

import uk.codepen.stickysponge.Chunk;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public interface IDatabase {
	public void saveData(Chunk chunk);
	public void saveAllData(ArrayList<Chunk> data);
	public ArrayList<Chunk> loadData();
}
