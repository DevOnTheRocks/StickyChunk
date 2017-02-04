package rocks.devonthe.stickychunk.world;

import com.flowpowered.math.vector.Vector3i;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class Coordinate {
	private int x;
	private int z;

	public Coordinate(int x, int z) {
		this.x = x;
		this.z = z;
	}

	public Coordinate(Vector3i point) {
		this.x = point.getX();
		this.z = point.getZ();
	}

	/***
	 * Returns the x value of the coordinate x,z pair.
	 * @return the x value of the coordinate
	 */
	public int getX() {
		return x;
	}

	/***
	 * Returns the z value of the coordinate x,z pair.
	 * @return the x value of the coordinate
	 */
	public int getZ() {
		return z;
	}
}
