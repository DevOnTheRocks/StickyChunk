package devonthe.rocks.stickychunk.world;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;

/**
 * Created by Cossacksman on 02/01/2017.
 */
public class Coordinate {
	private Vector2i point;

	public Coordinate(int x, int z) {
		this.point = new Vector2i(x, z);
	}

	public Coordinate(Vector2i point) {
		this.point = point;
	}

	public Coordinate(Vector3i point) {
		this.point = new Vector2i(point.getX(), point.getZ());
	}

	public int getX() {
		return point.getX();
	}

	public int getZ() {
		return point.getY();
	}
}
